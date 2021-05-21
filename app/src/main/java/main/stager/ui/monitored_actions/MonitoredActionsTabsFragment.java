package main.stager.ui.monitored_actions;

import androidx.fragment.app.FragmentStatePagerAdapter;
import main.stager.Base.StagerTabsFragment;
import main.stager.R;

public class MonitoredActionsTabsFragment extends StagerTabsFragment {
    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_contact_requests_tabs;
    }

    @Override
    protected FragmentStatePagerAdapter getPagerAdapter() {
        return new MonitoredActionsTabsAdapter(
                getChildFragmentManager(), getContext());
    }
}

