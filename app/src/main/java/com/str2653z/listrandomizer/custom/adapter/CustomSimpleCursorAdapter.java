package com.str2653z.listrandomizer.custom.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.str2653z.listrandomizer.MainActivity;

/**
 * Created by str2653z on 2015/11/23.
 */
public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        MainActivity mainActivity = (MainActivity) view.getContext();
        Integer selectedRandomIndex = mainActivity.selectedRandomIndex;

        if ( selectedRandomIndex != null ) {
            Log.d("getView", "■position：" + position);
            Log.d("getView", "■selectedRandomIndex：" + selectedRandomIndex);
            if ( position != selectedRandomIndex.intValue() ) {
                Log.d("getView", "■背景色を初期化");
                view.setBackgroundColor(mainActivity.defaultBackgroundColor);
            } else {
                Log.d("getView", "■背景色をランダム選択中色に設定");
                view.setBackgroundColor(ContextCompat.getColor(mainActivity, android.R.color.holo_red_light));
            }
        }
        return view;
    }
}
