package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import main.stager.model.FBModel;

public abstract class AValueEventListener<T> implements com.google.firebase.database.ValueEventListener {
    protected OnError onError;

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        if (onError != null)
            onError.react(error.getMessage());
    }

    protected T modify(T item, DataSnapshot snapshot) {
        if (item instanceof FBModel)
            ((FBModel)item).setKey(snapshot.getKey());
        return item;
    }
}
