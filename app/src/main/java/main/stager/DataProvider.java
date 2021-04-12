package main.stager;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import main.stager.model.Stage;
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

    public DataProvider() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRef = db.getReference("stager-main-db");
    }

    public boolean isAuthorized() {
        return mAuth.getCurrentUser() != null;
    }

    public DatabaseReference getActions() {
        return mRef.child("actions").child(mAuth.getUid());
    }

    public DatabaseReference getAction(String key) {
        return getActions().child(key);
    }

    public String addAction(UserAction ua) {
        String key = getActions().push().getKey();
        getAction(key).setValue(ua);
        return key;
    }

    public String addStage(String actionName, Stage stage) {
        String key = getStages(actionName).push().getKey();
        getStage(actionName, key).setValue(stage);
        return key;
    }

    public DatabaseReference getStages(String key) {
        return mRef.child("stages")
                .child(mAuth.getUid())
                .child(key);
    }

    public DatabaseReference getStage(String actionName, String key) {
        return getStages(actionName).child(key);
    }

    public DatabaseReference getUserInfo() {
        return mRef.child("user_info").child(mAuth.getUid());
    }

    public DatabaseReference getUserName() {
        return getUserInfo().child("name");
    }

    public DatabaseReference getUserDescription() {
        return getUserInfo().child("description");
    }

    public interface IListItemModifier {
        public Object modify(Object item, DataSnapshot postSnapshot);
    }

    public interface IModifier {
        public Object modify(Object item, DataSnapshot snapshot);
    }

    public static <T> ValueEventListener getListChangesListener(MutableLiveData<List<T>> liveList,
                                                                Class<T> className) {
        return getListChangesListener(liveList, className, null);
    }

    public static <T> ValueEventListener getListChangesListener(MutableLiveData<List<T>> liveList,
                                                                Class<T> className,
                                                                IListItemModifier modifier
                                                                ) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;
                List<T> lst = new ArrayList<>();
                T item;
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    item = postSnapshot.getValue(className);
                    if (modifier != null)
                        item = (T) modifier.modify(item, postSnapshot);
                    lst.add(item);
                }
                liveList.postValue(lst);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing :)
            }
        };
    }

    public static <T> ValueEventListener getValueChangesListener(MutableLiveData<T> live,
                                                                Class<T> className,
                                                                IModifier modifier) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;
                T item = snapshot.getValue(className);
                if (modifier != null)
                    item = (T) modifier.modify(item, snapshot);
                live.postValue(item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing :)
            }
        };
    }

    public static <T> ValueEventListener getValueChangesListener(MutableLiveData<T> live,
                                                                 Class<T> className) {
        return getValueChangesListener(live, className, null);
    }

}
