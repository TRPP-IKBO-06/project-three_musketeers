package main.stager.utils.pushNotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;
import main.stager.MainActivity;
import main.stager.R;
import main.stager.utils.Utilits;

public class StagerPushNotificationHandler {
    private Context context;
    private final Bitmap largeIcon;
    private final PendingIntent openAppIntent;

    public static final String EVENT_DETAILS = "event_details";
    private static final String SEP = ".";
    public static final String EVENT_TYPE = EVENT_DETAILS+SEP+"type";
    public static final String SENDER = EVENT_DETAILS+SEP+"sender";

    public StagerPushNotificationHandler(@NonNull Context ctx) {
        context = ctx.getApplicationContext();
        largeIcon = BitmapFactory.decodeResource(
            context.getResources(),
            R.mipmap.ic_launcher_round
        );
        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openAppIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        );
    }

    public void handleAny(NotificationCompat.Builder builder, RemoteMessage message) {
        handleBasicNotification(builder, message);
        try {
            String event = message.getData().get(EVENT_TYPE);
            if (event == null) return;
            EventType eventType = EventType.valueOf(event);
            handleEvent(builder, message, eventType);
        } catch (IllegalArgumentException ignore) {}
    }

    public void handleBasicNotification(NotificationCompat.Builder builder, RemoteMessage message) {
        builder.setSmallIcon(R.mipmap.ic_launcher)
               .setLargeIcon(largeIcon)
               .setAutoCancel(true)
               .setContentIntent(openAppIntent);

        String title = message.getData().get(PushNotification.TITLE);
        if (!Utilits.isNullOrBlank(title))
            builder.setContentTitle(title);

        String body = message.getData().get(PushNotification.MESSAGE);
        if (!Utilits.isNullOrBlank(body))
            builder.setContentText(body);
    }

    public void handleEvent(NotificationCompat.Builder builder, RemoteMessage message,
                               @NonNull EventType event) {
        builder.setContentTitle(context.getString(EventNotificationBuilder.getTitle(event)))
               .setContentText(context.getString(EventNotificationBuilder.getMessage(event)));
        switch (event) {
            case FRIENDSHIP_REQUEST: handleFriendshipRequest(builder, message);
            break;
        }
    }

    public void handleFriendshipRequest(NotificationCompat.Builder builder, RemoteMessage message) {
        // Do something
    }
}
