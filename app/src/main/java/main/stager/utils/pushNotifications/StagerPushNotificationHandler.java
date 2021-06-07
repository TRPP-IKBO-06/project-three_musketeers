package main.stager.utils.pushNotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import main.stager.MainActivity;
import main.stager.MyFirebaseMessagingService.Callback;
import main.stager.R;
import main.stager.StagerApplication;
import main.stager.model.ContactType;
import main.stager.ui.contact_info.ContactInfoFragment;
import main.stager.ui.monitored_action.MonitoredActionFragment;
import main.stager.utils.SettingsWrapper;
import main.stager.utils.Utilits;

public class StagerPushNotificationHandler {
    private Context context;
    private final Bitmap largeIcon;
    private final SettingsWrapper settings;

    public static final String EVENT_DETAILS = "event_details";
    private static final String SEP = ".";
    public static final String EVENT_TYPE = EVENT_DETAILS+SEP+"type";
    public static final String ACTION = EVENT_DETAILS+SEP+"action_id";
    public static final String SENDER = EVENT_DETAILS+SEP+"sender";

    public StagerPushNotificationHandler(@NonNull Context ctx) {
        context = ctx.getApplicationContext();
        largeIcon = BitmapFactory.decodeResource(
            context.getResources(),
            R.mipmap.ic_launcher_round
        );
        settings = StagerApplication.getSettings();
    }

    public void handleAny(NotificationCompat.Builder builder,
                          RemoteMessage message,
                          Callback callback) {
        handleBasicNotification(builder, message);
        try {
            String event = message.getData().get(EVENT_TYPE);
            if (event == null) {
                callback.notify(builder.build());
                return;
            }
            EventType eventType = EventType.valueOf(event);
            if (!handleEvent(builder, message, eventType, callback))
                return;
        } catch (IllegalArgumentException ignore) {}
        callback.notify(builder.build());
    }

    private void handleBasicNotification(NotificationCompat.Builder builder, RemoteMessage message) {
        builder.setSmallIcon(R.mipmap.ic_launcher)
               .setLargeIcon(largeIcon)
               .setAutoCancel(true)
               .setContentIntent(PendingIntent.getActivity(
                   context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT
               ));

        String title = message.getData().get(PushNotification.TITLE);
        if (!Utilits.isNullOrBlank(title))
            builder.setContentTitle(title);

        String body = message.getData().get(PushNotification.MESSAGE);
        if (!Utilits.isNullOrBlank(body))
            builder.setContentText(body);
    }

    private boolean handleEvent(NotificationCompat.Builder builder,
                                RemoteMessage message,
                                @NonNull EventType event,
                                Callback callback) {
        builder.setContentTitle(context.getString(EventNotificationBuilder.getTitle(event)))
               .setContentText(context.getString(EventNotificationBuilder.getMessage(event)));
        switch (event) {
            case FRIENDSHIP_REQUEST:
                return handleFriendshipRequest(builder, message, event, callback);
            case FRIENDSHIP_REQUEST_ACCEPTED:
                return handleFriendshipRequestAccepted(builder, message, event, callback);
            case ACTION_COMPLETED_SUCCEED:
            case ACTION_COMPLETED_ABORTED:
                return handleActionCompleted(builder, message, event, callback);
        }
        return true;
    }

    private boolean handleFriendshipRequest(NotificationCompat.Builder builder,
                                            RemoteMessage message,
                                            @NonNull EventType selfEvent,
                                            Callback callback) {
        if (!settings.isNotifyFriendshipRequestAllowed(true))
            return false;

        String owner = message.getData().get(SENDER);
        if (Utilits.isNullOrBlank(owner))
            return false;

        builder.setGroup(selfEvent.name());

        Bundle args = new Bundle();
        args.putString(ContactInfoFragment.ARG_CONTACT_TYPE,
                       ContactType.INCOMING.name());
        args.putString(ContactInfoFragment.ARG_CONTACT_KEY, owner);
        addOnClickTransition(builder, new FragmentsStack()
            .addDestination(R.id.nav_contact_requests)
            .addDestination(R.id.nav_contact_info, args)
        );
        return true;
    }

    private boolean handleFriendshipRequestAccepted(NotificationCompat.Builder builder,
                                                    RemoteMessage message,
                                                    @NonNull EventType selfEvent,
                                                    Callback callback) {
        if (!settings.isNotifyFriendshipRequestAcceptedAllowed(false))
            return false;

        String owner = message.getData().get(SENDER);
        if (Utilits.isNullOrBlank(owner))
            return false;

        builder.setGroup(selfEvent.name());
        Bundle args = new Bundle();
        args.putString(ContactInfoFragment.ARG_CONTACT_TYPE,
                       ContactType.ACCEPTED.name());
        args.putString(ContactInfoFragment.ARG_CONTACT_KEY, owner);
        addOnClickTransition(builder, new FragmentsStack()
            .addDestination(R.id.nav_contact_requests)
            .addDestination(R.id.nav_contact_info, args)
        );
        return true;
    }

    private boolean handleActionCompleted(NotificationCompat.Builder builder,
                                          RemoteMessage message,
                                          @NonNull EventType selfEvent,
                                          Callback callback) {
        String action = message.getData().get(ACTION);
        String owner = message.getData().get(SENDER);
        if (Utilits.isNullOrBlank(action) ||
            Utilits.isNullOrBlank(owner))
            return false;

        if (!StagerApplication.getListenedEventsController().isEventListened(
                selfEvent.equals(EventType.ACTION_COMPLETED_ABORTED)
                ? StagerApplication.getDataProvider().getActionCompleteAbortedEventName(owner, action)
                : StagerApplication.getDataProvider().getActionCompleteSucceedEventName(owner, action)
            ))
            return false;

        builder.setGroup(selfEvent.name());
        Bundle args = new Bundle();
        args.putString(MonitoredActionFragment.ARG_ACTION_KEY, action);
        args.putString(MonitoredActionFragment.ARG_ACTION_OWNER, owner);
        addOnClickTransition(builder, new FragmentsStack()
            .addDestination(R.id.nav_monitored_actions)
            .addDestination(R.id.nav_monitored_action, args)
        );
        return true;
    }

    private void addOnClickTransition(NotificationCompat.Builder builder,
                                      FragmentsStack path) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_OPEN_FRAGMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.ARG_FRAGMENT_ID_LIST,
                        path.getFragments());

        for (Map.Entry<Integer, Bundle> entry:
                path.getFragmentArgs().entrySet())
            intent.putExtra(MainActivity.ARG_FRAGMENT_ARGS_BY_ID_LIST
                            + entry.getKey(), entry.getValue());

        builder.setContentIntent(PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_ONE_SHOT
        ));
    }

    private static class FragmentsStack {
        @Getter private Map<Integer, Bundle> fragmentArgs = new HashMap<>();
        @Getter private ArrayList<Integer> fragments = new ArrayList<>();

        public FragmentsStack addDestination(@IdRes int fragment) {
            return addDestination(fragment, null);
        }

        public FragmentsStack addDestination(@IdRes int fragment, @Nullable Bundle args) {
            fragmentArgs.put(fragment, args);
            fragments.add(fragment);
            return this;
        }
    }
}
