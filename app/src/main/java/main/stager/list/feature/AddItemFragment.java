package main.stager.list.feature;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import main.stager.R;
import main.stager.Base.StagerFragment;

public abstract class AddItemFragment extends StagerFragment {
    protected Button btnSaveChanges;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setHomeAsUpIndicator(R.drawable.ic_close);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
    }

    @Override
    protected void setEventListeners() {
        super.setEventListeners();
        if (btnSaveChanges != null)
            btnSaveChanges.setOnFocusChangeListener((v, focused) -> {
                if (focused) saveChanges();
            });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.item_edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_changes) {
            saveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void saveChanges();
}