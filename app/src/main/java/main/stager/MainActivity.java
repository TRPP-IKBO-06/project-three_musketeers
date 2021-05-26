package main.stager;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import main.stager.Base.AuthorizedOnlyActivity;

public class MainActivity extends AuthorizedOnlyActivity {
    private NavBarViewModel navBarViewModel;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!dataProvider.isAuthorized()) return;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_about_me,
                R.id.nav_my_contacts,
                R.id.nav_contact_requests,
                R.id.nav_monitored_actions,
                R.id.nav_my_actions)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        String email = dataProvider.getEmail();
        if (!StagerApplication.getSettings().isEmailHidden(false))
            // Актуализируем email на сервере
            dataProvider.getUserEmail().setValue(email);

        navBarViewModel = new ViewModelProvider(this).get(NavBarViewModel.class);
        navBarViewModel.buildBackPath();
        UserAvatar ua = navigationView.getHeaderView(0)
                        .findViewById(R.id.user_avatar);
        ua.setEmail(email);
        navBarViewModel.getName().observe(this, ua::setUserName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}