package com.aloisandco.beautifuleasysummer.MenuList;

import android.app.Activity;
import android.content.res.TypedArray;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.HtmlTagHandler;

/**
 * Created by quentinmetzler on 22/03/15.
 */
public class MenuListAdapter extends BaseAdapter {
    private Activity mContext;
    private TypedArray mMenuListArray;

    public MenuListAdapter(Activity context, int resourceId) {
        mContext = context;
        mMenuListArray = mContext.getResources().obtainTypedArray(resourceId);
    }

    public int getCount() {
        return mMenuListArray.length();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout = null;
        TypedArray itemArray = mContext.getResources().obtainTypedArray(mMenuListArray.getResourceId(position, 0));

        String string =  itemArray.getString(0);
        itemArray.recycle();

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            linearLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.activity_menu_list_item, parent, false);
        } else {
            linearLayout = (LinearLayout) convertView;
        }

        FontManager fontManager = FontManager.getInstance(mContext.getAssets());
        TextView textView = (TextView) linearLayout.findViewById(R.id.textView);
        textView.setTypeface(fontManager.ralewayLightFont);
        textView.setText(Html.fromHtml(string, null, new HtmlTagHandler()));

        return linearLayout;
    }
}
