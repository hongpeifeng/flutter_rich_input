//package com.fanbook.flutter_text_field.editview;
//
//import android.text.Layout;
//import android.text.Spannable;
//import android.text.method.BaseMovementMethod;
//import android.text.method.MovementMethod;
//import android.text.method.Touch;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.TextView;
//
//public class EditViewMovementMethod extends BaseMovementMethod implements MovementMethod {
//    @Override
//    protected boolean left(TextView widget, Spannable buffer) {
//        return scrollLeft(widget, buffer, 1);
//    }
//
//    @Override
//    protected boolean right(TextView widget, Spannable buffer) {
//        return scrollRight(widget, buffer, 1);
//    }
//
//    @Override
//    protected boolean up(TextView widget, Spannable buffer) {
//        return scrollUp(widget, buffer, 1);
//    }
//
//    @Override
//    protected boolean down(TextView widget, Spannable buffer) {
//        return scrollDown(widget, buffer, 1);
//    }
//
//    @Override
//    protected boolean pageUp(TextView widget, Spannable buffer) {
//        return scrollPageUp(widget, buffer);
//    }
//
//    @Override
//    protected boolean pageDown(TextView widget, Spannable buffer) {
//        return scrollPageDown(widget, buffer);
//    }
//
//    @Override
//    protected boolean top(TextView widget, Spannable buffer) {
//        return scrollTop(widget, buffer);
//    }
//
//    @Override
//    protected boolean bottom(TextView widget, Spannable buffer) {
//        return scrollBottom(widget, buffer);
//    }
//
//    @Override
//    protected boolean lineStart(TextView widget, Spannable buffer) {
//        return scrollLineStart(widget, buffer);
//    }
//
//    @Override
//    protected boolean lineEnd(TextView widget, Spannable buffer) {
//        return scrollLineEnd(widget, buffer);
//    }
//
//    @Override
//    protected boolean home(TextView widget, Spannable buffer) {
//        return top(widget, buffer);
//    }
//
//    @Override
//    protected boolean end(TextView widget, Spannable buffer) {
//        return bottom(widget, buffer);
//    }
//
//    @Override
//    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
//        return Touch.onTouchEvent(widget, buffer, event);
//    }
//
//    @Override
//    public void onTakeFocus(TextView widget, Spannable text, int dir) {
//        Layout layout = widget.getLayout();
//
//        if (layout != null && (dir & View.FOCUS_FORWARD) != 0) {
//            widget.scrollTo(widget.getScrollX(),
//                    layout.getLineTop(0));
//        }
//        if (layout != null && (dir & View.FOCUS_BACKWARD) != 0) {
//            int padding = widget.getTotalPaddingTop() +
//                    widget.getTotalPaddingBottom();
//            int line = layout.getLineCount() - 1;
//
//            widget.scrollTo(widget.getScrollX(),
//                    layout.getLineTop(line+1) -
//                            (widget.getHeight() - padding));
//        }
//    }
//
//    public static MovementMethod getInstance() {
//        if (sInstance == null)
//            sInstance = new android.text.method.ScrollingMovementMethod();
//
//        return sInstance;
//    }
//
//    private static android.text.method.ScrollingMovementMethod sInstance;
//}
