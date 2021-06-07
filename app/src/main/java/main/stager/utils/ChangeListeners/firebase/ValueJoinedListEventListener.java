package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import main.stager.utils.DataProvider;

public class ValueJoinedListEventListener<T> extends ValueListEventListener<T> {
    protected DatabaseReference source;
    protected Map<String, DatabaseReference> sources = new HashMap<>();
    protected Map<String, DatabaseReference> keySources = new HashMap<>();
    protected List<T> tmpList = new ArrayList<>();
    protected Set<String> awaitedKeys = new HashSet<>();
    protected Set<String> listenKeys;
    protected ValueEventListener sourceListener = new SourceValueListener();

    public ValueJoinedListEventListener(MutableLiveData<List<T>> liveList,
                                        Class<T> className, OnError onError,
                                        Query source) {
        super(liveList, className, onError);
        if (source != null)
            this.source = source.getRef();
    }

    public ValueJoinedListEventListener(MutableLiveData<List<T>> liveList,
                                        Class<T> className, Query source) {
        this(liveList, className, null, source);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        clearPreviousDataLists();

        if (!snapshot.exists()) {
            liveList.postValue(null);
            return;
        }

        handleListItems(snapshot);
        saveNewSourceListeners();
    }

    protected void clearPreviousDataLists() {
        tmpList.clear();
        awaitedKeys.clear();
        keySources.clear();
        removePreviousSourceListeners();
        sources.clear();
    }

    protected void handleListItems(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            String key = handleListItemKey(postSnapshot);
            if (!ignoredKeys.contains(key) ^ invertIgnoredKeys)
                handleListItem(postSnapshot, key);
        }
    }

    protected void handleListItem(@NonNull DataSnapshot snapshot, @NonNull String key) {
        awaitedKeys.add(key);
        keySources.put(key, handleListItemKeySource(snapshot));
        sources.put(key, handleListItemSource(snapshot));
    }

    protected DatabaseReference handleListItemKeySource(@NonNull DataSnapshot snapshot) {
        return snapshot.getRef();
    }

    protected DatabaseReference handleListItemSource(@NonNull DataSnapshot snapshot) {
        return source;
    }

    protected String handleListItemKey(@NonNull DataSnapshot snapshot) {
        return snapshot.getKey();
    }


    protected void removePreviousSourceListeners() {
        if (listenKeys == null) return;
        for (String key: listenKeys)
            removeSourceListener(key);
    }

    protected void saveNewSourceListeners() {
        listenKeys = new HashSet<>(awaitedKeys);
        for (String key: awaitedKeys)
            addSourceListener(key);
    }


    protected void onSourceDataChanged(@NotNull DataSnapshot snapshot) {
        if (!awaitedKeys.contains(snapshot.getKey()))
            onExistingSourceDataChanged(snapshot);
        else
            onSourceDataGotInitial(snapshot);
    }

    protected void onExistingSourceDataChanged(@NotNull DataSnapshot snapshot) {
        // Just cause update of list of keys (joined to source)

        String key = snapshot.getKey();
        if (key == null || !keySources.containsKey(key)) return;

        DatabaseReference ref = keySources.get(key);
        if (ref == null) return;

        DataProvider.toggle(ref);
    }

    protected void onSourceDataGotInitial(@NotNull DataSnapshot snapshot) {
        if (!snapshot.exists()) {
            removeAwaited(snapshot.getKey());
            return;
        }

        T item = snapshot.getValue(className);
        if (!isExcluded(item)) {
            item = modify(item, snapshot);
            if (!isModifiedExcluded(item))
                tmpList.add(item);
        }

        removeAwaited(snapshot.getKey());
    }

    protected void removeAwaited(String key) {
        awaitedKeys.remove(key);
        if (awaitedKeys.isEmpty())
            liveList.postValue(tmpList);
    }


    protected DatabaseReference getSourceOf(@NonNull String key) {
        return sources.get(key);
    }

    protected void removeSourceListener(@NonNull String key) {
        DatabaseReference ref = getSourceOf(key);
        if (ref == null) return;
        ref.child(key).removeEventListener(sourceListener);
    }

    protected void addSourceListener(@NonNull String key) {
        DatabaseReference ref = getSourceOf(key);
        if (ref == null) return;
        ref.child(key).addValueEventListener(sourceListener);
    }

    protected class SourceValueListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            onSourceDataChanged(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            ValueJoinedListEventListener.this.onCancelled(error);
        }
    }
}
