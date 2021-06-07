package main.stager.Base;

import android.widget.EditText;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.database.Query;
import main.stager.utils.ChangeListeners.firebase.front.OnLostFocusDBUpdater;

public abstract class StagerVMFragment<TVM extends StagerViewModel> extends StagerFragment {
    protected TVM viewModel;

    // Требует переопределения
    protected abstract Class<TVM> getViewModelType();

    protected void setObservers() {}

    @Override
    protected void setEventListeners() {
        setObservers();
        super.setEventListeners();
    }

    protected void setViewModelData() {}

    @Override
    protected void prepareFragmentComponents() {
        viewModel = new ViewModelProvider(this).get(getViewModelType());
        setViewModelData();
        viewModel.buildBackPath();
    }

    @FunctionalInterface
    public interface BindDataCallback<T> {
        void run(T data);
    }

    @FunctionalInterface
    public interface BindDataTwoWayListenerSetter {
        void setListener(Query ref);
    }

    protected <T> void bindData(LiveData<T> data,
                                BindDataCallback<T> callback) {
        data.observe(getViewLifecycleOwner(), callback::run);
    }

    protected <T> void bindFBDataTwoWay(LiveData<T> data,
                                        BindDataCallback<T> callback,
                                        BindDataTwoWayListenerSetter listenerSetter) {
        bindData(data, callback);
        listenerSetter.setListener(viewModel.getBackPathTo(data));
    }

    protected void bindDataTwoWay(LiveData<String> data,
                                  EditText edit,
                                  BindDataTwoWayListenerSetter listenerSetter) {
        bindFBDataTwoWay(data, edit::setText, listenerSetter);
    }

    protected void bindDataTwoWay(LiveData<String> data,
                                  EditText edit,
                                  boolean updateOnly) {
        bindDataTwoWay(data, edit, r -> edit.setOnFocusChangeListener(
            new OnLostFocusDBUpdater(r.getRef(), updateOnly)
        ));
    }

    protected void bindDataTwoWay(LiveData<String> data,
                                  EditText edit) {
        bindDataTwoWay(data, edit, true);
    }
}