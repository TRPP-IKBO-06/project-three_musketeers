package main.stager.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;
import org.jetbrains.annotations.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.With;
import main.stager.utils.Utilits.IPredicate;
import main.stager.StagerApplication;
import main.stager.model.FBModel;
import main.stager.model.Stage;
import main.stager.model.Status;
import main.stager.model.UserAction;
import main.stager.utils.ChangeListeners.firebase.AKeySetEventListener;
import main.stager.utils.GainObservers.IGainedObservable;
import main.stager.utils.ChangeListeners.firebase.OnValueGet;
import main.stager.utils.pushNotifications.EventNotificationBuilder;
import main.stager.utils.pushNotifications.EventType;
import main.stager.utils.pushNotifications.ListenedEventsController;
import main.stager.utils.pushNotifications.PushNotification;
import main.stager.utils.pushNotifications.StagerPushNotificationHandler;

@AllArgsConstructor // Not recommended to use this constructor
public class DataProvider {

    //region INIT
    private FirebaseAuth mAuth;
    private FirebaseMessaging mMes;
    private ListenedEventsController mEvents;
    private EventNotificationBuilder mNotyGen;
    private DatabaseReference mRef;

    public DataProvider() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mMes = FirebaseMessaging.getInstance();
        mEvents = StagerApplication.getListenedEventsController();
        db.setPersistenceEnabled(true);
        mRef = db.getReference(PATH.MAIN_DB);
        mNotyGen = StagerApplication.getEventNotificationBuilder();
        keepSynced();

        requestTracker = new IGainedObservable() {};
    }

    private void keepSynced() {
        DatabaseReference[] sync = new DatabaseReference[]{
                getActions(),
                getAllStages()
        };

        for (DatabaseReference dr: sync)
            dr.keepSynced(true);
    }

    // CONSTANTS
    public static final String INVALID_UID = "__INVALID_UID_HERE__";
    public static final String INVALID_EMAIL = "__INVALID_EMAIL_HERE__";
    public static final String INVALID_STAGE_KEY = "__INVALID_STAGE_KEY_HERE__";
    public static final String INVALID_ACTION_KEY = "__INVALID_ACTION_KEY_HERE__";
    public static final String INVALID_CONTACT_KEY = "__INVALID_CONTACT_KEY_HERE__";

    private static final class PATH {
        // Common
        public static final String MAIN_DB = "stager-main-db";
        public static final String FB_POS = "pos";

        // User
        public static final String USER_INFO = "user_info";
        public static final String USER_NAME = "name";
        public static final String USER_DESCRIPTION = "description";
        public static final String USER_EMAIL = "email";

        // Actions
        public static final String ACTIONS = "actions";
        public static final String ACTION_STATUS = "status";
        public static final String ACTION_NAME = "name";

        // Action stages
        public static final String STAGES = "stages";
        public static final String STAGE_STATUS = "currentStatus";
        public static final String STAGE_NAME = "name";

        // Contacts
        public static final String CONTACTS = "contacts";

        // Contact requests
        public static final String CONTACT_REQUESTS = "contact_requests";
        public static final String IGNORE_CONTACTS = "ignore_contacts";

        // Shared actions
        public static final String SHARED = "shared";

        // Monitored
        public static final String MONITORED = "monitored";
    }

        //region EXTRA PARAMS

    public static final class CBN {
        public static final class SetStageStatus {
            public static final String UPDATE_STAGES = "update_stages";
            public static final String UPDATE_ACTION = "update_action";
        }

        public static final String RESET_ACTION_STATUS = "reset_action_status";
        public static final String INIT_POS = "init_pos";
        public static final String ADD_ACTION = "add_action";
        public static final String ADD_STAGE = "add_stage";
        public static final class Notification {
            public static final String FRIENDSHIP_REQUEST = "friendship_request";
            public static final String FRIENDSHIP_REQUEST_ACCEPTED = "friendship_request_accepted";
            public static final String ACTION_COMPLETED_ABORTED = "action_completed_aborted";
            public static final String ACTION_COMPLETED_SUCCEED = "action_completed_succeed";
        }
    }

    @With private IGainedObservable requestTracker;

        //endregion EXTRA PARAMS

    //endregion INIT

    //region User data

    /** Path safe, not null uid
     * @return uid or empty string
     */
    public @NotNull String getUID() {
        String uid = mAuth.getUid();
        return uid == null ? INVALID_UID : uid;
    }

    public @NotNull String getEmail() {
        if (mAuth.getCurrentUser() == null) return INVALID_EMAIL;
        String email = mAuth.getCurrentUser().getEmail();
        return email != null ? email : INVALID_EMAIL;
    }

    public boolean isAuthorized() {
        return mAuth.getCurrentUser() != null;
    }

    public DatabaseReference getUserInfo() {
        return getUserInfo(getUID());
    }

    public DatabaseReference getUserInfo(@NonNull String uid) {
        return getAllUserInfo().child(uid);
    }

    public DatabaseReference getAllUserInfo() {
        return mRef.child(PATH.USER_INFO);
    }

    public DatabaseReference getUserName() {
        return getUserInfo().child(PATH.USER_NAME);
    }

    public DatabaseReference getUserDescription() {
        return getUserInfo().child(PATH.USER_DESCRIPTION);
    }

    public DatabaseReference getUserEmail() {
        return getUserInfo().child(PATH.USER_EMAIL);
    }

    public Task<Void> saveEmail() {
        return getUserEmail().setValue(getEmail());
    }

    public Task<Void> deleteEmail() {
        return getUserEmail().removeValue();
    }

    public Query findUserByEmail(String email) {
        return getAllUserInfo().orderByChild(PATH.USER_EMAIL)
                .startAt(email).endAt(email+"\uf8ff");
    }

    public Query findUserByName(String name) {
        return getAllUserInfo().orderByChild(PATH.USER_NAME)
                .startAt(name).endAt(name+"\uf8ff");
    }

    //endregion User data

    //region Contact requests

    public DatabaseReference getContactRequests(@NonNull String from) {
        return getAllContactRequests().child(from);
    }

    public DatabaseReference getContactRequests() {
        return getContactRequests(getUID());
    }

    public DatabaseReference getIncomingContactRequest(@NonNull String from) {
        return getContactRequests().child(from);
    }

    public DatabaseReference getAllContactRequests() {
        return mRef.child(PATH.CONTACT_REQUESTS);
    }

    public Query getOutgoingContactRequests() {
        return getAllContactRequests().orderByChild(getUID()).startAt(false).endAt(true);
    }

    public DatabaseReference getOutgoingContactRequest(@NonNull String from) {
        return getContactRequests(from).child(getUID());
    }

    public DatabaseReference getIgnoredContactRequests() {
        return mRef.child(PATH.IGNORE_CONTACTS).child(getUID());
    }

     public Query getIgnoringContacts() {
        return mRef.child(PATH.IGNORE_CONTACTS).orderByChild(getUID()).startAt(false).endAt(true);
    }

    public DatabaseReference getIgnoredContactRequest(@NonNull String from) {
        return getIgnoredContactRequests().child(from);
    }

    @Trackable(keys = {CBN.Notification.FRIENDSHIP_REQUEST})
    public Task<Void> makeContactRequest(@NonNull String receiver) {
        Task<Void> t = getAllContactRequests().child(receiver).child(getUID()).setValue(true);
        t.addOnSuccessListener(v -> sendFriendshipRequestNoty(receiver));
        return t;
    }

    public Task<Void> removeOutgoingContactRequest(@NonNull String from) {
        return getOutgoingContactRequest(from).removeValue();
    }

    public Task<Void> removeIgnoredContactRequest(@NonNull String from) {
        return getIgnoredContactRequest(from).removeValue();
    }

    @Trackable(keys = {CBN.Notification.FRIENDSHIP_REQUEST_ACCEPTED})
    public Task<Void> acceptContactRequest(@NonNull String from) {
        sendFriendshipRequestAcceptedNoty(from);
        return batchedFromRoot()
                .setTrue(getContacts().child(from))
                .setTrue(getContacts(from).child(getUID()))
                .remove(getIncomingContactRequest(from))
                .remove(getIgnoredContactRequest(from))
                .apply();
    }

    public Task<Void> ignoreContactRequest(@NonNull String from) {
        return batchedFromRoot()
                .remove(getIncomingContactRequest(from))
                .setTrue(getIgnoredContactRequest(from))
                .apply();
    }

    //endregion Contact requests

    //region Monitor Actions

    public DatabaseReference getMonitored() {
        return mRef.child(PATH.MONITORED);
    }

    public DatabaseReference getMonitoredActionHolders(@NonNull String uid) {
        return getMonitored().child(uid);
    }

    public DatabaseReference getMonitoredActionHolders() {
        return getMonitoredActionHolders(getUID());
    }

    public DatabaseReference getMonitoredActions(@NonNull String uid,
                                                @NonNull String actionOwner) {
        return getMonitoredActionHolders(uid).child(actionOwner);
    }

    public DatabaseReference getMonitoredAction(@NonNull String uid,
                                                @NonNull String actionOwner,
                                                @NonNull String actionKey) {
        return getMonitoredActions(uid, actionOwner).child(actionKey);
    }

    public DatabaseReference getMonitoredAction(@NonNull String actionOwner,
                                                @NonNull String actionKey) {
        return getMonitoredAction(getUID(), actionOwner, actionKey);
    }

    public DatabaseReference getMonitoredActionStages(@NonNull String actionOwner,
                                                @NonNull String actionKey) {
        return getAllStages(actionOwner).child(actionKey);
    }

    //endregion Monitor Actions

    //region Share Actions

    public DatabaseReference getShared() {
        return mRef.child(PATH.SHARED);
    }

    public DatabaseReference getSubscribers(@NonNull String uid) {
        return getShared().child(uid);
    }
    public DatabaseReference getSubscribers() {
        return getSubscribers(getUID());
    }

    public Query getSubscribersOfAction(@NonNull String actionKey) {
        return getSubscribers().orderByChild(actionKey).startAt(false).endAt(true);
    }

    public DatabaseReference getSharedActions(@NonNull String sharedTo) {
        return getSubscribers().child(sharedTo);
    }

    public DatabaseReference getSharedMeActions(@NonNull String sharedFrom) {
        return getSubscribers(sharedFrom).child(getUID());
    }

    public DatabaseReference getSharedAction(@NonNull String sharedTo, @NonNull String key) {
        return getSharedActions(sharedTo).child(key);
    }

    public Task<Void> shareAction(@NonNull String sharedTo, @NonNull String key) {
        return batchedFromRoot()
                   .setTrue(getSharedAction(sharedTo, key))
                   .setTrue(getMonitoredAction(sharedTo, getUID(), key))
                   .apply();
    }

    public Task<Void> revokeSharedActionAccess(@NonNull String sharedTo, @NotNull String key) {
        return batchedFromRoot()
                .remove(getSharedAction(sharedTo, key))
                .remove(getMonitoredAction(sharedTo, getUID(), key))
                .apply();
    }

    //endregion Share Actions

    //region Actions

    public DatabaseReference getAllActions() {
        return mRef.child(PATH.ACTIONS);
    }

    public DatabaseReference getActions() {
        return getActions(getUID());
    }

    public DatabaseReference getActions(@NotNull String uid) {
        return getAllActions().child(uid);
    }

    public DatabaseReference getAction(@NotNull String uid, @NotNull String key) {
        return getActions(uid).child(key);
    }

    public DatabaseReference getAction(@NotNull String key) {
        return getAction(getUID(), key);
    }

    public DatabaseReference getActionStatus(@NotNull String key) {
        return getAction(key).child(PATH.ACTION_STATUS);
    }

    public DatabaseReference getActionName(@NotNull String key) {
        return getActionName(getUID(), key);
    }
    public DatabaseReference getActionName(@NotNull String owner,
                                           @NotNull String key) {
        return getAction(owner, key).child(PATH.ACTION_NAME);
    }

    @Trackable(keys = {CBN.ADD_ACTION, CBN.INIT_POS})
    public String addAction(UserAction ua) {
        String key = getActions().push().getKey();
        Task<Void> task = getAction(key).setValue(ua);
        requestTracker.postItem(CBN.ADD_ACTION, task);
        initPositions(getActions());
        return key;
    }

    public void deleteAction(@NotNull String key) {
        getSubscribersOfAction(key).addListenerForSingleValueEvent(new OnValueGet((snapshot) -> {
            BatchUpdate batched = batchedFromRoot()
                .remove(getStages(key))
                .remove(getAction(key));

            if (snapshot.exists() && snapshot.hasChildren())
                for (DataSnapshot post: snapshot.getChildren()) {
                    if (post.getKey() == null)
                        continue;
                    batched.remove(getSharedAction(post.getKey(), key));
                    batched.remove(getMonitoredAction(post.getKey(), getUID(), key));
                }
            batched.apply();
        }));
    }

    //endregion Actions

    //region Stages of action

    @Trackable(keys = {CBN.ADD_STAGE, CBN.INIT_POS, CBN.RESET_ACTION_STATUS})
    public String addStage(@NotNull String actionKey,
                           Stage stage) {
        String key = getStages(actionKey).push().getKey();
        Task<Void> task = getStage(actionKey, key).setValue(stage);
        requestTracker.postItem(CBN.ADD_STAGE, task);
        initPositions(getStages(actionKey));
        resetActionStatus(actionKey);
        return key;
    }

    public DatabaseReference getAllStages(@NotNull String uid) {
        return mRef.child(PATH.STAGES).child(uid);
    }

    public DatabaseReference getAllStages() {
        return getAllStages(getUID());
    }

    public DatabaseReference getStages(@NotNull String actionKey) {
        return getAllStages().child(actionKey);
    }

    public DatabaseReference getStage(@NotNull String actionKey, @NotNull String stageKey) {
        return getStages(actionKey).child(stageKey);
    }

    public DatabaseReference getStageName(@NotNull String actionKey, @NotNull String stageKey) {
        return getStage(actionKey, stageKey).child(PATH.STAGE_NAME);
    }

    public DatabaseReference getStageStatus(@NotNull String actionName, @NotNull String stageKey) {
        return getStage(actionName, stageKey).child(PATH.STAGE_STATUS);
    }

    @Trackable(keys = {CBN.RESET_ACTION_STATUS})
    public void resetActionStatus(@NotNull String actionKey) {
        BatchUpdate batch = batchedFromRoot();
        getSorted(getStages(actionKey)).addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists() || !snapshot.hasChildren())
                return;

            boolean first = true;
            for (DataSnapshot item: snapshot.getChildren())
                if (first) {
                    first = false;
                    batch.set(item.child(PATH.STAGE_STATUS).getRef(),
                              Status.EVALUATING);
                } else batch.set(item.child(PATH.STAGE_STATUS).getRef(),
                                 Status.WAITING);
            batch.set(getActionStatus(actionKey), Status.WAITING);
            requestTracker.postItem(CBN.RESET_ACTION_STATUS, batch.apply());
        }));
    }

    @Trackable(keys = {CBN.SetStageStatus.UPDATE_ACTION,
                       CBN.SetStageStatus.UPDATE_STAGES,
                       CBN.Notification.ACTION_COMPLETED_SUCCEED})
    public void setStageStatusSucceed(@NotNull String actionKey,
                                      @NotNull String stageKey) {
        DatabaseReference ref = getStages(actionKey);
        BatchUpdate batch = BatchUpdate.init(ref);
        getSorted(ref).addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists() || !snapshot.hasChildren())
                return;

            Stage target = snapshot.child(stageKey).getValue(Stage.class);
            if (target == null)
                return;

            if (target.getCurrentStatus() != Status.EVALUATING)
                return;

            boolean targetFound = false;
            Stage stage;
            String nextEval = null;
            for (DataSnapshot item: snapshot.getChildren()) {
                stage = item.getValue(Stage.class);

                if (stage == null)
                    return;

                if (stage.getCurrentStatus() == Status.EVALUATING) {
                    targetFound = true;
                    continue;
                }

                if (targetFound
                    ? stage.getCurrentStatus() != Status.WAITING
                    : stage.getCurrentStatus() != Status.SUCCEED)
                    return;

                if (nextEval == null && targetFound)
                    nextEval = item.getKey();
            }
            if (!targetFound) return;

            batch.set(snapshot.child(stageKey)
                    .child(PATH.STAGE_STATUS)
                    .getRef(), Status.SUCCEED);

            if (nextEval != null)
                batch.set(snapshot.child(nextEval)
                        .child(PATH.STAGE_STATUS)
                        .getRef(), Status.EVALUATING);

            Task<Void> task = batch.apply();
            requestTracker.postItem(CBN.SetStageStatus.UPDATE_STAGES, task);

            if (nextEval != null)
                return;

            task.addOnSuccessListener(t -> {
                requestTracker.postItem(
                    CBN.SetStageStatus.UPDATE_ACTION,
                    getActionStatus(actionKey)
                    .setValue(Status.SUCCEED));
                sendActionCompletedSucceedNoty(getUID(), actionKey);
            });
        }));
    }

    @Trackable(keys = {CBN.SetStageStatus.UPDATE_ACTION,
                       CBN.SetStageStatus.UPDATE_STAGES,
                       CBN.Notification.ACTION_COMPLETED_ABORTED})
    public void setStageStatusAborted(@NotNull String actionKey,
                                      @NotNull String stageKey) {
        DatabaseReference ref = getStages(actionKey);
        BatchUpdate batch = BatchUpdate.init(ref);
        getSorted(ref).addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists())
                return;

            Stage target = snapshot.child(stageKey).getValue(Stage.class);
            if (target == null)
                return;

            if (target.getCurrentStatus() != Status.EVALUATING)
                return;

            boolean targetFound = false;
            Stage stage;
            for (DataSnapshot item: snapshot.getChildren()) {
                stage = item.getValue(Stage.class);

                if (stage == null || item.getKey() == null)
                    return;

                if (stage.getCurrentStatus() == Status.EVALUATING) {
                    targetFound = true;
                    continue;
                }

                if (targetFound
                    ? stage.getCurrentStatus() != Status.WAITING
                    : stage.getCurrentStatus() != Status.SUCCEED)
                    return;

                if (targetFound)
                    batch.set(snapshot.child(item.getKey())
                                      .child(PATH.STAGE_STATUS)
                                      .getRef(), Status.LOCKED);
            }
            if (!targetFound) return;

            batch.set(snapshot.child(stageKey)
                              .child(PATH.STAGE_STATUS)
                              .getRef(), Status.ABORTED);

            Task<Void> task = batch.apply();
            requestTracker.postItem(CBN.SetStageStatus.UPDATE_STAGES, task);
            task.addOnSuccessListener(t -> {
                requestTracker.postItem(
                    CBN.SetStageStatus.UPDATE_ACTION,
                    getActionStatus(actionKey)
                    .setValue(Status.ABORTED));
                sendActionCompletedAbortedNoty(getUID(), actionKey);
            });
        }));
    }

    public void deleteStage(@NotNull String actionKey, @NotNull String stageKey) {
        getStage(actionKey, stageKey).removeValue();
        resetActionStatus(actionKey);
    }

    //endregion Stages of action

    //region Contacts

    public DatabaseReference getContacts() {
        return getContacts(getUID());
    }

    public DatabaseReference getContact(@NonNull String of) {
        return getContacts(getUID()).child(of);
    }

    public DatabaseReference getContacts(@NonNull String uid) {
        return mRef.child(PATH.CONTACTS).child(uid);
    }

    public Task<Void> deleteContact(@NonNull String uid) {
        return batchedFromRoot()
                .remove(getContacts().child(uid))
                .remove(getContacts(uid).child(getUID()))
                .remove(getSharedActions(uid))
                .remove(getSharedMeActions(uid))
                .remove(getMonitoredActions(uid, getUID()))
                .remove(getMonitoredActions(getUID(), uid))
                .apply();
    }

    //endregion Contacts

    //region Notifications

    private static final String NT_SEP = "-";

        //region EventNames

    public List<String> getInitialEventNames() {
        List<String> list = new ArrayList<>();
        if (StagerApplication.getSettings()
                             .isNotifyFriendshipRequestAllowed(true))
            list.add(getFriendshipRequestEventName(getUID()));
        return list;
    }

    public String getFriendshipRequestEventName(@NonNull String uid) {
        return EventType.FRIENDSHIP_REQUEST +NT_SEP+ uid;
    }

    public String getFriendshipRequestAcceptedEventName(@NonNull String uid) {
        return EventType.FRIENDSHIP_REQUEST_ACCEPTED +NT_SEP+ uid;
    }

    public String getActionCompleteSucceedEventName(@NonNull String ownerUID,
                                                    @NonNull String actionKey) {
        return EventType.ACTION_COMPLETED_SUCCEED +NT_SEP+ ownerUID +NT_SEP+ actionKey;
    }

    public String getActionCompleteAbortedEventName(@NonNull String ownerUID,
                                                    @NonNull String actionKey) {
        return EventType.ACTION_COMPLETED_ABORTED +NT_SEP+ ownerUID +NT_SEP+ actionKey;
    }

        //endregion EventNames

        //region Subscribe

    public void subscribeInitial() {
        mEvents.reset();
        for (String eventName: getInitialEventNames())
            subscribe(eventName);
    }

    public void unsubscribeAll() {
        for (String eventName: mEvents.getListenedEvents())
            unsubscribe(eventName);
        mEvents.reset();
    }

    public void setSubscribe(@NonNull String eventName, boolean subscribe) {
        if (subscribe)
            subscribe(eventName);
        else
            unsubscribe(eventName);
    }

    public void subscribe(@NonNull String eventName) {
        mMes.subscribeToTopic(eventName);
        mEvents.setEventListened(eventName);
    }

    public void unsubscribe(@NonNull String eventName) {
        mMes.unsubscribeFromTopic(eventName);
        mEvents.setEventNotListened(eventName);
    }

    public void setupAutoUnsubscribeUnavailable() {
        getMonitoredActionHolders().addValueEventListener(
            new AKeySetEventListener(null) {
                @Override
                public void onDataChangeFinished(Set<String> set) {
                    if (set == null)
                        set = new HashSet<>();
                    Set<String> listened = mEvents.getListenedEvents();
                    Set<String> listened_dup = new HashSet<>(listened);
                    listened.removeAll(set);

                    IPredicate<String> isAborted = e -> e.startsWith(
                        EventType.ACTION_COMPLETED_ABORTED.name()
                    );

                    IPredicate<String> isSucceed = e -> e.startsWith(
                        EventType.ACTION_COMPLETED_SUCCEED.name()
                    );

                    IPredicate<String> isCompletedAction = e ->
                            isAborted.apply(e) || isSucceed.apply(e);

                    // Now work only with not listened or not ActionComplete event
                    for (String event: listened)
                        if (isCompletedAction.apply(event))
                            unsubscribe(event);
                    // unsubscribe action completed events of not monitored actions

                    SettingsWrapper settings = StagerApplication.getSettings();
                    boolean listenS = settings.isActionOnCompleteSucceedListenedByDefault(false);
                    boolean listenA = settings.isActionOnCompleteAbortedListenedByDefault(false);
                    if (!listenA && !listenS)
                        return;

                    set.removeAll(listened_dup);
                    // Now work only with new ActionComplete events
                    for (String event: set)
                        if (listenA && isAborted.apply(event) ||
                            listenS && isSucceed.apply(event))
                            subscribe(event);
                    // subscribe on new action completed events
                }

                @Override
                protected void readKeys(@NonNull @NotNull DataSnapshot snapshot, Set<String> set) {
                    for (DataSnapshot owner: snapshot.getChildren())
                        if (owner.getKey() != null)
                            for (DataSnapshot action: owner.getChildren())
                                if (action.getKey() != null) {
                                    set.add(getActionCompleteAbortedEventName(owner.getKey(),
                                                                              action.getKey()));
                                    set.add(getActionCompleteSucceedEventName(owner.getKey(),
                                                                              action.getKey()));
                                }
                }
            }
        );
    }

        //endregion Subscribe

        //region Send

    @Trackable(keys = {CBN.Notification.FRIENDSHIP_REQUEST})
    public void sendFriendshipRequestNoty(@NonNull String uid) {
        PushNotification.PushNotificationBuilder builder =
        mNotyGen.getSimpleEventNotification(EventType.FRIENDSHIP_REQUEST);
        requestTracker.postItem(CBN.Notification.FRIENDSHIP_REQUEST, builder);
        builder.build().send(getFriendshipRequestEventName(uid));
    }

    @Trackable(keys = {CBN.Notification.FRIENDSHIP_REQUEST_ACCEPTED})
    public void sendFriendshipRequestAcceptedNoty(@NonNull String uid) {
        PushNotification.PushNotificationBuilder builder =
        mNotyGen.getSimpleEventNotification(EventType.FRIENDSHIP_REQUEST_ACCEPTED);
        requestTracker.postItem(CBN.Notification.FRIENDSHIP_REQUEST, builder);
        builder.build().send(getFriendshipRequestAcceptedEventName(uid));
    }

    @Trackable(keys = {CBN.Notification.ACTION_COMPLETED_ABORTED})
    public void sendActionCompletedAbortedNoty(@NonNull String uid,
                                               @NonNull String actionKey) {
        PushNotification.PushNotificationBuilder builder =
        mNotyGen.getSimpleEventNotification(EventType.ACTION_COMPLETED_ABORTED)
                .setExtra(StagerPushNotificationHandler.ACTION, actionKey);
        requestTracker.postItem(CBN.Notification.ACTION_COMPLETED_ABORTED, builder);
        builder.build().send(getActionCompleteAbortedEventName(uid, actionKey));
    }

    @Trackable(keys = {CBN.Notification.ACTION_COMPLETED_SUCCEED})
    public void sendActionCompletedSucceedNoty(@NonNull String uid,
                                               @NonNull String actionKey) {
        PushNotification.PushNotificationBuilder builder =
        mNotyGen.getSimpleEventNotification(EventType.ACTION_COMPLETED_SUCCEED)
                .setExtra(StagerPushNotificationHandler.ACTION, actionKey);
        requestTracker.postItem(CBN.Notification.ACTION_COMPLETED_SUCCEED, builder);
        builder.build().send(getActionCompleteSucceedEventName(uid, actionKey));
    }


        //endregion Send

    //endregion Notifications

    //region Other

    public static <T> void trySetValue(@NotNull DatabaseReference ref, T value) {
        String key = ref.getKey();
        if (key == null || ref.getParent() == null)
            return;
        ref.getParent().runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NotNull MutableData currentData) {
                if (!currentData.hasChild(key))
                    return Transaction.success(currentData);
                currentData.child(key).setValue(value);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed,
                                   @Nullable DataSnapshot currentData) {}
        });
    }

    public static void toggle(@NotNull DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new OnValueGet(currentData -> {
            if (!currentData.exists())
                return;
            Boolean value = currentData.getValue(boolean.class);
            if (value == null)
                return;
            ref.setValue(!value);
        }));
    }

    public static void resetPositions(@NotNull DatabaseReference ref, List<String> keys) {
        BatchUpdate batch = BatchUpdate.init(ref);
        ref.addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists() || !snapshot.hasChildren())
                return;

            for (int i = 0; i < keys.size(); i++)
                if (snapshot.child(keys.get(i)).getValue() != null)
                    batch.set(snapshot.child(keys.get(i))
                                         .child(PATH.FB_POS)
                                         .getRef(), i+1);
            batch.apply();
        }));
    }

    @Trackable(keys = {CBN.INIT_POS})
    public void initPositions(@NotNull DatabaseReference ref) {
        BatchUpdate batch = BatchUpdate.init(ref);
        ref.addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists() || !snapshot.hasChildren())
                return;

            int max_pos = 0;
            Integer i;

            for (DataSnapshot item: snapshot.getChildren()) {
                i = item.child(PATH.FB_POS).getValue(Integer.class);
                if (i != null && i != Integer.MAX_VALUE && i > max_pos)
                    max_pos = i;
            }

            for (DataSnapshot item: snapshot.getChildren()) {
                i = item.child(PATH.FB_POS).getValue(Integer.class);
                if (i == null || i == Integer.MAX_VALUE)
                    batch.set(item.child(PATH.FB_POS).getRef(), ++max_pos);
            }
            requestTracker.postItem(CBN.INIT_POS, batch.apply());
        }));
    }

    public Query getSorted(@NotNull Query ref) {
        return ref.orderByChild(PATH.FB_POS);
    }

    @NotNull
    public static <T extends FBModel> List<String> getKeys(@NotNull List<T> list) {
        ArrayList<String> keys = new ArrayList<>();
        for (T item: list) keys.add(item.getKey());
        return keys;
    }

    @NotNull
    private BatchUpdate batchedFromRoot() {
        return BatchUpdate.init(mRef);
    }

    //endregion Other

    //region Annotations

    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Trackable {
        String[] keys();
    }

    //endregion Annotations
}
