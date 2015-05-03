package com.aloisandco.beautifuleasysummer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.Enum.AnimType;
import com.aloisandco.beautifuleasysummer.View.AnimatedView;
import com.aloisandco.beautifuleasysummer.Adapter.MenuListAdapter;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.Utils.Manager.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.Utils.Constants;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FavoriteManager;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FontManager;
import com.aloisandco.beautifuleasysummer.Utils.UI.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by quentinmetzler on 18/03/15.
 */
public class MenuListActivity extends AnimatedActivity {
    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private ListView mListView;
    private MenuListAdapter mAdapter;
    private View mLastClickedView;
    private int mLastClickedResourceId;
    private int mLastClickedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_menu_list, 2);

        mIconImageView = (ImageView) findViewById(R.id.icon);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mListView = (ListView) findViewById(R.id.listView);

        initFont();

        initTitleAndIcon();
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListView.setEnabled(true);
        if (mLastClickedView != null) {
            // An article was removed of the favorite in the next activity and we are in the
            // Favorite category
            if (!FavoriteManager.isArticleFavorite(mLastClickedResourceId, this)) {
                // We had it back to the model so we can animate its deletion
                FavoriteManager.addArticleToFavoriteAtPosition(mLastClickedResourceId, mLastClickedIndex, this);
                mAdapter.deleteCell(mLastClickedView, mLastClickedResourceId);
            }
            mLastClickedView = null;
        } else {
            if (mAdapter != null) {
                // Refresh the state of the suns
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Init the title with our custom font
     */
    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayMediumFont);
        TextView addFavoriteTextView = (TextView) findViewById(R.id.add_favorite);
        addFavoriteTextView.setTypeface(fontManager.ralewayMediumFont);
    }

    /**
     * Set the image and text of our title and icon associated
     */
    private void initTitleAndIcon() {
        Bundle bundle = getIntent().getExtras();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), bundle.getInt(Constants.PACKAGE_NAME + ".iconId"));
        String title = bundle.getString(Constants.PACKAGE_NAME + ".text");

        mIconImageView.setImageBitmap(icon);
        mTitleTextView.setText(title);
    }

    /**
     * Replace the listView by a text helping to add content
     */
    private void replaceListViewByPlaceholderText() {
        mListView.setVisibility(View.GONE);
        TextView addFavoriteTextView = (TextView) findViewById(R.id.add_favorite);
        addFavoriteTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Init listView listener so we can detect when a row is touch so we can go to the next activity
     */
    private void initListView() {
        Bundle bundle = getIntent().getExtras();
        final int menuListArrayResourceId = bundle.getInt(Constants.PACKAGE_NAME + ".menuListArray");

        if (menuListArrayResourceId == 0 && FavoriteManager.getNumberOfFavoriteArticle(this) == 0) {
            // if no content and we are in the favorite category, display placeholder
            replaceListViewByPlaceholderText();
        } else {
            if (menuListArrayResourceId == 0) {
                // if we are in the favorite category, we register to get notified when the last
                // favorite is removed
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                        new IntentFilter(Constants.NO_MORE_FAVORITES));
            }
            mAdapter = new MenuListAdapter(this, menuListArrayResourceId);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mListView.setEnabled(false);

                    ActivityTransitionManager.getInstance().setMenuListItemView(view);

                    int resourceId;
                    if (menuListArrayResourceId == 0) {
                        // get the article id of the favorite item at the index touched
                        resourceId = FavoriteManager.getArticleAtPosition(position, MenuListActivity.this);
                        // Register the view of the item touch so we can animate it if it gets
                        // removed from the favorite
                        mLastClickedView = view;
                        mLastClickedResourceId = resourceId;
                        mLastClickedIndex = position;
                    } else {
                        // get the article id from the category array
                        TypedArray menuListArray = getResources().obtainTypedArray(menuListArrayResourceId);
                        resourceId = menuListArray.getResourceId(position, 0);
                        menuListArray.recycle();
                    }

                    TextView textView = (TextView) view.findViewById(R.id.textView);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.favorite);
                    int[] viewScreenLocation = new int[2];
                    view.getLocationOnScreen(viewScreenLocation);
                    int[] textScreenLocation = new int[2];
                    textView.getLocationOnScreen(textScreenLocation);
                    int[] checkboxScreenLocation = new int[2];
                    checkBox.getLocationOnScreen(checkboxScreenLocation);
                    Intent intent = new Intent(MenuListActivity.this, ArticleActivity.class);

                    // Initialize the views that will be move and scale
                    // during the change of activity
                    ArrayList<AnimatedView> dataList = new ArrayList<>();
                    AnimatedView dividerAnimatedView = new AnimatedView(viewScreenLocation[1] + view.getHeight(),
                            viewScreenLocation[0] + ScreenUtils.valueToDpi(getResources(), 20),
                            view.getWidth() - ScreenUtils.valueToDpi(getResources(), 40),
                            ScreenUtils.valueToDpi(getResources(), 1),
                            R.id.divider, 2, 0);
                    AnimatedView textAnimatedView = new AnimatedView(textScreenLocation[1],
                            textScreenLocation[0],
                            textView.getWidth(),
                            textView.getHeight(),
                            R.id.title, 2, 0);
                    AnimatedView checkboxAnimatedView = new AnimatedView(checkboxScreenLocation[1],
                            checkboxScreenLocation[0],
                            checkBox.getWidth(),
                            checkBox.getHeight(),
                            R.id.favorite, 2, 0);
                    dataList.add(dividerAnimatedView);
                    dataList.add(textAnimatedView);
                    dataList.add(checkboxAnimatedView);

                    intent.
                            putExtra(Constants.PACKAGE_NAME + ".itemResourceId", resourceId).
                            putParcelableArrayListExtra(Constants.PACKAGE_NAME + ".animatedViews", dataList);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with the action of the last favorite removed is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            replaceListViewByPlaceholderText();
        }
    };

    /**
     * Add animated views to the superview so that it can animate them
     */
    @Override
    public void addAnimatedViews() {
        AnimatedView backgroundAnimatedView = new AnimatedView(findViewById(R.id.tropical_background), 0, 0, AnimType.ALPHA, 2);
        backgroundAnimatedView.setStartDelay(0);
        backgroundAnimatedView.setEndDelay(Constants.ANIMATION_DURATION / 2);

        AnimatedView listAnimatedView = new AnimatedView(findViewById(R.id.listView), 0, 0, AnimType.RESIZE_HEIGHT, 2);
        listAnimatedView.setStartDelay(Constants.ANIMATION_DURATION / 2);
        listAnimatedView.setEndDelay(0);

        AnimatedView dividerAnimatedView = new AnimatedView(findViewById(R.id.divider), 0, 0, AnimType.ALPHA, 2);
        dividerAnimatedView.setStartDelay(0);
        dividerAnimatedView.setEndDelay(Constants.ANIMATION_DURATION / 2);
        AnimatedView addFavoriteAnimatedView = new AnimatedView(findViewById(R.id.add_favorite), 0, 0, AnimType.ALPHA, 2);
        dividerAnimatedView.setStartDelay(0);
        dividerAnimatedView.setEndDelay(Constants.ANIMATION_DURATION / 2);

        animatedViews.add(backgroundAnimatedView);
        animatedViews.add(listAnimatedView);
        animatedViews.add(dividerAnimatedView);
        animatedViews.add(addFavoriteAnimatedView);
    }

    /**
     * Make the moving view disappear from the previous activity
     */
    @Override
    protected void processSpecialCaseEnterAnimation() {
        final View view = ActivityTransitionManager.getInstance().getMenuItemView();
        if (view != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setAlpha(0);
                }
            });
        }
    }

    /**
     * Make the moving view reappear in the previous activity
     */
    @Override
    protected void processSpecialCaseExitAnimation() {
        final View view = ActivityTransitionManager.getInstance().getMenuItemView();
        if (view != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setAlpha(1);
                }
            });
        }
    }

    /**
     * Unregister from the last favorite notification before destroying the activity
     */
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        // This is somewhat like [[NSNotificationCenter defaultCenter] removeObserver:name:object:]
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
