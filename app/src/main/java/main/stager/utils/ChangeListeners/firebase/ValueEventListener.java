package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import main.stager.model.FBModel;

public class ValueEventListener<T> extends AValueEventListener<T> {
    protected MutableLiveData<T> live;
    protected Class<T> className;

    public ValueEventListener(MutableLiveData<T> live, Class<T> className, OnError onError) {
        this.live = live;
        this.className = className;
        this.onError = onError;
    }

    public ValueEventListener(MutableLiveData<T> live, Class<T> className) {
        this(live, className, null);
    }

    protected T modify(T item, DataSnapshot snapshot) {
        if (item instanceof FBModel)
            ((FBModel)item).setKey(snapshot.getKey());
        return item;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists())
            live.postValue(modify(snapshot.getValue(className), snapshot));
    }
}
