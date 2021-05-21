package main.stager.ui.actions;

import android.os.Bundle;
import android.view.View;
import main.stager.R;
import main.stager.list.StagerExtendableList;
import main.stager.model.UserAction;
import main.stager.ui.edit_item.edit_action.EditActionFragment;

public class ActionsListFragment extends
        StagerExtendableList<ActionsListViewModel, ActionItemRecyclerViewAdapter, UserAction> {
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
    protected void onItemClick(UserAction item, int pos, View view) {
        super.onItemClick(item, pos, view);
        Bundle args = new Bundle();
        args.putString(EditActionFragment.ARG_ACTION_NAME, item.getName());
        args.putString(EditActionFragment.ARG_ACTION_KEY, item.getKey());
        navigator.navigate(R.id.transition_actions_list_to_action_stages_list, args);
    }

    @Override
    protected void onButtonAddClicked(View v) {
        navigator.navigate(R.id.transition_actions_list_to_add_action);
    }
}