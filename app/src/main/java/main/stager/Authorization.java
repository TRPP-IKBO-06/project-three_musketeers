package main.stager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import main.stager.Base.SmartActivity;
import main.stager.utils.DataProvider;

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
            getSupportActionBar().setTitle(R.string.RegistrationActivity_Button_SignIn);

        init();
    }

    private void init() {

        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.password);

        // Переход на восстановления пароля
        findViewById(R.id.btn_restore_acc).setOnClickListener(v -> resetPassword());

        // Переход на регистрацию
        findViewById(R.id.btn_registration).setOnClickListener(v -> {
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
        });

        findViewById(R.id.success_btn).setOnFocusChangeListener((v, hasFocus) -> {
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

        // Пустое поле email
        if (email.isEmpty()) {
            edEmail.setError(getResources().getString(
                    R.string.AuthorizationActivity_ErrorMessage_NoEmail));
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

        // Пустое поле email
        if (email.isEmpty()) {
            edEmail.setError(getResources().
                    getString(R.string.AuthorizationActivity_ErrorMessage_NoEmail));
            edEmail.requestFocus();
            return;
        }

        // Некорректный ввод логина
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError(getResources().
                    getString(R.string.AuthorizationActivity_ErrorMessage_NoValidEmail));
            edEmail.requestFocus();
            return;
        }

        // Пустое поле пароля
        if (password.isEmpty()) {
            edPassword.setError(getResources().
                    getString(R.string.AuthorizationActivity_ErrorMessage_NoPassword));
            edPassword.requestFocus();
            return;
        }

        // Некорректная длина пароля
        if (password.length() < 6) {
            edPassword.setError(getResources().
                    getString(R.string.AuthorizationActivity_ErrorMessage_MinLengthPassword));
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
