package main.stager.utils.GainObservers;

public interface IGainedObservable {
    default IGainedObservable addOnItemGainListener(String key,
                                                    OnItemGained listener) {
        return this;
    }

    default void postItem(String key, Object Item) {}
}
