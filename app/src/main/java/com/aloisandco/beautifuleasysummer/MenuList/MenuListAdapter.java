package com.aloisandco.beautifuleasysummer.MenuList;

import android.app.Activity;
import android.content.res.TypedArray;
import android.text.Html;
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
import com.aloisandco.beautifuleasysummer.utils.FavoriteManager;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.HtmlTagHandler;

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

    // create a new ImageView for each item referenced by the Adapter
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

        String string =  itemArray.getString(0);
        itemArray.recycle();

        if (convertView == null || ((ViewHolder)convertView.getTag()).needInflate) {  // if it's not recycled, initialize some attributes
            linearLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.activity_menu_list_item, parent, false);
            setViewHolder(linearLayout);
        } else {
            linearLayout = (LinearLayout) convertView;
        }

        vh = (ViewHolder)linearLayout.getTag();
        vh.text.setText(Html.fromHtml(string, null, new HtmlTagHandler()));
        vh.checkBox.setOnCheckedChangeListener(null);
        vh.checkBox.setChecked(FavoriteManager.isArticleFavorite(articleId, mContext));
        vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FavoriteManager.addArticleToFavorite(articleId, mContext);
                } else {
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
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };

        collapse(v, al);
    }

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

    private class ViewHolder {
        public boolean needInflate;
        public TextView text;
        public CheckBox checkBox;
    }

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
