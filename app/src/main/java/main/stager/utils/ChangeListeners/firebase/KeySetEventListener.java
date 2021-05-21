package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import java.util.HashSet;
import java.util.Set;

public class KeySetEventListener extends AValueEventListener<String> {
    protected MutableLiveData<Set<String>> liveSet;

    public KeySetEventListener(MutableLiveData<Set<String>> liveSet, OnError onError) {
        this.liveSet = liveSet;
        this.onError = onError;
    }

    public KeySetEventListener(MutableLiveData<Set<String>> liveSet) {
        this(liveSet, null);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) {
            liveSet.postValue(null);
            return;
        }
        Set<String> lst = new HashSet<>();
        for (DataSnapshot postSnapshot: snapshot.getChildren())
            lst.add(postSnapshot.getKey());
        liveSet.postValue(lst);
    }
}
