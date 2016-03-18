package com.huliang.weibo.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Pull2RefreshListView extends PullToRefreshListView {

    public Pull2RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public Pull2RefreshListView(
            Context context,
            Mode mode,
            AnimationStyle style) {
        super(context, mode, style);
        // TODO Auto-generated constructor stub
    }

    public Pull2RefreshListView(Context context,
                                Mode mode) {
        super(context, mode);
        // TODO Auto-generated constructor stub
    }

    public Pull2RefreshListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onPlvScrollListener != null) {
            onPlvScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private OnPlvScrollListener onPlvScrollListener;

    public void setOnPlvScrollListener(OnPlvScrollListener onPlvScrollListener) {
        this.onPlvScrollListener = onPlvScrollListener;
    }

    public interface OnPlvScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
