package main.stager.ui.edit_item.edit_subscribers;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import static main.stager.utils.DataProvider.INVALID_ACTION_KEY;
import lombok.Setter;
import main.stager.list.StagerListViewModel;
import main.stager.model.Contact;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.ChangeListeners.firebase.ValueJoinedListEventListener;
import main.stager.utils.ChangeListeners.firebase.ValueListEventListener;

public class EditSubscribersViewModel extends StagerListViewModel<Contact> {
    private MutableLiveData<String> actionName;
    @Setter private String actionKey = INVALID_ACTION_KEY;

    public EditSubscribersViewModel(@NonNull Application application) {
        super(application);
        actionName = new MutableLiveData<>();
    }

    public void deleteItem(Contact s) {
        dataProvider.revokeSharedActionAccess(s.getKey(), actionKey);
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getSubscribersOfAction(actionKey);
    }

    @Override
    protected Class<Contact> getItemType() {
        return Contact.class;
    }

    @Override
    protected ValueListEventListener<Contact> getListEventListener(OnError onError) {
        return new ValueJoinedListEventListener<Contact>(mValues, getItemType(),
                onError, dataProvider.getAllUserInfo()){
            @Override
            protected DatabaseReference handleListItemKeySource(@NonNull DataSnapshot snapshot) {
                return snapshot.getChildren().iterator().next().getRef();
            }
        };
    }

    public LiveData<String> getActionName() {
        return getText(actionName);
    }

    @Override
    public void buildBackPath() {
        super.buildBackPath();
        backPath.put(actionName,
                dataProvider.getActionName(actionKey));
    }
}
