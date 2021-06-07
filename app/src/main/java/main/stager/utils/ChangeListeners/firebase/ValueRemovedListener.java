package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public abstract class ValueRemovedListener implements com.google.firebase.database.ValueEventListener {

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) onValueRemoved();
    }

    protected void onValueRemoved() {}

    @Override
    public void onCancelled(@NonNull DatabaseError error) {}
}
