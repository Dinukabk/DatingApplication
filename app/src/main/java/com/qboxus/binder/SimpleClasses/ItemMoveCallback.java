package com.qboxus.binder.SimpleClasses;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.ViewHolders.PhotosviewHolder;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(PhotosviewHolder myViewHolder);
        void onRowClear(PhotosviewHolder myViewHolder);

    }



    private final ItemTouchHelperContract itemTouchListener;

    public ItemMoveCallback(ItemTouchHelperContract itemTouchListener) {
        this.itemTouchListener = itemTouchListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }



    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Functions.printLog("onSwiped");
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, 0);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        itemTouchListener.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        Functions.printLog("clearView");
        if (viewHolder instanceof PhotosviewHolder) {
            PhotosviewHolder myViewHolder=
                    (PhotosviewHolder) viewHolder;
            itemTouchListener.onRowClear(myViewHolder);
        }
    }


}
