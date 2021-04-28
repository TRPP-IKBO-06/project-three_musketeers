package main.stager.utils.ChangeListeners.firebase.front;

import com.google.firebase.database.DatabaseReference;

import main.stager.utils.DataProvider;

// Update DB <- info
public abstract class FBFrontListener {
    protected DatabaseReference mRef;
    protected boolean mUpdateOnly;

    public FBFrontListener(DatabaseReference ref) { mRef = ref; }
    public FBFrontListener(DatabaseReference ref, boolean update) {
        this(ref);
        mUpdateOnly = update;
    }

    protected void send(Object value) {
        if (mUpdateOnly)
            DataProvider.trySetValue(mRef, value);
        else
            mRef.setValue(value);
    }
}