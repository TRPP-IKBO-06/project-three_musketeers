package main.stager;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import main.stager.Base.StagerViewModel;

public class NavBarViewModel extends StagerViewModel {
    private MutableLiveData<String> mName;

    public NavBarViewModel(@NonNull Application application) {
        super(application);
        mName = new MutableLiveData<>();
    }

    public LiveData<String> getName() {
        return getText(mName);
    }

    @Override
    public void buildBackPath() {
        super.buildBackPath();
        backPath.put(mName, dataProvider.getUserName());
    }
}