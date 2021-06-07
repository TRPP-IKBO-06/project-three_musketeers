package main.stager.ui.contact_requests.outgoing;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import main.stager.list.StagerListViewModel;
import main.stager.model.Contact;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.ChangeListeners.firebase.ValueJoinedListEventListener;
import main.stager.utils.ChangeListeners.firebase.ValueListEventListener;

public class OutgoingContactRequestsViewModel extends StagerListViewModel<Contact> {

    public OutgoingContactRequestsViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getOutgoingContactRequests();
    }

    @Override
    protected Class<Contact> getItemType() {
        return Contact.class;
    }

    @Override
    protected ValueListEventListener<Contact> getListEventListener(OnError onError) {
        return new ValueJoinedListEventListener<Contact>(mValues, getItemType(),
                onError, dataProvider.getAllUserInfo()) {
            @Override
            protected DatabaseReference handleListItemKeySource(@NonNull DataSnapshot snapshot) {
                return snapshot.getChildren().iterator().next().getRef();
            }
        };
    }

    @Override
    public void deleteItem(Contact s) {
        dataProvider.removeOutgoingContactRequest(s.getKey());
    }
}