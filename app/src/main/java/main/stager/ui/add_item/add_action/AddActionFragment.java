package main.stager.ui.add_item.add_action;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import main.stager.list.feature.AddItemFragment;
import main.stager.utils.GainObservers.GainedObservable;
import main.stager.utils.DataProvider;
import main.stager.R;
import main.stager.model.Status;
import main.stager.model.UserAction;
import main.stager.utils.validators.ActionNameValidator;

public class AddActionFragment extends AddItemFragment {

    private EditText inputName;

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_add_action;
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        inputName = view.findViewById(R.id.add_action_input_name);
    }

    @Override
    protected void saveChanges() {
        String name = inputName.getText().toString().trim();
        ActionNameValidator actionNameValidator = new ActionNameValidator(getContext());

        if (!actionNameValidator.isValid(name)) {
            inputName.setError(actionNameValidator.getMessage());
            inputName.requestFocus();
            return;
        }

        dataProvider.withRequestTracker(new GainedObservable()
                        .addOnItemGainListener(
                DataProvider.CBN.ADD_ACTION,
                t -> ((Task<Void>)t).addOnSuccessListener(e -> close())))
                .addAction(new UserAction(Status.WAITING, name));
    }

    @Override
    protected EditText getAutoFocusedView() {
        return inputName;
    }
}