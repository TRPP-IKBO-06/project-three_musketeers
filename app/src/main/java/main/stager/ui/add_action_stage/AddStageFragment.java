package main.stager.ui.add_action_stage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import main.stager.DataProvider;
import main.stager.R;
import main.stager.model.Stage;
import main.stager.model.Status;
import main.stager.model.TriggerType;

public class AddStageFragment extends Fragment {
    static public final String ARG_ACTION_KEY = "Stager.add_action_stage.param_action_key";
    private String mActionKey;
    private AddStageViewModel mViewModel;
    private EditText inputName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionKey = getArguments() != null
                   ? getArguments().getString(ARG_ACTION_KEY)
                   : "";
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_action_stage, container, false);
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setHomeAsUpIndicator(R.drawable.ic_close);
        inputName = view.findViewById(R.id.add_action_stage_input_name);
        return view;
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
            save_changes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save_changes() {
        String name = inputName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(),
                    getString(R.string.add_action_error_message,
                            getString(R.string.add_action_error_message_reason_noname)
                    ), Toast.LENGTH_LONG).show();
            return;
        }
        DataProvider.getInstance().addStage(mActionKey,
                new Stage(Status.WAITING, name, TriggerType.MANUAL));
        go_back();
    }

    private void go_back() {
        ((NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment))
                .getNavController().navigateUp();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddStageViewModel.class);
    }
}