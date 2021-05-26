package main.stager.list;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class StagerViewHolder<T> extends RecyclerView.ViewHolder {
    public final View mView;
    public T mItem;

    public StagerViewHolder(View view) {
        super(view);
        mView = view;
    }
}
