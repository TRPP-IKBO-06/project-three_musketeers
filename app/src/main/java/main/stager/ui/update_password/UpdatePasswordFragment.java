package main.stager.ui.update_password;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import main.stager.Base.StagerFragment;
import main.stager.R;

public class UpdatePasswordFragment extends StagerFragment {

    private EditText edOldPassword;
    private EditText edNewPassword;
    private EditText edNewPasswordConfirm;

    @Override
    protected void setEventListeners() {
        super.setEventListeners();

        view.findViewById(R.id.btn_NewPassword).setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {

                String oldPassword = edOldPassword.getText().toString();

                if (oldPassword.isEmpty() || oldPassword.length() < 6) {
                    edOldPassword.setError(getResources().getString(
                            R.string.UpdatePasswordFragment_ErrorMessage_MinLengthPassword));
                    edOldPassword.requestFocus();
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null || user.getEmail() == null)
                    return;

                showLoadingScreen();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oldPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            hideLoadingScreen();
                            if (task.isSuccessful()) {
                                view.findViewById(R.id.layout_OldPassword).setVisibility(View.GONE);
                                view.findViewById(R.id.layout_UpdatePassword).setVisibility(View.VISIBLE);
                            } else {
                                edOldPassword.setError(getResources().
                                        getString(R.string.UpdatePasswordFragment_ErrorMessage_PasswordIsNotCorrect));
                                edOldPassword.requestFocus();
                            }
                        });
            }
        });

        view.findViewById(R.id.btn_ChangePassword).setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) return;

            String newPassword = edNewPassword.getText().toString();
            String newPasswordConfirm = edNewPasswordConfirm.getText().toString();

            if (newPassword.isEmpty() || newPassword.length() < 6) {
                edNewPassword.setError(getResources().getString(
                        R.string.UpdatePasswordFragment_ErrorMessage_MinLengthPassword));
                edNewPassword.requestFocus();
                return;
            }

            if (!newPassword.equals(newPasswordConfirm)) {
                edNewPassword.setError(getResources().
                        getString(R.string.UpdatePasswordFragment_ErrorMessage_PasswordsDoNotMatch));
                edNewPasswordConfirm.setError(getResources().
                        getString(R.string.UpdatePasswordFragment_ErrorMessage_PasswordsDoNotMatch));
                edNewPasswordConfirm.requestFocus();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null)
                return;

            showLoadingScreen();

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        hideLoadingScreen();
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), getResources().
                                    getString(R.string.UpdatePasswordFragment_Toast_PasswordUpdate),
                                    Toast.LENGTH_LONG).show();
                            navigator.navigate(R.id.nav_about_me);
                        } else {
                            Toast.makeText(getContext(), getResources().
                                    getString(R.string.UpdatePasswordFragment_ErrorMessage_PasswordNotUpdate),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_update_password;
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        edOldPassword = view.findViewById(R.id.edit_OldPassword);
        edNewPassword = view.findViewById(R.id.edit_text_new_password);
        edNewPasswordConfirm = view.findViewById(R.id.edit_text_new_password_confirm);
    }
}
