package main.stager.ui.about_me;

import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import main.stager.MainActivity;
import main.stager.R;
import main.stager.Base.StagerVMFragment;

public class AboutMeFragment extends StagerVMFragment<AboutMeViewModel> {

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
            dataProvider.unsubscribeInitial();

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this.getActivity(), MainActivity.class));
            this.getActivity().finish();
        });
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        bindDataTwoWay(viewModel.getName(), view.findViewById(R.id.personName), false);
        bindDataTwoWay(viewModel.getDescription(), view.findViewById(R.id.personDescription), false);
    }
}