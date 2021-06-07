package main.stager.ui.contact_info;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import lombok.Setter;
import main.stager.Base.StagerViewModel;
import main.stager.model.Contact;

public class ContactInfoViewModel extends StagerViewModel {

    private MutableLiveData<Contact> mContact;
    @Setter private String key;

    public ContactInfoViewModel(@NonNull Application application) {
        super(application);
        mContact = new MutableLiveData<>();
    }

    public LiveData<Contact> getContact() {
        return getSimpleFBData(mContact, Contact.class);
    }

    @Override
    public void buildBackPath() {
        super.buildBackPath();
        backPath.put(mContact, dataProvider.getUserInfo(key));
    }
}