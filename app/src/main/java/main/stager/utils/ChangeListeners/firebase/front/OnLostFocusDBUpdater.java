package main.stager.utils.ChangeListeners.firebase.front;

import android.view.View;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;

public class OnLostFocusDBUpdater extends FBFrontListener
        implements View.OnFocusChangeListener {
    public OnLostFocusDBUpdater(DatabaseReference ref) { super(ref); }
    public OnLostFocusDBUpdater(DatabaseReference ref, boolean updateOnly) {
        super(ref, updateOnly);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus)
                send(((TextView)v).getText().toString());
    }
}
