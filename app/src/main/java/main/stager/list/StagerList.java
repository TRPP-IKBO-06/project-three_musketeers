package main.stager.list;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.rockerhieu.rvadapter.states.StatesRecyclerViewAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import main.stager.model.FBModel;
import main.stager.R;
import main.stager.Base.StagerVMFragment;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.Utilits;

public abstract class StagerList<TVM extends StagerListViewModel<T>,
                                 TA extends StagerListAdapter<T,
                                            ? extends RecyclerView.ViewHolder>,
                                 T extends FBModel> extends StagerVMFragment<TVM> {

    public boolean ALLOW_SEARCH() { return false; }
    public boolean ALLOW_DRAG_AND_DROP() { return false; }
    public boolean ALLOW_SWIPE() { return false; }

    protected TA adapter;

    // Требует переопределения
    protected abstract Class<TA> getAdapterType();

    // Основное
    protected RecyclerView rv;
    protected LiveData<List<T>> list;
    protected StatesRecyclerViewAdapter srvAdapter;

    // Режимы отображения
    protected View emptyView;
    protected View errorView;
    protected View loadingView;

    // Listeners
    protected void onItemClick(T item, int pos, View view) {}
    protected void onItemLongClick(T item, int pos, View view) {
        if (ALLOW_DRAG_AND_DROP())
            animateItemDrag(view);
    }
    protected void onItemLongClickFinished(T item, int pos, View view) {
        if (!ALLOW_DRAG_AND_DROP())
            onItemClick(item, pos, view);
    }
    public void onItemSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int pos, int direction) {}
    public void onSearchQuerySubmit(String query) {}
    public void onSearchQueryChange(String query) {}
    protected void onItemDragged(int from, int to) {
        adapter.moveItem(from, to);
    }
    protected void onItemDropped(int from, int to) {
        viewModel.pushItemsPositions(adapter.getCurrentList());
    }

    private boolean isLastClickLong = false;

    protected TA createAdapter() {
        try {
            return getAdapterType().newInstance();
        } catch (IllegalAccessException e) {
            return null;
        } catch (java.lang.InstantiationException e) {
            return null;
        }
    }

    protected LiveData<List<T>> getList(OnError onError) {
        return viewModel.getItems(onError, ALLOW_DRAG_AND_DROP());
    }

    protected RecyclerView getRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        return recyclerView;
    }

    protected View getEmptyView() {
        return getLayoutInflater().inflate(R.layout.empty_list_view, rv, false);
    }

    protected View getErrorView() {
        return getLayoutInflater().inflate(R.layout.error_list_view, rv, false);
    }

    protected View getLoadingView() {
        View v = getLayoutInflater().inflate(R.layout.loading_list_view, rv, false);
        v.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.appear));
            }

            @Override
            public void onViewDetachedFromWindow(View v) {}
        });
        return v;
    }

    protected String getQueryHintText() {
        return null;
    }

    protected String getEmptyText() {
        return getString(R.string.CommonList_EmptyListView_TextView_text);
    }

    protected String getErrorText(String reason) {
        return getString(R.string.CommonList_ErrorListView_TextView_text, reason);
    }

    protected String getLoadingText() {
        return getString(R.string.CommonList_LoadingListView_TextView_text);
    }

    private void setNNText(View v, @IdRes int id, String text) {
        if (v == null) return;
        TextView tv = v.findViewById(id);
        if (tv == null) return;
        tv.setText(text);
    }

    protected void prepareViews() {
        emptyView = getEmptyView();
        errorView = getErrorView();
        loadingView = getLoadingView();
        setNNText(emptyView, R.id.empty_list_view_message, getEmptyText());
        setNNText(errorView, R.id.error_list_view_message, getErrorText(null));
        setNNText(loadingView, R.id.loading_list_view_message, getLoadingText());
    }

    protected void onError(String reason) {
        if (!isSafe()) return;
        setNNText(errorView, R.id.error_list_view_message, getErrorText(reason));
        srvAdapter.setState(StatesRecyclerViewAdapter.STATE_ERROR);
    }

    protected void onDataLoadingStarted() {
        srvAdapter.setState(StatesRecyclerViewAdapter.STATE_LOADING);
    }

    protected void reactState(List<?> list) {
        if (Utilits.isNullOrEmpty(list))
            srvAdapter.setState(StatesRecyclerViewAdapter.STATE_EMPTY);
        else
            srvAdapter.setState(StatesRecyclerViewAdapter.STATE_NORMAL);
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        bindData(list, this::reactState);
        bindData(list, lst -> adapter.submitList(
                Utilits.isNullOrEmpty(lst) ? null : lst));
    }

    // Bind listeners
    protected void bindOnSwipeListener() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                onItemSwiped(viewHolder, viewHolder.getAdapterPosition(), direction);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (srvAdapter.getState() != StatesRecyclerViewAdapter.STATE_NORMAL)
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        }).attachToRecyclerView(rv);
    }

    protected void bindOnDragAndDropListener() {
        new ItemTouchHelper(new ItemDragAndDropCallback() {
            @Override
            protected void onDrag(int from, int to) {
                onItemDragged(from, to);
            }

            @Override
            protected void onDrop(int from, int to) {
                onItemDropped(from, to);
            }

            @Override
            protected void onCompleted(View v, boolean isMoved) {
                super.onCompleted(v, isMoved);
                animateItemDrop(v, isMoved);
            }

            @Override
            public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                if (srvAdapter.getState() != StatesRecyclerViewAdapter.STATE_NORMAL)
                    return 0;
                return super.getMovementFlags(recyclerView, viewHolder);
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    protected void setEventListeners() {
        super.setEventListeners();
        adapter.setOnItemClickListener((item, pos, view) -> {
            if (isLastClickLong)
                onItemLongClickFinished(item, pos, view);
            else
                onItemClick(item, pos, view);
            isLastClickLong = false;
        });
        adapter.setOnItemLongClickListener((item, pos, view) -> {
            isLastClickLong = true;
            onItemLongClick(item, pos, view);
        });
        if (ALLOW_SWIPE()) bindOnSwipeListener();
        if (ALLOW_DRAG_AND_DROP()) bindOnDragAndDropListener();
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        adapter = createAdapter();
        rv = getRecyclerView();
        prepareViews();
        srvAdapter = new StatesRecyclerViewAdapter(adapter, loadingView, emptyView, errorView);
        rv.setAdapter(srvAdapter);
        list = getList(this::onError);
        onDataLoadingStarted();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!ALLOW_SEARCH()) return;
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getQueryHintText());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchQuerySubmit(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onSearchQueryChange(newText);
                return false;
            }
        });
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ALLOW_SEARCH())
            setHasOptionsMenu(true);
    }

    // Animation support
    protected void animateItemDrag(View view) {
        view.setRotation(5f);
    }

    protected void animateItemDrop(View view, boolean isMoved) {
        view.setRotation(0f);
    }
}