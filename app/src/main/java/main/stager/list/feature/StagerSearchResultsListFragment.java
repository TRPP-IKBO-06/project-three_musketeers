package main.stager.list.feature;

import androidx.recyclerview.widget.RecyclerView;
import main.stager.list.StagerList;
import main.stager.list.StagerListAdapter;
import main.stager.list.StagerListViewModel;
import main.stager.model.FBModel;
import main.stager.utils.Utilits;

public abstract class StagerSearchResultsListFragment<
        TVM extends StagerSearchResultsListViewModel<T>,
        TA extends StagerListAdapter<T, ? extends RecyclerView.ViewHolder>,
        T extends FBModel> extends StagerList<TVM, TA, T> {

    @Override
    public boolean ALLOW_SEARCH() { return true; }

    @Override
    public void onSearchQueryChange(String query) {
        super.onSearchQueryChange(query);
        if (Utilits.isNullOrBlank(query)) return;
        onDataLoadingStarted();
        viewModel.setQuery(query.trim());
    }
}