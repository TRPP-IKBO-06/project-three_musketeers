package main.stager.utils.BroadcasterHolders;

public interface IGainedObservable {
    default IGainedObservable addOnItemGainListener(String key,
                                                    OnItemGained listener) {
        return this;
    }

    default void postItem(String key, Object Item) {}
}
