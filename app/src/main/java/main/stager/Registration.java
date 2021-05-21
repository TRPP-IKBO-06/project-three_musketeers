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

    private void init() {
        edNickname = findViewById(R.id.nickname);
        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.password);
        edPasswordCheck = findViewById(R.id.check_password);

        // Регистрация аккаунта
        findViewById(R.id.success_btn).setOnFocusChangeListener(
                (v, hasFocus) -> onClickRegistration()
        );

        // Переход на вход
        findViewById(R.id.sign_in).setOnClickListener(
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

        // Пустое поле имени
        if (name.isEmpty()) {
            edNickname.setError("Name is required");
            edNickname.requestFocus();
            return;
        }

        // Длина имени меньше 5
        if (name.length() < 5) {
            edNickname.setError("Minimum length of name should be 5");
            edNickname.requestFocus();
            return;
        }

        // Пустое поле email
        if (email.isEmpty()) {
            edEmail.setError("Email is required");
            edEmail.requestFocus();
            return;
        }

        // Некорректный ввод логина
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Please enter a valid email");
            edEmail.requestFocus();
            return;
        }

        // Пустое поле пароля
        if (password.isEmpty()) {
            edPassword.setError("Password is required");
            edPassword.requestFocus();
            return;
        }

        // Некорректная длина пароля
        if (password.length() < 6) {
            edPassword.setError("Minimum length of password should be 6");
            edPassword.requestFocus();
            return;
        }

        // Пароли не совпадают
        if (!password.equals(passwordCheck)) {
             edPassword.setError("Passwords don't match");
             edPasswordCheck.setError("Passwords don't match");
             edPassword.requestFocus();
             edPasswordCheck.requestFocus();
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
