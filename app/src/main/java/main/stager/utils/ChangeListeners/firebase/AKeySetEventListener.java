package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import java.util.HashSet;
import java.util.Set;

public abstract class AKeySetEventListener extends AValueEventListener<String> {

    public AKeySetEventListener(OnError onError) {
        this.onError = onError;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) {
            onDataChangeFinished(null);
            return;
        }
        Set<String> set = new HashSet<>();
        readKeys(snapshot, set);
        onDataChangeFinished(set);
    }

    public abstract void onDataChangeFinished(Set<String> set);

    protected void readKeys(@NonNull DataSnapshot snapshot, Set<String> set) {
        for (DataSnapshot postSnapshot: snapshot.getChildren())
            set.add(postSnapshot.getKey());
    }
}
