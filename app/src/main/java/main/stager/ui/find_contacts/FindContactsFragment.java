package main.stager.ui.find_contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import main.stager.R;
import main.stager.list.feature.StagerSearchResultsListFragment;
import main.stager.model.Contact;
import main.stager.ui.my_contacts.ContactRecyclerViewAdapter;
import main.stager.utils.Utilits;

import static main.stager.utils.DataProvider.INVALID_ACTION_KEY;

public class FindContactsFragment extends
        StagerSearchResultsListFragment<FindContactsViewModel, ContactRecyclerViewAdapter, Contact> {
    static public final String ARG_ACTION_KEY = "Stager.find_contacts.param_action_key";
    private String mActionKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActionKey = Utilits.getDefaultOnNullOrBlank(
                getArguments().getString(ARG_ACTION_KEY),
                INVALID_ACTION_KEY
            );
        } else {
            mActionKey = INVALID_ACTION_KEY;
        }
    }

    @Override
    protected Class<FindContactsViewModel> getViewModelType() {
        return FindContactsViewModel.class;
    }

    @Override
    protected Class<ContactRecyclerViewAdapter> getAdapterType() {
        return ContactRecyclerViewAdapter.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_find_contacts;
    }

    @Override
    protected String getQueryHintText() {
        return getString(R.string.FindContactsFragment_message_queryHint);
    }

    @Override
    protected void onItemClick(Contact item, int pos, View view) {
        super.onItemClick(item, pos, view);
        final float selected = 0.8f;
        final float reset = 1f;
        if (view.getAlpha() == selected) {
            dataProvider.revokeSharedActionAccess(item.getKey(), mActionKey)
                        .addOnSuccessListener(t -> {
                Toast.makeText(getContext(),
                        getString(R.string.FindContactsFragment_message_revoke, item.getName()),
                        Toast.LENGTH_SHORT).show();
                view.setAlpha(reset);
            });
            return;
        }
        dataProvider.shareAction(item.getKey(), mActionKey).addOnSuccessListener(t -> {
            Toast.makeText(getContext(),
                    getString(R.string.FindContactsFragment_message_success, item.getName()),
                    Toast.LENGTH_SHORT).show();
            view.setAlpha(selected);
        });
    }

    @Override
    protected void setDependencies() {
        super.setDependencies();
        dependencies.add(dataProvider.getAction(mActionKey));
    }
}