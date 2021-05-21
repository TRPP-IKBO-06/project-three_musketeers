package main.stager.ui.find_new_contacts;

import android.view.View;
import android.widget.Toast;
import main.stager.R;
import main.stager.list.feature.StagerSearchResultsListFragment;
import main.stager.model.Contact;

public class FindNewContactsFragment extends
        StagerSearchResultsListFragment<FindNewContactsViewModel, FoundContactRecyclerViewAdapter, Contact> {

    @Override
    protected Class<FindNewContactsViewModel> getViewModelType() {
        return FindNewContactsViewModel.class;
    }

    @Override
    protected Class<FoundContactRecyclerViewAdapter> getAdapterType() {
        return FoundContactRecyclerViewAdapter.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_find_contacts;
    }

    @Override
    protected String getQueryHintText() {
        return getString(R.string.FindNewContactsFragment_message_queryHint);
    }

    @Override
    protected void onItemClick(Contact item, int pos, View view) {
        super.onItemClick(item, pos, view);
        if (item.isOutgoing()) {
            dataProvider.removeOutgoingContactRequest(item.getKey()).addOnCompleteListener(e -> {
                Toast.makeText(getContext(),
                        getString(R.string.FindNewContactsFragment_message_revoke, item.getName()),
                        Toast.LENGTH_SHORT).show();
            });
            return;
        }
        dataProvider.makeContactRequest(item.getKey()).addOnSuccessListener(e -> {
            Toast.makeText(getContext(),
                    getString(R.string.FindNewContactsFragment_message_success, item.getName()),
                    Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), R.string.FindNewContactsFragment_message_denied,
                    Toast.LENGTH_SHORT).show();
            FoundContactRecyclerViewAdapter.disable(view);
        });
    }
}