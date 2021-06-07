package main.stager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import main.stager.Base.SmartActivity;
import main.stager.utils.validators.EmailValidator;
import main.stager.utils.validators.PasswordValidator;

public class Authorization extends SmartActivity {

    private EditText edEmail, edPassword;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fbAuth = FirebaseAuth.getInstance();

        if (fbAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_form);

        // Устанавливает название активности, вместо названия приложения
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.RegistrationActivity_Button_Log_In);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFocusAtInput(edEmail);
    }

    private void init() {
        edEmail = findViewById(R.id.input_email_log_in);
        edPassword = findViewById(R.id.input_password_log_in);

        // Переход на восстановления пароля
        findViewById(R.id.btn_restore_acc).setOnClickListener(v -> resetPassword());

        // Переход на регистрацию
        findViewById(R.id.btn_registration).setOnClickListener(v -> {
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_log_in).setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) signIn();
        });
    }

    private void resetPassword() {
        String email = edEmail.getText().toString();

        // Проверка на интернет-соединение
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.AuthorizationActivity_ErrorMessage_NoInternet),
                    Toast.LENGTH_LONG).show();
            return;
        }

        EmailValidator emailValidator = new EmailValidator(getApplicationContext());
        if (!emailValidator.isValid(email)) {
            edEmail.setError(emailValidator.getMessage());
            edEmail.requestFocus();
            return;
        }


        fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.AuthorizationActivity_Toast_CheckEmail),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.AuthorizationActivity_Toast_ErrorRestore),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signIn() {

        // Вход в аккаунт
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();

        // Проверка на интернет-соединение
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(),
                    getResources().
                            getString(R.string.AuthorizationActivity_ErrorMessage_NoInternet),
                                    Toast.LENGTH_LONG).show();
            return;
        }

        EmailValidator emailValidator = new EmailValidator(getApplicationContext());
        if (!emailValidator.isValid(email)) {
            edEmail.setError(emailValidator.getMessage());
            edEmail.requestFocus();
            return;
        }

        PasswordValidator passwordValidator = new PasswordValidator(getApplicationContext());
        if (!passwordValidator.isValid(password)) {
            edPassword.setError(passwordValidator.getMessage());
            edPassword.requestFocus();
            return;
        }

        showLoadingScreen();

        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            hideLoadingScreen();
            if (task.isSuccessful()) {
                StagerApplication.getDataProvider().subscribeInitial();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else if (task.getException() != null)
                Toast.makeText(getApplicationContext(),
                        getResources().
                                getString(R.string.AuthorizationActivity_Toast_ErrorAuthorization),
                        Toast.LENGTH_LONG).show();
            });
    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        return cm.getActiveNetworkInfo() != null;
    }
}
