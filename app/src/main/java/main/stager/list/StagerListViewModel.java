package main.stager.list;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import main.stager.Base.StagerViewModel;
import main.stager.model.FBModel;
import main.stager.utils.DataProvider;
import main.stager.utils.ChangeListeners.firebase.*;

public abstract class StagerListViewModel<T extends FBModel> extends StagerViewModel {

    protected MutableLiveData<List<T>> mValues;

    public StagerListViewModel(@NonNull Application application) {
        super(application);
        mValues = new MutableLiveData<>();
    }

    protected abstract Query getListPath();
    protected abstract Class<T> getItemType();

    public void deleteItem(T s) {}

    public void pushItemsPositions(List<T> items) {
        DataProvider.resetPositions((DatabaseReference)getListPath(), DataProvider.getKeys(items));
    }

    protected ValueEventListener getListEventListener(OnError onError) {
        return new ValueListEventListener<>(mValues, getItemType(), onError);
    }

    protected ValueEventListener getJoinedListEventListener(DatabaseReference source,
                                                            OnError onError) {
        return new ValueJoinedListEventListener<>(mValues, getItemType(),
                                                  onError, source);
    }

    public LiveData<List<T>> getItems(OnError onError, boolean sorted) {
        Query ref = (!sorted) ? getListPath()
                    : dataProvider.getSorted(getListPath());
        return getData(mValues, () ->
                ref.addValueEventListener(getListEventListener(onError)));
    }

    @Override
    public void buildBackPath() {
        super.buildBackPath();
        backPath.put(mValues, getListPath());
    }
}
