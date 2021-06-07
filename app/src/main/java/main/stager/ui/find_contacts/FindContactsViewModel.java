package main.stager.ui.find_contacts;

import android.app.Application;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import java.util.Set;
import main.stager.list.feature.StagerSearchResultsListViewModel;
import main.stager.model.Contact;
import main.stager.utils.ChangeListeners.firebase.AValueEventListener;

public class FindContactsViewModel extends StagerSearchResultsListViewModel<Contact> {

    public FindContactsViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected Query getListPath() {
        return dataProvider.findUserByName(query);
    }

    @Override
    protected Class<Contact> getItemType() {
        return Contact.class;
    }

    @Override
    protected void setupListEventListener() {
        super.setupListEventListener();
        mListEventListener.setInvertIgnoredKeys(true);
        dataProvider.getContacts().addValueEventListener(new AValueEventListener<String>() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> ignore = mListEventListener
                        .getIgnoredKeys();
                ignore.clear();
                if (!snapshot.exists())
                    return;
                for (DataSnapshot postSnapshot : snapshot.getChildren())
                    ignore.add(postSnapshot.getKey());
            }
        });
    }
}