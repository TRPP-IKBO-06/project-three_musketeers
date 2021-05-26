package main.stager.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import main.stager.R;
import main.stager.model.FBModel;

public abstract class StagerListAdapter<T, VH extends StagerViewHolder<T>>
        extends ListAdapter<T, VH> {
    protected OnItemClickListener<T> onItemClickListener;

    protected abstract Class<VH> getViewHolderType();

    protected StagerListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    protected VH createViewHolder(View view) {
        try {
            return getViewHolderType().getConstructor(View.class).newInstance(view);
        } catch (IllegalAccessException e) {
            return null;
        } catch (java.lang.InstantiationException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    public T get(int index) {
        return getItem(index);
    }

    @FunctionalInterface
    public interface OnItemClickListener<T> {
        void onItemClick(T item, int pos, View view);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void moveItem(int from, int to) {
        ArrayList<T> list = new ArrayList<T>(getCurrentList());
        list.add(to, list.remove(from));
        submitList(list);
    }

    protected void bindOnItemClickListener(View view, T item, int pos){
        if (onItemClickListener != null)
            view.setOnClickListener(v -> onItemClickListener
                    .onItemClick(item, pos, view));
    }

    protected abstract static class FBItemCallback<U extends FBModel> extends DiffUtil.ItemCallback<U> {
        @Override
        public boolean areItemsTheSame(@NonNull U oldItem, @NonNull U newItem) {
            return oldItem.getKey().equals(newItem.getKey());
        }
    }

    protected @LayoutRes int getItemListLayout() {
        return R.layout.fragment_list_item;
    }

    @NotNull
    @Override
    public VH onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getItemListLayout(), parent, false);
        VH vh = createViewHolder(view);
        onViewHolderCreated(view, vh);
        return vh;
    }

    protected void onViewHolderCreated(View view, VH holder) {}

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.mItem = getItem(position);
        onBindViewHolderInner(holder, position);
        bindOnItemClickListener(holder.mView, holder.mItem, position);
    }

    protected void onBindViewHolderInner(final VH holder, int position) {}
}
