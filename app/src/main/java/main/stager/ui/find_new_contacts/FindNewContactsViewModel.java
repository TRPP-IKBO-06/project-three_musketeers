package main.stager.ui.find_new_contacts;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import main.stager.list.feature.StagerSearchResultsListViewModel;
import main.stager.model.Contact;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.ChangeListeners.firebase.ValueListEventListener;
import main.stager.utils.Utilits;

public class FindNewContactsViewModel extends StagerSearchResultsListViewModel<Contact> {

    protected MutableLiveData<Set<String>> mContacts;
    protected MutableLiveData<Set<String>> mOutgoingRequests;
    protected MutableLiveData<Set<String>> mIncomingRequests;
    protected MutableLiveData<Set<String>> mIgnoredRequests;

    @Override
    protected String getInitialQuery() {
        return " ";
    }

    public FindNewContactsViewModel(@NonNull Application application) {
        super(application);
        mContacts = new MutableLiveData<>();
        mOutgoingRequests = new MutableLiveData<>();
        mIncomingRequests = new MutableLiveData<>();
        mIgnoredRequests = new MutableLiveData<>();
    }

    @Override
    protected Query getListPath() {
        return dataProvider.findUserByEmail(query);
    }

    @Override
    protected Class<Contact> getItemType() {
        return Contact.class;
    }

    @Override
    protected ValueListEventListener<Contact> getListEventListener(OnError onError) {
        return new FoundContactsListEventListener(mValues, getItemType(), onError);
    }

    @Override
    protected void setupListEventListener() {
        super.setupListEventListener();
        Set<String> tmp = new HashSet<>();
        tmp.add(dataProvider.getUID());
        mListEventListener.setIgnoredKeys(tmp);

        mValues.addSource(getFBKeySet(mContacts), (keys) -> {
            if (keys == null)
                keys = new HashSet<>();
            keys.add(dataProvider.getUID());
            mListEventListener.setIgnoredKeys(keys);
            Set<String> finalKeys = keys;
            mValues.postValue(Utilits.filter(mValues.getValue(),
                    x -> !finalKeys.contains(x.getKey())));
        });

        mValues.addSource(getFBKeySet(mOutgoingRequests), keys -> {
            if (keys == null)
                keys = new HashSet<>();
            ((FoundContactsListEventListener)mListEventListener).setStateOutgoingKeys(keys);
            Set<String> finalKeys = keys;
            mValues.postValue(Utilits.map(mValues.getValue(), x -> {
                x.setOutgoing(finalKeys.contains(x.getKey()));
                return x;
            }));
        });

        mValues.addSource(getFBKeySet(mIgnoredRequests), keys -> {
            if (keys == null)
                keys = new HashSet<>();
            ((FoundContactsListEventListener)mListEventListener).setStateIgnoredKeys(keys);
            Set<String> finalKeys = keys;
            mValues.postValue(Utilits.map(mValues.getValue(), x -> {
                x.setIgnored(finalKeys.contains(x.getKey()));
                return x;
            }));
        });

        mValues.addSource(getFBKeySet(mIncomingRequests), keys -> {
            if (keys == null)
                keys = new HashSet<>();
            ((FoundContactsListEventListener)mListEventListener).setStateIncomingKeys(keys);
            Set<String> finalKeys = keys;
            mValues.postValue(Utilits.map(mValues.getValue(), x -> {
                x.setIncoming(finalKeys.contains(x.getKey()));
                return x;
            }));
        });
    }

    @Override
    public void buildBackPath() {
        super.buildBackPath();
        backPath.put(mContacts, dataProvider.getContacts());
        backPath.put(mOutgoingRequests, dataProvider.getOutgoingContactRequests());
        backPath.put(mIncomingRequests, dataProvider.getContactRequests());
        backPath.put(mIgnoredRequests, dataProvider.getIgnoringContacts());
    }

    static class FoundContactsListEventListener extends ValueListEventListener<Contact> {
        @Getter private Set<String> stateOutgoingKeys = new HashSet<>();
        @Getter private Set<String> stateIncomingKeys = new HashSet<>();
        @Getter private Set<String> stateIgnoredKeys = new HashSet<>();

        public void setStateOutgoingKeys(Set<String> stateOutgoingKeys) {
            if (stateOutgoingKeys == null)
                this.stateOutgoingKeys.clear();
            else
                this.stateOutgoingKeys = stateOutgoingKeys;
        }

        public void setStateIncomingKeys(Set<String> stateIncomingKeys) {
            if (stateIgnoredKeys == null)
                this.stateIncomingKeys.clear();
            else
                this.stateIncomingKeys = stateIncomingKeys;
        }

        public void setStateIgnoredKeys(Set<String> stateIgnoredKeys) {
            if (stateIgnoredKeys == null)
                this.stateIgnoredKeys.clear();
            else
                this.stateIgnoredKeys = stateIgnoredKeys;
        }

        public FoundContactsListEventListener(MutableLiveData<List<Contact>> liveList, Class<Contact> className, OnError onError) {
            super(liveList, className, onError);
        }

        public FoundContactsListEventListener(MutableLiveData<List<Contact>> liveList, Class<Contact> className) {
            super(liveList, className);
        }

        @Override
        protected Contact modify(Contact item, DataSnapshot snapshot) {
            item = super.modify(item, snapshot);

            if (stateOutgoingKeys.contains(item.getKey()))
                item.setOutgoing(true);

            if (stateIncomingKeys.contains(item.getKey()))
                item.setIncoming(true);

            if (stateIgnoredKeys.contains(item.getKey()))
                item.setIgnored(true);

            return item;
        }
    }
}