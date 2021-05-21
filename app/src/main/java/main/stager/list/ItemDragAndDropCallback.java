package main.stager.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import static com.google.android.material.color.MaterialColors.ALPHA_FULL;

abstract class ItemDragAndDropCallback extends ItemTouchHelper.Callback {
    private Integer mFrom = null;
    private Integer mTo = null;

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView,
                          RecyclerView.ViewHolder source,
                          RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType())
            return false;

        if (mFrom == null)
            mFrom = source.getAdapterPosition();
        mTo = target.getAdapterPosition();

        recyclerView.getAdapter().notifyItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
        onDrag(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    protected abstract void onDrag(int from, int to);

    protected abstract void onDrop(int from, int to);

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

        if (mFrom != null && mTo != null)
            onDrop(mFrom, mTo);

        // clear saved positions
        mFrom = mTo = null;
    }
}
