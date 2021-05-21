package main.stager.Base;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import java.util.HashMap;
import java.util.Set;

import main.stager.StagerApplication;
import main.stager.utils.ChangeListeners.firebase.KeySetEventListener;
import main.stager.utils.ChangeListeners.firebase.ValueEventListener;
import main.stager.utils.DataProvider;
import main.stager.utils.Utilits;

public abstract class StagerViewModel extends AndroidViewModel {
    protected static DataProvider dataProvider = StagerApplication.getDataProvider();
    protected HashMap<Object, Query> backPath;

    public StagerViewModel(@NonNull Application application) {
        super(application);
    }

    @FunctionalInterface
    protected interface EventListenerSetter {
        void add();
    }

    protected <T> LiveData<T> getData(MutableLiveData<T> data, EventListenerSetter els) {
        if (data.getValue() == null)
            els.add();
        return data;
    }

    protected <T> LiveData<T> getSimpleFBData(MutableLiveData<T> data, Class<T> clazz) {
        return getData(data,
                () -> getBackPathTo(data).addValueEventListener(new ValueEventListener<T>(data, clazz)));
    }

    protected LiveData<Set<String>> getFBKeySet(MutableLiveData<Set<String>> data) {
        return getData(data, () -> getBackPathTo(data)
                .addValueEventListener(new KeySetEventListener(data)));
    }

    protected LiveData<String> getText(MutableLiveData<String> data) {
        return getText(data, "");
    }

    /** Возвращает ссылку, по которой были получены данные
     * @param data - LiveData
     * @return
     */
    public Query getBackPathTo(Object data) {
        return backPath.get(data);
    }

    public void buildBackPath() {
        backPath = new HashMap<>();
    }

    protected LiveData<String> getText(MutableLiveData<String> data, String defaultValue) {
        return getData(data, () -> getBackPathTo(data).addValueEventListener(
            new ValueEventListener<String>(data, String.class) {
                @Override
                protected String modify(String item, DataSnapshot snapshot) {
                    if (Utilits.isNullOrBlank(item))
                        return defaultValue;
                    return item.trim();
                }
            }
        ));
    }
}