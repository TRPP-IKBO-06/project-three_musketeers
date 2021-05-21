package main.stager.list.feature;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.google.firebase.database.Query;
import java.util.List;
import main.stager.list.StagerListViewModel;
import main.stager.model.FBModel;
import main.stager.utils.ChangeListeners.firebase.OnError;
import main.stager.utils.ChangeListeners.firebase.ValueListEventListener;

public abstract class StagerSearchResultsListViewModel<T extends FBModel>
        extends StagerListViewModel<T> {

    protected String query;
    protected Query previousRequest;
    protected ValueListEventListener<T> mListEventListener;

    public StagerSearchResultsListViewModel(@NonNull Application application) {
        super(application);
        query = getInitialQuery();
    }

    protected String getInitialQuery() { return ""; }

    @Override
    public LiveData<List<T>> getItems(OnError onError, boolean unused) {
        if (mListEventListener == null) {
            mListEventListener = getListEventListener(onError);
            setupListEventListener();
        } else
            previousRequest.removeEventListener(mListEventListener);
        previousRequest = getListPath();
        previousRequest.addValueEventListener(mListEventListener);
        return mValues;
    }

    protected void setupListEventListener() {}

    public void setQuery(String query) {
        this.query = query;
        getItems(null);
    }
}
