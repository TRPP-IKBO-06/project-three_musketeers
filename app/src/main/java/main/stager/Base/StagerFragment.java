package main.stager.Base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import main.stager.R;
import main.stager.utils.ChangeListeners.firebase.ValueRemovedListener;
import main.stager.utils.DataProvider;
import main.stager.utils.Utilits;

public abstract class StagerFragment extends Fragment {
    protected static DataProvider dataProvider = DataProvider.getInstance();

    // Зависимости
    protected List<DatabaseReference> dependencies;

    // Требует переопределения
    protected abstract @LayoutRes int getViewBaseLayoutId();

    // Основное
    protected NavController navigator;
    protected View view;

    protected NavController getNavigator() {
        return ((NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment))
                .getNavController();
    }

    protected void setEventListeners() {}
    protected void prepareFragmentComponents() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setDependencies();
        bindDependencies();
        view = inflater.inflate(getViewBaseLayoutId(), container, false);
        navigator = getNavigator();
        prepareFragmentComponents();
        setEventListeners();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDependencies();
    }

    public boolean isSafe() {
        return !(this.isRemoving() || this.getActivity() == null ||
                 this.isDetached() || !this.isAdded() ||
                 this.getView() == null || navigator == null ||
                 this.getActivity().isFinishing());
    }

    protected void setDependencies() { dependencies = new ArrayList<>(); }

    protected ValueRemovedListener onValueRemovedListener;

    protected void bindDependencies() {
        if (Utilits.isNullOrEmpty(dependencies)) return;

        if (onValueRemovedListener == null)
            onValueRemovedListener = new ValueRemovedListener() {
                @Override public void onValueRemoved() { close(); }
            };

        for (DatabaseReference ref: dependencies)
            ref.addValueEventListener(onValueRemovedListener);
    }

    protected void unbindDependencies() {
        if (Utilits.isNullOrEmpty(dependencies)
                || onValueRemovedListener == null) return;
        for (DatabaseReference ref: dependencies)
            ref.removeEventListener(onValueRemovedListener);
    }

    protected void close() {
        if (isSafe())
            navigator.navigateUp();
    }
}