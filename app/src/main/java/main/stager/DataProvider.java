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
        return mRef.child("actions")
                .child(mAuth.getUid());
    }

    public DatabaseReference getStages(String key) {
        return mRef.child("stages")
                .child(mAuth.getUid())
                .child(key);
    }

    public static <T> ValueEventListener getListChangesListener(MutableLiveData<List<T>> liveList, Class<T> className) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;
                List<T> lst = new ArrayList<T>();
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                    lst.add(postSnapshot.getValue(className));
                liveList.postValue(lst);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing :)
            }
        };
    }
}
