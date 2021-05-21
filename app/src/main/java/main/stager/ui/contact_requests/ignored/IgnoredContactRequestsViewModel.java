package main.stager.ui.contact_requests.ignored;

import android.app.Application;
import androidx.annotation.NonNull;
import com.google.firebase.database.Query;

import main.stager.list.StagerListViewModel;
import main.stager.model.Contact;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.ChangeListeners.firebase.ValueListEventListener;

public class IgnoredContactRequestsViewModel extends StagerListViewModel<Contact> {

    public IgnoredContactRequestsViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getIgnoredContactRequests();
    }

    @Override
    protected Class<Contact> getItemType() {
        return Contact.class;
    }

    @Override
    protected ValueListEventListener<Contact> getListEventListener(OnError onError) {
        return getJoinedListEventListener(
                dataProvider.getAllUserInfo(), onError);
    }
}