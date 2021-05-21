package main.stager.ui.monitored_action;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.Query;
import lombok.Setter;
import main.stager.list.StagerListViewModel;
import main.stager.model.Stage;

public class MonitoredActionViewModel extends StagerListViewModel<Stage> {
    private MutableLiveData<String> actionName;
    @Setter private String actionKey = "";
    @Setter private String actionOwner = "";

    public MonitoredActionViewModel(@NonNull Application application) {
        super(application);
        actionName = new MutableLiveData<>();
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getMonitoredActionStages(actionOwner, actionKey);
    }

    @Override
    protected Class<Stage> getItemType() {
        return Stage.class;
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
