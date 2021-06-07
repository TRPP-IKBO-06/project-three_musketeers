package main.stager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import main.stager.Base.AuthorizedOnlyActivity;

public class MainActivity extends AuthorizedOnlyActivity {
    private NavBarViewModel navBarViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isCreated = false;
    public static final String ACTION_OPEN_FRAGMENT = "Stager.main.actions.open_fragment";
    public static final String ARG_FRAGMENT_ID_LIST = "Stager.main.MainActivity.param.fragments.id";
    public static final String ARG_FRAGMENT_ARGS_BY_ID_LIST = "Stager.main.MainActivity.param.fragments.args_for_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!dataProvider.isAuthorized()) return;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_about_me,
            R.id.nav_my_contacts,
            R.id.nav_contact_requests,
            R.id.nav_monitored_actions,
            R.id.nav_my_actions
        ).setOpenableLayout(drawer).build();
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
                        .findViewById(R.id.nav_user_avatar);
        ua.setEmail(email);
        navBarViewModel.getName().observe(this, ua::setUserName);

        Intent intent = getIntent();
        if (intent != null &&
            intent.getAction() != null &&
            intent.getAction().equals(ACTION_OPEN_FRAGMENT))
            onOpenFragmentIntent(intent);

        if (!isCreated) // Once per App creation
            dataProvider.setupAutoUnsubscribeUnavailable();

        isCreated = true; // Count created only if authorized
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    protected void onOpenFragmentIntent(@NotNull Intent intent) {
        List<Integer> fragments = intent.getIntegerArrayListExtra(ARG_FRAGMENT_ID_LIST);
        if (fragments == null)
            return;
        NavController navigator = Navigation.findNavController(this, R.id.nav_host_fragment);
        for (int fragment: fragments)
            navigator.navigate(fragment, intent.getBundleExtra(
                ARG_FRAGMENT_ARGS_BY_ID_LIST + fragment
            ));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null &&
                isCreated &&
                intent.getAction() != null &&
                intent.getAction().equals(ACTION_OPEN_FRAGMENT))
            onOpenFragmentIntent(intent);
    }
}