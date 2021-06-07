package main.stager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        StagerApplication.getPushNotificationHandler().handleAny(
            new NotificationCompat.Builder(
                getApplicationContext(),
                getString(R.string.Notification__channel_id)
            ), remoteMessage, this::notify
        );
    }

    public void notify(android.app.Notification noty) {
        NotificationManagerCompat.from(getApplicationContext()).notify(
                new Random().nextInt(3000), noty
        );
    }

    @FunctionalInterface public interface Callback {
        void notify(android.app.Notification noty);
    }
}