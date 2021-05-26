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
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.With;
import main.stager.StagerApplication;
import main.stager.model.FBModel;
import main.stager.model.Stage;
import main.stager.model.Status;
import main.stager.model.UserAction;
import main.stager.utils.BroadcasterHolders.IGainedObservable;
import main.stager.utils.ChangeListeners.firebase.OnValueGet;
import main.stager.utils.pushNotifications.EventNotificationBuilder;
import main.stager.utils.pushNotifications.EventType;

@AllArgsConstructor // Not recommended to use this constructor
public class DataProvider {

    //region INIT
    private FirebaseAuth mAuth;
    private FirebaseMessaging mMes;
    private EventNotificationBuilder mNotyGen;
    private DatabaseReference mRef;

    public DataProvider() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mMes = FirebaseMessaging.getInstance();
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

    public static final class CBN {
        public static final class SetStageStatus {
            public static final String UPDATE_STAGES = "update_stages";
            public static final String UPDATE_ACTION = "update_action";
        }
        public static final String RESET_ACTION_STATUS = "reset_action_status";

        public static final String INIT_POS = "init_pos";
        public static final String ADD_ACTION = "add_action";
        public static final String ADD_STAGE = "add_stage";
    }

    //endregion INIT

    //region EXTRA PARAMS

    @With private IGainedObservable requestTracker;

    //endregion EXTRA PARAMS

    //region User data

    /** Path safe, not null uid
     * @return uid or empty string
     */
    public @NotNull String getUID() {
        String uid = mAuth.getUid();
        return uid == null ? "" : uid;
    }

    public @NotNull String getEmail() {
        if (mAuth.getCurrentUser() == null) return "";
        String email = mAuth.getCurrentUser().getEmail();
        return email != null ? email : "";
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

    public Task<Void> acceptContactRequest(@NonNull String from) {
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
        return getMonitoredAction(uid, actionOwner).child(actionKey);
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

    public DatabaseReference getSubscribers() {
        return getShared().child(getUID());
    }

    public Query getSubscribersOfAction(@NonNull String actionKey) {
        return getSubscribers().orderByChild(actionKey).startAt(false).endAt(true);
    }

    public DatabaseReference getSharedActions(@NonNull String sharedTo) {
        return getSubscribers().child(sharedTo);
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
        return getAllActions().child(getUID());
    }

    public DatabaseReference getAction(@NotNull String key) {
        return getActions().child(key);
    }

    public DatabaseReference getActionStatus(@NotNull String key) {
        return getAction(key).child(PATH.ACTION_STATUS);
    }

    public DatabaseReference getActionName(@NotNull String key) {
        return getActions().child(key).child(PATH.ACTION_NAME);
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
        getStages(actionKey).addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists() || !snapshot.hasChildren())
                return;

            for (DataSnapshot item: snapshot.getChildren())
                batch.set(item.child(PATH.STAGE_STATUS).getRef(),
                          Status.WAITING);

            batch.set(getActionStatus(actionKey), Status.WAITING);
            requestTracker.postItem(CBN.RESET_ACTION_STATUS, batch.apply());
        }));
    }

    @Trackable(keys = {CBN.SetStageStatus.UPDATE_ACTION,
                       CBN.SetStageStatus.UPDATE_STAGES})
    public void setStageStatusSucceed(@NotNull String actionKey,
                                      @NotNull String stageKey) {
        DatabaseReference ref = getStages(actionKey);
        BatchUpdate batch = BatchUpdate.init(ref);
        ref.addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists() || !snapshot.hasChildren())
                return;

            Stage target = snapshot.child(stageKey).getValue(Stage.class);
            if (target == null)
                return;

            int pos = target.getPos();
            if (pos == Integer.MAX_VALUE)
                return;

            Stage stage;
            int unfinishedCount = 0;
            for (DataSnapshot item: snapshot.getChildren()) {
                stage = item.getValue(Stage.class);
                if (stage == null)
                    return;

                if (stage.getCurrentStatus() != Status.SUCCEED) {
                    if (stage.getPos() < pos) return;
                    unfinishedCount++;
                }
            }

            batch.set(snapshot.child(stageKey)
                    .child(PATH.STAGE_STATUS)
                    .getRef(), Status.SUCCEED);

            if (unfinishedCount == 0) return;

            Task<Void> task = batch.apply();
            requestTracker.postItem(CBN.SetStageStatus.UPDATE_STAGES, task);

            if (unfinishedCount != 1) return;

            task.addOnSuccessListener(t ->
                requestTracker.postItem(
                        CBN.SetStageStatus.UPDATE_ACTION,
                        getActionStatus(actionKey)
                        .setValue(Status.SUCCEED))
            );
        }));
    }

    @Trackable(keys = {CBN.SetStageStatus.UPDATE_ACTION,
                       CBN.SetStageStatus.UPDATE_STAGES})
    public void setStageStatusAborted(@NotNull String actionKey,
                                      @NotNull String stageKey) {
        DatabaseReference ref = getStages(actionKey);
        BatchUpdate batch = BatchUpdate.init(ref);
        ref.addListenerForSingleValueEvent(new OnValueGet(snapshot -> {
            if (!snapshot.exists())
                return;

            Stage target = snapshot.child(stageKey).getValue(Stage.class);
            if (target == null)
                return;

            int pos = target.getPos();
            if (pos == Integer.MAX_VALUE)
                return;

            List<String> lockList = new ArrayList<>();

            Stage stage;
            for (DataSnapshot item: snapshot.getChildren()) {
                stage = item.getValue(Stage.class);
                if (stage == null)
                    return;

                if (stage.getPos() < pos &&
                    stage.getCurrentStatus() != Status.SUCCEED)
                    return;

                if (stage.getPos() > pos) {
                    if (stage.getCurrentStatus() != Status.WAITING &&
                        stage.getCurrentStatus() != Status.LOCKED)
                        return;
                    lockList.add(item.getKey());
                }
            }

            batch.set(snapshot.child(stageKey)
                                 .child(PATH.STAGE_STATUS)
                                 .getRef(), Status.ABORTED);

            for (String lock_key: lockList)
                batch.set(snapshot.child(lock_key)
                                     .child(PATH.STAGE_STATUS)
                                     .getRef(), Status.LOCKED);

            Task<Void> task = batch.apply();
            requestTracker.postItem(CBN.SetStageStatus.UPDATE_STAGES, task);
            task.addOnSuccessListener(t ->
                requestTracker.postItem(CBN.SetStageStatus.UPDATE_ACTION,
                    getActionStatus(actionKey)
                    .setValue(Status.ABORTED))
            );
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
                .remove(getMonitoredActions(uid, getUID()))
                .apply();
    }

    //endregion Contacts

    //region Notifications

    private static final String NT_SEP = "-";

        //region EventNames

    public List<String> getInitialEventNames() {
        List<String> list = new ArrayList<>();
        list.add(getFriendshipRequestEventName(getUID()));
        return list;
    }

    public String getFriendshipRequestEventName(@NonNull String uid) {
        return EventType.FRIENDSHIP_REQUEST +NT_SEP+ uid;
    }

        //endregion EventNames

        //region Subscribe

    public void subscribeInitial() {
        for (String eventName: getInitialEventNames())
            subscribe(eventName);
    }

    public void unsubscribeInitial() {
        for (String eventName: getInitialEventNames())
            unsubscribe(eventName);
    }

    private void subscribe(@NonNull String eventName) {
        mMes.subscribeToTopic(eventName);
    }

    private void unsubscribe(@NonNull String eventName) {
        mMes.unsubscribeFromTopic(eventName);
    }

        //endregion Subscribe

        //region Send

    public void sendFriendshipRequestNoty(@NonNull String uid) {
        mNotyGen.getSimpleEventNotification(EventType.FRIENDSHIP_REQUEST)
                .build().send(getFriendshipRequestEventName(uid));
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
                if (currentData == null ||
                    !currentData.hasChild(key))
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
