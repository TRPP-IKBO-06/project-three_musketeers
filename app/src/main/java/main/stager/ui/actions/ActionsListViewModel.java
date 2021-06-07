package main.stager.ui.actions;

import android.app.Application;
import androidx.annotation.NonNull;
import com.google.firebase.database.Query;
import main.stager.list.StagerListViewModel;
import main.stager.model.UserAction;

public class ActionsListViewModel extends StagerListViewModel<UserAction> {

    public ActionsListViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected Query getListPath() {
        return dataProvider.getActions();
    }

    @Override
    protected Class<UserAction> getItemType() {
        return UserAction.class;
    }

    @Override
    public void deleteItem(UserAction ua) {
        dataProvider.deleteAction(ua.getKey());
    }
}