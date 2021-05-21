package main.stager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import lombok.Getter;
import main.stager.utils.DataProvider;
import main.stager.utils.pushNotifications.EventNotificationBuilder;
import main.stager.utils.pushNotifications.RequestQueueController;
import main.stager.utils.LocaleController;
import main.stager.utils.SettingsWrapper;
import main.stager.utils.pushNotifications.StagerPushNotificationHandler;

public class StagerApplication extends MultiDexApplication {
    @Getter private static SettingsWrapper settings;
    @Getter private static RequestQueueController requestQueueController;
    @Getter private static EventNotificationBuilder eventNotificationBuilder;
    @Getter private static StagerPushNotificationHandler pushNotificationHandler;
    @Getter private static DataProvider dataProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        settings = new SettingsWrapper(this);
        requestQueueController = new RequestQueueController(this);
        eventNotificationBuilder = new EventNotificationBuilder(this);
        pushNotificationHandler = new StagerPushNotificationHandler(this);
        dataProvider = new DataProvider();
        LocaleController.init(this);
        createNotificationChannel();
    }

    protected void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
            getString(R.string.Notification__channel_id),
            "StagerNotyChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);

        if (nm != null)
            nm.createNotificationChannel(channel);
    }

    public static void restart(final @NonNull Activity activity) {
        // Systems at 29/Q and later don't allow relaunch, but System.exit(0) on
        // all supported systems will relaunch ... but by killing the process, then
        // restarting the process with the back stack intact. We must make sure that
        // the launch activity is the only thing in the back stack before exiting.
        final PackageManager pm = activity.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(activity.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finishAffinity();
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        System.exit(0);
    }
}