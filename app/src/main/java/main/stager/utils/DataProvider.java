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
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import main.stager.model.FBModel;
import main.stager.model.Stage;
import main.stager.model.Status;
import main.stager.model.UserAction;

public class DataProvider {


    private static DataProvider instance;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    public static synchronized DataProvider getInstance() {
        if (instance == null)
            instance = new DataProvider();
        return instance;
    }

    private DataProvider() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db.setPersistenceEnabled(true);
        mRef = db.getReference(PATH.MAIN_DB);
        keepSynced();
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
    }

    // User data

    /** Path safe, not null uid
     * @return uid or empty string
     */
    public @NotNull String getUID() {
        String uid = mAuth.getUid();
        return uid == null ? "" : uid;
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

    public Query findUserByEmail(String email) {
        return getAllUserInfo().orderByChild(PATH.USER_EMAIL)
                .startAt(email).endAt(email+"\uf8ff").limitToFirst(5);
    }

    // Contact requests

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

    public DatabaseReference getIgnoredContactRequest(@NonNull String from) {
        return getIgnoredContactRequests().child(from);
    }

    public Task<Void> makeContactRequest(@NonNull String receiver) {
        return getAllContactRequests().child(receiver).child(getUID()).setValue(true);
    }

    public Task<Void> removeOutgoingContactRequest(@NonNull String from) {
        return getOutgoingContactRequest(from).removeValue();
    }

    public Task<Void> removeIgnoredContactRequest(@NonNull String from) {
        return getIgnoredContactRequest(from).removeValue();
    }

    public void acceptContactRequest(@NonNull String from) {
        getContacts().child(from).setValue(true);
        getContacts(from).child(getUID()).setValue(true);
        getIncomingContactRequest(from).removeValue();
        getIgnoredContactRequest(from).removeValue();
    }

    public void ignoreContactRequest(@NonNull String from) {
        getIncomingContactRequest(from).removeValue();
        getIgnoredContactRequest(from).setValue(true);
    }

    // Actions

    public DatabaseReference getActions() {
        return mRef.child(PATH.ACTIONS).child(getUID());
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

    public String addAction(UserAction ua) {
        String key = getActions().push().getKey();
        getAction(key).setValue(ua);
        initPositions(getActions());
        return key;
    }

    public void deleteAction(@NotNull String key) {
        getStages(key).removeValue();
        getAction(key).removeValue();
    }

    // Stages of action

    public String addStage(@NotNull String actionKey, Stage stage) {
        String key = getStages(actionKey).push().getKey();
        getStage(actionKey, key).setValue(stage);
        initPositions(getStages(actionKey));
        return key;
    }

    public DatabaseReference getAllStages() {
        return mRef.child(PATH.STAGES).child(getUID());
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

    public void setStageStatusSucceed(@NotNull String actionKey, @NotNull String stageKey) {
        getStages(actionKey).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (!currentData.hasChildren())
                    return Transaction.success(currentData);

                Stage target = currentData.child(stageKey).getValue(Stage.class);
                if (target == null)
                    return Transaction.abort();

                int pos = target.getPos();
                if (pos == Integer.MAX_VALUE)
                    return Transaction.abort();

                Stage stage;
                for (MutableData item: currentData.getChildren()) {
                    stage = item.getValue(Stage.class);
                    if (stage == null)
                        return Transaction.abort();

                    if (stage.getPos() < pos &&
                        stage.getCurrentStatus() != Status.SUCCEED)
                        return Transaction.abort();
                }

                currentData.child(stageKey).child(PATH.STAGE_STATUS).setValue(Status.SUCCEED);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed, @Nullable DataSnapshot currentData) {
                if (!committed || currentData == null || !currentData.exists())
                    return;

                for (DataSnapshot item : currentData.getChildren())
                    if (item.child(PATH.STAGE_STATUS).getValue(Status.class) != Status.SUCCEED)
                        return;

                getActionStatus(actionKey).setValue(Status.SUCCEED);
            }
        });
    }

    public void setStageStatusAborted(@NotNull String actionKey, @NotNull String stageKey) {
        getStages(actionKey).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (!currentData.hasChildren())
                    return Transaction.success(currentData);

                Stage target = currentData.child(stageKey).getValue(Stage.class);
                if (target == null)
                    return Transaction.abort();

                int pos = target.getPos();
                if (pos == Integer.MAX_VALUE)
                    return Transaction.abort();

                List<String> lockList = new ArrayList<>();

                Stage stage;
                for (MutableData item: currentData.getChildren()) {
                    stage = item.getValue(Stage.class);
                    if (stage == null)
                        return Transaction.abort();

                    if (stage.getPos() < pos &&
                        stage.getCurrentStatus() != Status.SUCCEED)
                        return Transaction.abort();

                    if (stage.getPos() > pos) {
                        if (stage.getCurrentStatus() != Status.WAITING &&
                            stage.getCurrentStatus() != Status.LOCKED)
                            return Transaction.abort();
                        lockList.add(item.getKey());
                    }
                }

                currentData.child(stageKey).child(PATH.STAGE_STATUS).setValue(Status.ABORTED);

                for (String lock_key: lockList)
                    currentData.child(lock_key).child(PATH.STAGE_STATUS).setValue(Status.LOCKED);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed, @Nullable DataSnapshot currentData) {
                if (!committed || currentData == null || !currentData.exists())
                    return;

                getActionStatus(actionKey).setValue(Status.ABORTED);
            }
        });
    }

    public void deleteStage(@NotNull String actionKey, @NotNull String stageKey) {
        getStage(actionKey, stageKey).removeValue();
    }

    // Contacts

    public DatabaseReference getContacts() {
        return getContacts(getUID());
    }

    public DatabaseReference getContact(@NonNull String of) {
        return getContacts(getUID()).child(of);
    }

    public DatabaseReference getContacts(@NonNull String uid) {
        return mRef.child(PATH.CONTACTS).child(uid);
    }

    // Other

    private String getPath(Query ref) {
        String path = ref.getRef().toString();
        String repo = ref.getRef().getRoot().toString();
        return path.substring(repo.length());
    }

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
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NotNull MutableData currentData) {
                Boolean value = currentData.getValue(boolean.class);
                if (value == null)
                    return Transaction.success(currentData);
                currentData.setValue(!value);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed,
                                   @Nullable DataSnapshot currentData) {}
        });
    }

    public static void resetPositions(@NotNull DatabaseReference ref, List<String> keys) {
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() != null)
                    for (int i = 0; i < keys.size(); i++)
                        if (currentData.child(keys.get(i)).getValue() != null)
                            currentData.child(keys.get(i)).child(PATH.FB_POS).setValue(i+1);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed,
                                   @Nullable DataSnapshot currentData) {}
        });
    }

    public static void initPositions(@NotNull DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null || !currentData.hasChildren())
                    return Transaction.success(currentData);

                int max_pos = 0;
                Integer i;

                for (MutableData item: currentData.getChildren()) {
                    i = item.child(PATH.FB_POS).getValue(Integer.class);
                    if (i != null && i != Integer.MAX_VALUE && i > max_pos)
                        max_pos = i;
                }

                for (MutableData item: currentData.getChildren()) {
                    i = item.child(PATH.FB_POS).getValue(Integer.class);
                    if (i == null || i == Integer.MAX_VALUE)
                        item.child(PATH.FB_POS).setValue(++max_pos);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed,
                                   @Nullable DataSnapshot currentData) {}
        });
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
}
