package com.aloisandco.beautifuleasysummer.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.Utils.Constants;
import com.aloisandco.beautifuleasysummer.Utils.Manager.AnalyticsManager;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FavoriteManager;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FontManager;
import com.aloisandco.beautifuleasysummer.Utils.HTML.HtmlTagHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by quentinmetzler on 22/03/15.
 */
public class MenuListAdapter extends BaseAdapter {
    private Activity mContext;
    private Boolean mIsFavorite;
    private TypedArray mMenuListArray;
    private static final int ANIM_DURATION = 200;

    public MenuListAdapter(Activity context, int resourceId) {
        mContext = context;
        if (resourceId == 0) {
            mIsFavorite = true;
        } else {
            mMenuListArray = mContext.getResources().obtainTypedArray(resourceId);
            mIsFavorite = false;
        }
    }

    @Override
    public int getCount() {
        if (mIsFavorite) {
            return FavoriteManager.getNumberOfFavoriteArticle(mContext);
        } else {
            return mMenuListArray.length();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Create if necessary and initialize the row with the content of the article list array
     * @param position the position of the item to initialize
     * @param convertView the view which is recycled
     * @param parent the listView it is for
     * @return
     */
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final LinearLayout linearLayout;
        ViewHolder vh;
        final int articleId;
        if (mIsFavorite) {
            articleId = FavoriteManager.getArticleAtPosition(position, mContext);
        } else {
            articleId = mMenuListArray.getResourceId(position, 0);
        }
        TypedArray itemArray = mContext.getResources().obtainTypedArray(articleId);

        final String string =  itemArray.getString(0);
        itemArray.recycle();

        if (convertView == null || ((ViewHolder)convertView.getTag()).needInflate) {  // if it's not recycled, initialize some attributes
            linearLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.activity_menu_list_item, parent, false);
            setViewHolder(linearLayout);
        } else {
            linearLayout = (LinearLayout) convertView;
        }

        vh = (ViewHolder)linearLayout.getTag();
        vh.text.setText(Html.fromHtml(string, null, new HtmlTagHandler()));
        // Checkbox will remove and add favorites
        vh.checkBox.setOnCheckedChangeListener(null);
        vh.checkBox.setChecked(FavoriteManager.isArticleFavorite(articleId, mContext));
        vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Tracker tracker = AnalyticsManager.getTracker(AnalyticsManager.TrackerName.APP_TRACKER, mContext);
                    tracker.send(new HitBuilders.AppViewBuilder()
                            .setCustomDimension(2, string)
                            .build());
                    FavoriteManager.addArticleToFavorite(articleId, mContext);
                } else {
                    Tracker tracker = AnalyticsManager.getTracker(AnalyticsManager.TrackerName.APP_TRACKER, mContext);
                    tracker.send(new HitBuilders.AppViewBuilder()
                            .setCustomDimension(3, string)
                            .build());
                    if (mIsFavorite) {
                        deleteCell(linearLayout, articleId);
                    } else {
                        FavoriteManager.removeArticleFromFavorite(articleId, mContext);
                    }
                }
            }
        });

        return linearLayout;
    }

    /**
     * Remove a cell with an animation
     * @param v the cell to remove
     * @param articleId the article id to remove from the favorite
     */
    public void deleteCell(final View v, final int articleId) {
        v.setHasTransientState(true);
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                if (articleId != 0) {
                    FavoriteManager.removeArticleFromFavorite(articleId, mContext);
                }

                ViewHolder vh = (ViewHolder)v.getTag();
                vh.needInflate = true;

                MenuListAdapter.this.notifyDataSetChanged();
                v.setHasTransientState(false);
                if (FavoriteManager.getNumberOfFavoriteArticle(mContext) == 0) {
                    sendMessage();
                }
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };

        collapse(v, al);
    }

    // Send an Intent with an action named "custom-event-name". The Intent sent should
    // be received by the ReceiverActivity.
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.NO_MORE_FAVORITES);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Animate the collapsing of a view
     * @param v the view to collapse
     * @param al the animation listener to call when it's done
     */
    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIM_DURATION);
        v.startAnimation(anim);
    }

    /**
     * the cell data model
     */
    private class ViewHolder {
        public boolean needInflate;
        public TextView text;
        public CheckBox checkBox;
    }

    /**
     * initialize a cell data model from a view
     * @param view the view containing the graphical elements of a cell
     */
    private void setViewHolder(View view) {
        ViewHolder vh = new ViewHolder();
        FontManager fontManager = FontManager.getInstance(mContext.getAssets());
        vh.text = (TextView)view.findViewById(R.id.textView);
        vh.text.setTypeface(fontManager.ralewayLightFont);
        vh.checkBox = (CheckBox) view.findViewById(R.id.favorite);
        vh.needInflate = false;
        view.setTag(vh);
    }
}
