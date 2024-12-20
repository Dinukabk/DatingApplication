package com.qboxus.binder.interfaces;

import android.view.View;

public interface AdapterClickListener {
    void onItemClick(int pos, Object item, View view);

    void onLongItemClick(int pos, Object item, View view);
}
