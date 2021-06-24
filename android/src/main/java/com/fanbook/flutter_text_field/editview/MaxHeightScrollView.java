package com.fanbook.flutter_text_field.editview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.fanbook.flutter_text_field.Utils;

public class MaxHeightScrollView extends ScrollView {

    // max height unit - dp
    private int maxHeight;

    public MaxHeightScrollView(Context context) {
        this(context, 142);
    }

    public MaxHeightScrollView(Context context, int maxHeight) {
        this(context, null);
        this.maxHeight = Utils.dip2px(context, maxHeight);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST));
    }
}