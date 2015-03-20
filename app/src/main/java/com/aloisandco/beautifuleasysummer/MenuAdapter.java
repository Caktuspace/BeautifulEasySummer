package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.utils.FontManager;

/**
 * Created by quentinmetzler on 19/03/15.
 */
public class MenuAdapter extends BaseAdapter {
    private Activity mContext;
    private TypedArray mMenuArray;

    public MenuAdapter(Activity c) {
        mContext = c;
        mMenuArray = mContext.getResources().obtainTypedArray(R.array.menu);
    }

    public int getCount() {
        return  mMenuArray.length();
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
        TypedArray itemArray = mContext.getResources().obtainTypedArray(mMenuArray.getResourceId(position, 0));

        int resourceId =  itemArray.getResourceId(0, 0);
        String text =  itemArray.getString(1);
        itemArray.recycle();

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            linearLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.activity_menu_item, parent, false);
        } else {
            linearLayout = (LinearLayout) convertView;
        }

        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.icon);
        TextView textView = (TextView) linearLayout.findViewById(R.id.text);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), resourceId));
        textView.setText(text);
        FontManager fontManager = FontManager.getInstance(mContext.getAssets());
        textView.setTypeface(fontManager.ralewayMediumFont);

        return linearLayout;
    }
}
