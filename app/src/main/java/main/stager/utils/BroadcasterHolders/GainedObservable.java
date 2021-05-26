package main.stager.utils.BroadcasterHolders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GainedObservable implements IGainedObservable {
    private Map<String, Set<OnItemGained>> onItemsGainedListeners;

    public GainedObservable() {
        this(new HashMap<>());
    }

    public GainedObservable(Map<String, Set<OnItemGained>> onItemsGainedListeners) {
        this.onItemsGainedListeners = onItemsGainedListeners;
    }

    public GainedObservable addOnItemGainListener(String key,
                                                  OnItemGained listener) {
        if (!onItemsGainedListeners.containsKey(key))
            onItemsGainedListeners.put(key, new HashSet<>());
        Set<OnItemGained> onItemGainedListeners =
                          onItemsGainedListeners.get(key);
        if (onItemGainedListeners != null)
            onItemGainedListeners.add(listener);
        return this;
    }

    public void postItem(String key, Object item) {
        Set<OnItemGained> onItemGainedListeners =
                          onItemsGainedListeners.get(key);
        if (onItemGainedListeners == null) return;
        for (OnItemGained onItemGained : onItemGainedListeners)
            if (onItemGained != null)
                onItemGained.apply(item);
    }
}
