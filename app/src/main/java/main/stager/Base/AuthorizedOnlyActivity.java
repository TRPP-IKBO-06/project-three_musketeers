package main.stager.Base;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import main.stager.Authorization;
import main.stager.StagerApplication;
import main.stager.utils.DataProvider;
import main.stager.R;

public abstract class AuthorizedOnlyActivity extends SmartActivity {

    // Firebase
    protected static DataProvider dataProvider = StagerApplication.getDataProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Предотвращаем доступ неавторизованных пользователей
        if (!dataProvider.isAuthorized()) {
            startActivity(new Intent(this, Authorization.class));
            finish();
        }

        if (savedInstanceState == null)
            savedInstanceState = new Bundle();
        savedInstanceState.putBoolean(SHOW_ACTION_BAR, false);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Прикрепляем значок настроек на панель инструментов
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != R.id.action_settings)
            return super.onOptionsItemSelected(item);
        ((NavHostFragment)getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment))
            .getNavController().navigate(R.id.transition_to_settings);
        return true;
    }
}
