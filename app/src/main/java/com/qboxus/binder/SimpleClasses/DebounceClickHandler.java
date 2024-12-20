package com.qboxus.binder.SimpleClasses;

import android.view.View;

public class DebounceClickHandler implements View.OnClickListener {

    private static final long DEBOUNCE_INTERVAL = 500; // 500 milliseconds
    private long lastClickTime = 0;
    private final View.OnClickListener onClickListener;

    public DebounceClickHandler(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DEBOUNCE_INTERVAL) {
            lastClickTime = currentTime;
            onClickListener.onClick(view);
        }
    }
}
