package main.stager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import main.stager.Base.SmartActivity;
import main.stager.utils.DataProvider;
import main.stager.utils.validators.EmailValidator;
import main.stager.utils.validators.NameValidator;
import main.stager.utils.validators.PasswordValidator;


public class Registration extends SmartActivity {
    private EditText edNickname, edEmail, edPassword, edPasswordCheck;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fbAuth = FirebaseAuth.getInstance();

        // Проверка на то что пользователь не авторизован
        if (fbAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registr_form);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFocusAtInput(edNickname);
    }

    private void init() {
        edNickname = findViewById(R.id.input_nickname);
        edEmail = findViewById(R.id.input_email_registration);
        edPassword = findViewById(R.id.input_password_registration);
        edPasswordCheck = findViewById(R.id.input_password_check);

        // Регистрация аккаунта
        findViewById(R.id.btn_sign_in).setOnFocusChangeListener(
                (v, hasFocus) -> onClickRegistration()
        );

        // Переход на вход
        findViewById(R.id.btn_back_to_log_in).setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, Authorization.class);
                    startActivity(intent);
                }
        );
    }

    private void onClickRegistration() {

        String name = edNickname.getText().toString();
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        String passwordCheck = edPasswordCheck.getText().toString();

        // Проверка на интернет-соединение
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(),
                    "No internet connection",Toast.LENGTH_LONG).show();
            return;
        }

        NameValidator nameValidator = new NameValidator(getApplicationContext());
        if (!nameValidator.isValid(name)) {
            edNickname.setError(nameValidator.getMessage());
            edNickname.requestFocus();
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

        // Пароли не совпадают
        if (!password.equals(passwordCheck)) {
             edPassword.setError(getString(
                     R.string.RegistrationActivity_ErrorMessage_PasswordNotMatch));
             edPasswordCheck.setError(getString(
                     R.string.RegistrationActivity_ErrorMessage_PasswordNotMatch));
             edPassword.requestFocus();
             return;
        }

        showLoadingScreen();

        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    hideLoadingScreen();
                    if (task.isSuccessful()) {
                        DataProvider dp = StagerApplication.getDataProvider();
                        dp.subscribeInitial();
                        dp.getUserName().setValue(name);
                        // Пользователь зарегистрировался и вошел в аккаунт
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        // Получение ошибок при регистрации
                        Toast.makeText(getApplicationContext(),
                                task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
