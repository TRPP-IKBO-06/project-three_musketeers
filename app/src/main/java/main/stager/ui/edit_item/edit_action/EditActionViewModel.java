package main.stager.ui.edit_item.edit_action;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.Query;
import java.util.List;
import lombok.Setter;
import main.stager.list.StagerListViewModel;
import main.stager.model.Stage;
import static main.stager.utils.DataProvider.INVALID_ACTION_KEY;

public class EditActionViewModel extends StagerListViewModel<Stage> {
    private MutableLiveData<String> actionName;
    @Setter private String actionKey = INVALID_ACTION_KEY;

    public EditActionViewModel(@NonNull Application application) {
        super(application);
        actionName = new MutableLiveData<>();
    }

    public void deleteItem(Stage s) {
        dataProvider.deleteStage(actionKey, s.getKey());
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getStages(actionKey);
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

    @Override
    public void pushItemsPositions(List<Stage> items) {
        super.pushItemsPositions(items);
        dataProvider.resetActionStatus(actionKey);
    }
}
