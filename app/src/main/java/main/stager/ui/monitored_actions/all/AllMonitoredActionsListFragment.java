package main.stager.ui.monitored_actions.all;

import android.os.Bundle;
import android.view.View;
import main.stager.R;
import main.stager.list.StagerList;
import main.stager.model.UserAction;
import main.stager.ui.actions.ActionItemRecyclerViewAdapter;
import main.stager.ui.monitored_action.MonitoredActionFragment;

public class AllMonitoredActionsListFragment extends
        StagerList<AllMonitoredActionsListViewModel, ActionItemRecyclerViewAdapter, UserAction> {
    @Override
    protected Class<AllMonitoredActionsListViewModel> getViewModelType() {
        return AllMonitoredActionsListViewModel.class;
    }

    @Override
    protected Class<ActionItemRecyclerViewAdapter> getAdapterType() {
        return ActionItemRecyclerViewAdapter.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_monitored_actions_all;
    }

    @Override
    protected void onItemClick(UserAction item, int pos, View view) {
        super.onItemClick(item, pos, view);
        Bundle args = new Bundle();
        args.putString(MonitoredActionFragment.ARG_ACTION_NAME, item.getName());
        args.putString(MonitoredActionFragment.ARG_ACTION_KEY, item.getKey());
        args.putString(MonitoredActionFragment.ARG_ACTION_OWNER, item.getOwner());
        navigator.navigate(R.id.transition_monitored_actions_to_monitored_action, args);
    }
}