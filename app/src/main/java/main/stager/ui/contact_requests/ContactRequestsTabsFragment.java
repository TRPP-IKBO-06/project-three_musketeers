package main.stager.ui.contact_requests;

import androidx.fragment.app.FragmentStatePagerAdapter;
import main.stager.Base.StagerTabsFragment;
import main.stager.R;

public class ContactRequestsTabsFragment extends StagerTabsFragment {
    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_contact_requests_tabs;
    }

    @Override
    protected FragmentStatePagerAdapter getPagerAdapter() {
        return new ContactRequestsTabsAdapter(
                getChildFragmentManager(), getContext());
    }
}

