package main.stager.utils.pushNotifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import main.stager.R;
import main.stager.StagerApplication;

public class EventNotificationBuilder {
    private Context context;

    public EventNotificationBuilder(@NonNull Context ctx) {
        context = ctx.getApplicationContext();
    }

    public PushNotification.PushNotificationBuilder
    getBasicEventNotification(@NonNull EventType event) {
        return PushNotification.builder()
                .setExtra(
                    StagerPushNotificationHandler.SENDER,
                    StagerApplication.getDataProvider().getUID())
                .setExtra(
                    StagerPushNotificationHandler.EVENT_TYPE,
                    event.name()
                );
    }

    public PushNotification.PushNotificationBuilder
    getSimpleEventNotification(@NonNull EventType event) {
        return getBasicEventNotification(event)
                .setTitle(context.getString(getTitle(event)))
                .setMessage(context.getString(getMessage(event)));
    }

    public static @StringRes int getMessage(@NonNull EventType event) {
        switch (event) {
            case FRIENDSHIP_REQUEST: return R.string.Notifications_message_FriendshipRequest;
            case FRIENDSHIP_REQUEST_ACCEPTED: return R.string.Notifications_message_FriendshipRequestAccepted;
            case ACTION_COMPLETED_ABORTED: return R.string.Notifications_message_ActionCompletedAborted;
            case ACTION_COMPLETED_SUCCEED: return R.string.Notifications_message_ActionCompletedSucceed;
        }
        throw new IllegalStateException("Unsupported EventType");
    }

    public static @StringRes int getTitle(@NonNull EventType event) {
        switch (event) {
            case FRIENDSHIP_REQUEST:
            case FRIENDSHIP_REQUEST_ACCEPTED:
                return R.string.Notifications_group_Friendship;
            case ACTION_COMPLETED_SUCCEED:
            case ACTION_COMPLETED_ABORTED:
                return R.string.Notifications_group_ActionCompleted;
        }
        throw new IllegalStateException("Unsupported EventType");
    }
}
