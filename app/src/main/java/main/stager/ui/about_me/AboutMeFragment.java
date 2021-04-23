package main.stager.ui.about_me;

import android.content.Intent;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import main.stager.utils.ChangeListeners.OnLostFocusDBUpdater;
import main.stager.utils.DataProvider;
import main.stager.MainActivity;
import main.stager.R;
import main.stager.Base.StagerVMFragment;

public class AboutMeFragment extends StagerVMFragment<AboutMeViewModel> {
    private EditText inputName;
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
        view.findViewById(R.id.btn_exit_in_acc).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this.getActivity(), MainActivity.class));
            this.getActivity().finish();
        });
        inputName.setOnFocusChangeListener(new OnLostFocusDBUpdater(dataProvider.getUserName()));
        inputDescription.setOnFocusChangeListener(
                new OnLostFocusDBUpdater(dataProvider.getUserDescription()));
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        inputName = view.findViewById(R.id.personName);
        inputDescription = view.findViewById(R.id.secondName);
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        viewModel.getName().observe(getViewLifecycleOwner(), inputName::setText);
        viewModel.getDescription().observe(getViewLifecycleOwner(), inputDescription::setText);
    }
}