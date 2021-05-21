package main.stager.ui.edit_item.edit_stage;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import main.stager.Base.SmartActivity;
import main.stager.Base.StagerVMFragment;
import main.stager.R;
import main.stager.model.Status;
import main.stager.utils.Utilits;

public class EditStageFragment extends StagerVMFragment<EditStageViewModel> {
    static public final String ARG_STAGE_NAME = "Stager.edit_item.stage.param_stage_name";
    static public final String ARG_ACTION_KEY = "Stager.edit_item.stage.param_action_key";
    static public final String ARG_STAGE_KEY = "Stager.edit_item.stage.param_stage_key";
    private EditText editStageName;
    private String mStageName;
    private String mActionKey;
    private String mStageKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStageName = Utilits.getDefaultOnNullOrBlank(getArguments().getString(ARG_STAGE_NAME),
                    getString(R.string.EditStageFragment_message_UntitledStage));
            mActionKey = getArguments().getString(ARG_ACTION_KEY);
            mStageKey = getArguments().getString(ARG_STAGE_KEY);
        } else {
            mStageName = getString(R.string.EditStageFragment_message_UntitledStage);
            mActionKey = "";
            mStageKey = "";
        }
    }

    @Override
    protected Class<EditStageViewModel> getViewModelType() {
        return EditStageViewModel.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_edit_stage;
    }

    private void updateTitle(String text) {
        getActionBar().setTitle(Utilits.getDefaultOnNullOrBlank(text,
            getString(R.string.EditStageFragment_message_UntitledStage
        )));
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        bindData(viewModel.getStageName(), this::updateTitle);
        bindDataTwoWay(viewModel.getStageName(), editStageName);
        bindData(viewModel.getStageStatus(), (status) -> {
            view.findViewById(R.id.edit_stage_layout_controls)
                    .setVisibility(status == Status.WAITING ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void setEventListeners() {
        super.setEventListeners();
        view.findViewById(R.id.edit_stage_button_abort).setOnClickListener((l) -> {
            dataProvider.setStageStatusAborted(mActionKey, mStageKey);
        });
        view.findViewById(R.id.edit_stage_button_success).setOnClickListener((l) -> {
            dataProvider.setStageStatusSucceed(mActionKey, mStageKey);
        });
    }

    @Override
    protected void setDependencies() {
        super.setDependencies();
        dependencies.add(dataProvider.getAction(mActionKey));
        dependencies.add(dataProvider.getStage(mActionKey, mStageKey));
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        editStageName = view.findViewById(R.id.edit_stage_input_name);
        updateTitle(mStageName);

        // Задание начального значения
        // (не стоит выносить в updateTitle)
        editStageName.setText(mStageName);
    }

    @Override
    protected void setViewModelData() {
        super.setViewModelData();
        viewModel.setActionKey(mActionKey);
        viewModel.setStageKey(mStageKey);
    }
}