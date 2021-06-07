package main.stager.ui.about_me;

import android.content.Intent;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import main.stager.Authorization;
import main.stager.R;
import main.stager.Base.StagerVMFragment;
import main.stager.utils.ChangeListeners.firebase.front.OnLostFocusDBUpdater;
import main.stager.utils.validators.DescriptionValidator;
import main.stager.utils.validators.NameValidator;

public class AboutMeFragment extends StagerVMFragment<AboutMeViewModel> {
    private EditText inputPersonName;
    private EditText inputDescription;

    @Override
    protected Class<AboutMeViewModel> getViewModelType() {
        return AboutMeViewModel.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_about_me;
    }

    @Override
    protected void setEventListeners() {
        super.setEventListeners();

        view.findViewById(R.id.btn_change_pass).setOnClickListener(
                v -> navigator.navigate(R.id.transition_about_me_to_update_password));

        view.findViewById(R.id.btn_exit_in_acc).setOnClickListener(v -> {
            dataProvider.unsubscribeAll();

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this.getActivity(), Authorization.class));
            this.getActivity().finish();
        });
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();

        inputPersonName = view.findViewById(R.id.personName);
        inputDescription = view.findViewById(R.id.personDescription);
    }

    @Override
    protected void setObservers() {
        super.setObservers();

        bindDataTwoWay(viewModel.getName(), inputPersonName, ref -> {
            inputPersonName.setOnFocusChangeListener(
                new OnLostFocusDBUpdater(ref.getRef(), false) {
                    @Override
                    protected boolean isValid(Object value) {
                        NameValidator nameValidator = new NameValidator(getContext());
                        if (!nameValidator.isValid((String) value)) {
                            inputPersonName.setError(nameValidator.getMessage());
                            return false;
                        } else return super.isValid(value);
                    }
                }
            );
        });

        bindDataTwoWay(viewModel.getDescription(), inputDescription, ref -> {
            inputDescription.setOnFocusChangeListener(
                new OnLostFocusDBUpdater(ref.getRef(), false) {
                    @Override
                    protected boolean isValid(Object value) {
                        DescriptionValidator descriptionValidator = new DescriptionValidator(getContext());
                        if (!descriptionValidator.isValid((String) value)) {
                            inputDescription.setError(descriptionValidator.getMessage());
                            return false;
                        } else return super.isValid(value);
                    }
                }
            );
        });

    }
}