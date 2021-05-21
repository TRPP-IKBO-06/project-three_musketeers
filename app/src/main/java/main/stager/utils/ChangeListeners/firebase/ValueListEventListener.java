package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class ValueListEventListener<T> extends AValueEventListener<T> {
    protected MutableLiveData<List<T>> liveList;
    @Getter protected Set<String> ignoredKeys = new HashSet<>();
    @Getter @Setter protected boolean invertIgnoredKeys = false;
    protected Class<T> className;

    public void setIgnoredKeys(Set<String> ignoredKeys) {
        if (ignoredKeys == null)
            this.ignoredKeys.clear();
        else
            this.ignoredKeys = ignoredKeys;
    }

    public ValueListEventListener(MutableLiveData<List<T>> liveList, Class<T> className, OnError onError) {
        this.liveList = liveList;
        this.className = className;
        this.onError = onError;
    }

    public ValueListEventListener(MutableLiveData<List<T>> liveList, Class<T> className) {
        this(liveList, className, null);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) {
            liveList.postValue(null);
            return;
        }
        List<T> lst = new ArrayList<>();
        T item;
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            item = postSnapshot.getValue(className);
            if (isExcluded(item)
                    || ignoredKeys.contains(postSnapshot.getKey())
                        ^ invertIgnoredKeys)
                continue;

            item = modify(item, postSnapshot);
            if (isModifiedExcluded(item))
                continue;

            lst.add(item);
        }
        liveList.postValue(lst);
    }

    protected boolean isExcluded(T item) {
        return false;
    }

    protected boolean isModifiedExcluded(T item) {
        return false;
    }
}
