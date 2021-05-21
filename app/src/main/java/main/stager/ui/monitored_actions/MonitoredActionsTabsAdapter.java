package main.stager.ui.monitored_actions;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import main.stager.R;
import main.stager.ui.monitored_actions.all.AllMonitoredActionsListFragment;

public class MonitoredActionsTabsAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public MonitoredActionsTabsAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    @NonNull
    public Fragment getItem(int i) {
        switch (i) {
            case 0:  return new AllMonitoredActionsListFragment();
            case 1: // Not implemented yet
            default: throw new IllegalStateException("Item not found");
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    private String getString(@StringRes int id) {
        return context.getString(id);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return getString(R.string.AllMonitoredActions_title);
            case 1: return getString(R.string.GroupedMonitoredActions_title);
            default: throw new IllegalStateException("Item not found");
        }
    }
}
