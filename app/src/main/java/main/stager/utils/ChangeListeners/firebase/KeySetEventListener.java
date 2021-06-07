package main.stager.utils.ChangeListeners.firebase;

import androidx.lifecycle.MutableLiveData;
import java.util.Set;

public class KeySetEventListener extends AKeySetEventListener {
    protected MutableLiveData<Set<String>> liveSet;

    public KeySetEventListener(MutableLiveData<Set<String>> liveSet, OnError onError) {
        super(onError);
        this.liveSet = liveSet;
    }

    public KeySetEventListener(MutableLiveData<Set<String>> liveSet) {
        this(liveSet, null);
    }

    public void onDataChangeFinished(Set<String> set) {
        liveSet.postValue(set);
    }
}
