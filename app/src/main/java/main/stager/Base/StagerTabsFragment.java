package main.stager.Base;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import main.stager.R;

public abstract class StagerTabsFragment extends StagerFragment {
    FragmentStatePagerAdapter adapter;
    ViewPager viewPager;

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        adapter = getPagerAdapter();
        viewPager = view.findViewById(getPagerId());
        viewPager.setAdapter(adapter);
        ((TabLayout) view.findViewById(getTabLayoutId()))
                           .setupWithViewPager(viewPager);
    }

    protected @IdRes int getTabLayoutId() {
        return R.id.sliding_tabs;
    }

    protected @IdRes int getPagerId() {
        return R.id.pager;
    }

    protected abstract FragmentStatePagerAdapter getPagerAdapter();
}

