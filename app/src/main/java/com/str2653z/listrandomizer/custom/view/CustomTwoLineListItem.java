package com.str2653z.listrandomizer.custom.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.str2653z.listrandomizer.MainActivity;

/**
 * Created by str2653z on 2015/11/22.
 */
public class CustomTwoLineListItem extends TwoLineListItem {

    public CustomTwoLineListItem(Context context) {
        super(context);
    }

    public CustomTwoLineListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTwoLineListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        // 画面から非表示になったら背景色をデフォルトに戻す
        if (visibility == View.INVISIBLE) {
            MainActivity activity = (MainActivity)this.getContext();
            this.setBackgroundColor(activity.defaultBackgroundColor);
            Log.d("onDetachedFromWindow","onDetachedFromWindow動作");
        }
    }
}
