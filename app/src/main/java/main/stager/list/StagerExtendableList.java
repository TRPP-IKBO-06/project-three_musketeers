package main.stager.list;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import main.stager.R;
import main.stager.model.FBModel;

public abstract class StagerExtendableList<TVM extends StagerListViewModel<T>,
                                           TA extends StagerListAdapter<T,
                                                      ? extends StagerViewHolder<T>>,
                                           T extends FBModel> extends StagerList<TVM, TA, T> {

    public boolean ALLOW_DRAG_AND_DROP() { return true; }
    public boolean ALLOW_SWIPE() { return true; }

    // Listeners
    protected abstract void onButtonAddClicked(View v);

    @Override
    public void onItemSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int pos, int direction) {
        super.onItemSwiped(viewHolder, pos, direction);
        viewModel.deleteItem(adapter.get(pos));
        adapter.removeItem(pos);
    }

    protected void setEventListeners() {
        super.setEventListeners();
        view.findViewById(R.id.button_list_add).setOnClickListener(this::onButtonAddClicked);
    }
}