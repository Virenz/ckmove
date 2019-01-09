package com.example.tq.ckmove.base;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WebViewMod extends WebView {
    public WebViewMod(Context context) {
        super(context);
    }

    public WebViewMod(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewMod(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //重写onScrollChanged 方法
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        scrollTo(l,0);
    }
}
