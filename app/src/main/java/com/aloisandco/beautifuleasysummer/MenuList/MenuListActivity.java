package com.aloisandco.beautifuleasysummer.MenuList;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.AnimType;
import com.aloisandco.beautifuleasysummer.AnimatedActivity;
import com.aloisandco.beautifuleasysummer.AnimatedView;
import com.aloisandco.beautifuleasysummer.Article.ArticleActivity;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.utils.BitmapCacheUtils;
import com.aloisandco.beautifuleasysummer.utils.Constants;
import com.aloisandco.beautifuleasysummer.utils.FavoriteManager;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.ScreenUtils;

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
            if (!FavoriteManager.isArticleFavorite(mLastClickedResourceId, this)) {
                FavoriteManager.addArticleToFavoriteAtPosition(mLastClickedResourceId, mLastClickedIndex, this);
                mAdapter.deleteCell(mLastClickedView, mLastClickedResourceId);
            }
            mLastClickedView = null;
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayMediumFont);
    }

    private void initTitleAndIcon() {
        Bundle bundle = getIntent().getExtras();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), bundle.getInt(Constants.PACKAGE_NAME + ".iconId"));
        String title = bundle.getString(Constants.PACKAGE_NAME + ".text");

        mIconImageView.setImageBitmap(icon);
        mTitleTextView.setText(title);
    }

    private void initListView() {
        Bundle bundle = getIntent().getExtras();
        final int menuListArrayResourceId = bundle.getInt(Constants.PACKAGE_NAME + ".menuListArray");

        mAdapter = new MenuListAdapter(this, menuListArrayResourceId);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setEnabled(false);

                ActivityTransitionManager.getInstance().setMenuListItemView(view);

                int resourceId;
                if(menuListArrayResourceId == 0) {
                    resourceId = FavoriteManager.getArticleAtPosition(position, MenuListActivity.this);
                    mLastClickedView = view;
                    mLastClickedResourceId = resourceId;
                    mLastClickedIndex = position;
                } else {
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

                ArrayList<AnimatedView> dataList = new ArrayList<>();
                AnimatedView dividerAnimatedView = new AnimatedView(viewScreenLocation[1] + view.getHeight(),
                        viewScreenLocation[0] + ScreenUtils.valueToDpi(getResources(), 20),
                        view.getWidth() - ScreenUtils.valueToDpi(getResources(), 40),
                        ScreenUtils.valueToDpi(getResources(), 1),
                        R.id.divider, 2);
                AnimatedView textAnimatedView = new AnimatedView(textScreenLocation[1],
                        textScreenLocation[0],
                        textView.getWidth(),
                        textView.getHeight(),
                        R.id.title, 2);
                AnimatedView checkboxAnimatedView = new AnimatedView(checkboxScreenLocation[1],
                        checkboxScreenLocation[0],
                        checkBox.getWidth(),
                        checkBox.getHeight(),
                        R.id.favorite, 2);
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

        animatedViews.add(backgroundAnimatedView);
        animatedViews.add(listAnimatedView);
        animatedViews.add(dividerAnimatedView);
    }

    @Override
    protected void processSpecialCaseBeforeAnimation() {
        View view = ActivityTransitionManager.getInstance().getMenuItemView();
        if (view != null) {
            view.setAlpha(0);
        }
    }

    @Override
    protected void processSpecialCaseAfterAnimation() {
        View view = ActivityTransitionManager.getInstance().getMenuItemView();
        if (view != null) {
            view.setAlpha(1);
        }
    }
}
