package main.stager.ui.add_item.add_stage;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;

import main.stager.list.feature.AddItemFragment;
import main.stager.utils.GainObservers.GainedObservable;
import main.stager.utils.DataProvider;
import main.stager.R;
import main.stager.model.Stage;
import main.stager.model.Status;
import main.stager.model.TriggerType;
import main.stager.utils.Utilits;
import main.stager.utils.validators.ActionNameValidator;
import main.stager.utils.validators.StageNameValidator;

public class AddStageFragment extends AddItemFragment {
    static public final String ARG_ACTION_KEY = "Stager.add_action_stage.param_action_key";
    private String mActionKey;
    private EditText inputName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mActionKey = Utilits.getDefaultOnNullOrBlank(
                getArguments().getString(ARG_ACTION_KEY),
                DataProvider.INVALID_ACTION_KEY
            );
        else
            mActionKey = DataProvider.INVALID_ACTION_KEY;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_add_stage;
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        inputName = view.findViewById(R.id.add_stage_input_name);
    }

    @Override
    protected void setDependencies() {
        super.setDependencies();
        dependencies.add(dataProvider.getAction(mActionKey));
    }

    @Override
    protected void saveChanges() {
        String name = inputName.getText().toString().trim();
        StageNameValidator stageNameValidator = new StageNameValidator(getContext());

        if (!stageNameValidator.isValid(name)) {
            inputName.setError(stageNameValidator.getMessage());
            inputName.requestFocus();
            return;
        }
        dataProvider.withRequestTracker(new GainedObservable()
                    .addOnItemGainListener(
            DataProvider.CBN.ADD_STAGE,
            t -> ((Task<Void>)t).addOnSuccessListener(e -> close())
        )).addStage(mActionKey, new Stage(
            Status.WAITING, name, TriggerType.MANUAL
        ));
    }

    @Override
    protected EditText getAutoFocusedView() {
        return inputName;
    }
}