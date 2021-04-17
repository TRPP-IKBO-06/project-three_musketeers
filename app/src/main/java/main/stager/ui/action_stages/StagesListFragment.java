package main.stager.ui.action_stages;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import main.stager.R;
import main.stager.SmartActivity;
import main.stager.StagerExtendableListFragment;
import main.stager.model.Stage;
import main.stager.model.UserAction;
import main.stager.ui.add_action_stage.AddStageFragment;

public class StagesListFragment extends StagerExtendableListFragment<StagesListViewModel, StageItemRecyclerViewAdapter> {
    static public final String ARG_ACTION_NAME = "Stager.stages_list.param_action_name";
    static public final String ARG_ACTION_KEY = "Stager.stages_list.param_action_key";
    private String mActionName;
    private String mActionKey;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActionName = getArguments().getString(ARG_ACTION_NAME);
            mActionKey = getArguments().getString(ARG_ACTION_KEY);
        } else {
            mActionName = getString(R.string.StagesFragment_ActivityList_UntitledAction);
            mActionKey = "";
        }
    }

    @Override
    protected Class<StagesListViewModel> getViewModelType() {
        return StagesListViewModel.class;
    }

    @Override
    protected Class<StageItemRecyclerViewAdapter> getAdapterType() {
        return StageItemRecyclerViewAdapter.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_action_stages;
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        LiveData<List<Stage>> ld = viewModel.getStages(mActionKey, this::onError);
        ld.observe(getViewLifecycleOwner(), this::reactState);
        ld.observe(getViewLifecycleOwner(), adapter::setValues);

        viewModel.getActionName(mActionKey, mActionName).observe(getViewLifecycleOwner(),
                (String text) -> ((AppCompatActivity)getActivity())
                                 .getSupportActionBar()
                                 .setTitle(getString(R.string.StagesFragment_label, text)));
    }

    @Override
    protected void onButtonAddClicked(View v) {
        Bundle args = new Bundle();
        args.putString(AddStageFragment.ARG_ACTION_KEY, mActionKey);
        navigator.navigate(R.id.transition_action_stages_to_add_stage, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((SmartActivity)getActivity())
                .getSupportActionBar()
                .setTitle(getString(R.string.StagesFragment_label, mActionName));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}