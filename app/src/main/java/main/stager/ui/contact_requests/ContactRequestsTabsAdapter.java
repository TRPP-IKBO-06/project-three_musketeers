package main.stager.ui.contact_requests;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import main.stager.R;
import main.stager.model.ContactType;
import main.stager.ui.contact_requests.ignored.IgnoredContactRequestsFragment;
import main.stager.ui.contact_requests.incoming.IncomingContactRequestsFragment;
import main.stager.ui.contact_requests.outgoing.OutgoingContactRequestsFragment;

public class ContactRequestsTabsAdapter extends FragmentStatePagerAdapter {
    public static final ContactType[] tabs = new ContactType[] {
            ContactType.INCOMING,
            ContactType.OUTGOING,
            ContactType.IGNORED
    };

    private Context context;

    public ContactRequestsTabsAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (tabs[i]) {
            case IGNORED: return new IgnoredContactRequestsFragment();
            case INCOMING: return new IncomingContactRequestsFragment();
            case OUTGOING: return new OutgoingContactRequestsFragment();
            default: throw new IllegalStateException("Unsupported ContactType");
        }
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    private String getString(@StringRes int id) {
        return context.getString(id);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (tabs[position]) {
            case INCOMING: return getString(R.string.ContactRequestsTabsFragment_Tab_title_incoming);
            case OUTGOING: return getString(R.string.ContactRequestsTabsFragment_Tab_title_outgoing);
            case IGNORED: return getString(R.string.ContactRequestsTabsFragment_Tab_title_ignored);
            default:
                return "??? HOW DID U GET THERE ???";
        }
    }
}
