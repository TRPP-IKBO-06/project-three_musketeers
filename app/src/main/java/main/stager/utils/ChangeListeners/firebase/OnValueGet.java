package main.stager.utils.ChangeListeners.firebase;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class OnValueGet implements com.google.firebase.database.ValueEventListener {
    @lombok.NonNull private OnDataChangeListener onDataChangeListener;
    private OnCancelledListener onCancelledListener;

    @FunctionalInterface
    public interface OnDataChangeListener {
        void onDataChange(@NonNull DataSnapshot snapshot);
    }

    @FunctionalInterface
    public interface OnCancelledListener {
        void onCancelled(@NonNull DatabaseError snapshot);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        onDataChangeListener.onDataChange(snapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        if (onCancelledListener != null)
            onCancelledListener.onCancelled(error);
    }
}
