package com.qboxus.binder.SimpleClasses;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;
    private final int space1;

    public SpacesItemDecoration(int space, int space1) {
        this.space = space;
        this.space1 = space1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        outRect.top = 0;
        outRect.left = space;

        boolean isLast = position == state.getItemCount() - 1;
        if (isLast) {
            outRect.right = space;
            outRect.left = space1;
        } else if(position == 0){
            outRect.left = space;
            outRect.right = space1;
        } else {
            outRect.left = space1;
            outRect.right = space1;
        }
    }
}
