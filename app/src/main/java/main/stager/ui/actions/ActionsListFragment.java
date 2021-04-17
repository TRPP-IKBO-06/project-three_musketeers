package main.stager.ui.actions;

import android.view.View;

import androidx.lifecycle.LiveData;

import java.util.List;

import main.stager.DataProvider;
import main.stager.R;
import main.stager.StagerExtendableListFragment;
import main.stager.model.UserAction;

public class ActionsListFragment extends
        StagerExtendableListFragment<ActionsListViewModel, ActionItemRecyclerViewAdapter, UserAction> {
    @Override
    protected Class<ActionsListViewModel> getViewModelType() {
        return ActionsListViewModel.class;
    }

    @Override
    protected Class<ActionItemRecyclerViewAdapter> getAdapterType() {
        return ActionItemRecyclerViewAdapter.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_actions;
    }

    @Override
    protected LiveData<List<UserAction>> getList(DataProvider.OnError onError) {
        return viewModel.getActions(onError);
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        list.observe(getViewLifecycleOwner(), adapter::setValues);
    }

    @Override
    protected void onButtonAddClicked(View v) {
        navigator.navigate(R.id.transition_actions_list_to_add_action);
    }
}