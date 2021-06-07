package main.stager.ui.monitored_actions.all;

import android.app.Application;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import main.stager.list.StagerListViewModel;
import main.stager.model.UserAction;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.ChangeListeners.firebase.ValueJoinedListEventListener;
import main.stager.utils.ChangeListeners.firebase.ValueListEventListener;

public class AllMonitoredActionsListViewModel extends StagerListViewModel<UserAction> {

    public AllMonitoredActionsListViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getMonitoredActionHolders();
    }

    @Override
    protected Class<UserAction> getItemType() {
        return UserAction.class;
    }

    @Override
    protected ValueListEventListener<UserAction> getListEventListener(OnError onError) {
        return new ValueJoinedListEventListener<UserAction>(mValues,
                getItemType(), onError, dataProvider.getAllActions()){
            private String currentOwner;
            private Map<String, String> owners = new HashMap<>();

            @Override
            protected void handleListItems(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        currentOwner = postSnapshot.getKey();
                        super.handleListItems(postSnapshot);
                    }
                }

            @Override
            protected void handleListItem(@NonNull @NotNull DataSnapshot snapshot, @NonNull @NotNull String key) {
                super.handleListItem(snapshot, key);
                owners.put(key, currentOwner);
            }

            @Override
                protected UserAction modify(UserAction item, DataSnapshot snapshot) {
                    item = super.modify(item, snapshot);
                    item.setOwner(owners.get(item.getKey()));
                    return item;
                }

                @Override
                protected DatabaseReference handleListItemSource(@NotNull DataSnapshot snapshot) {
                    return source.child(currentOwner);
                }
        };
    }

    @Override
    public void deleteItem(UserAction ua) {
        dataProvider.deleteAction(ua.getKey());
    }
}