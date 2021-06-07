package main.stager.ui.contact_requests.incoming;

import android.os.Bundle;
import android.view.View;

import main.stager.R;
import main.stager.list.StagerList;
import main.stager.model.Contact;
import main.stager.ui.contact_info.ContactInfoFragment;
import main.stager.model.ContactType;
import main.stager.ui.my_contacts.ContactRecyclerViewAdapter;

public class IncomingContactRequestsFragment extends
        StagerList<IncomingContactRequestsViewModel, ContactRecyclerViewAdapter, Contact> {

    @Override
    protected Class<IncomingContactRequestsViewModel> getViewModelType() {
        return IncomingContactRequestsViewModel.class;
    }

    @Override
    protected Class<ContactRecyclerViewAdapter> getAdapterType() {
        return ContactRecyclerViewAdapter.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_incoming_contact_request;
    }

    @Override
    protected void onItemClick(Contact item, int pos, View view) {
        super.onItemClick(item, pos, view);
        Bundle args = new Bundle();
        args.putString(ContactInfoFragment.ARG_CONTACT_NAME, item.getName());
        args.putString(ContactInfoFragment.ARG_CONTACT_KEY, item.getKey());
        args.putString(ContactInfoFragment.ARG_CONTACT_TYPE, ContactType.INCOMING.name());
        navigator.navigate(R.id.transition_contact_requests_to_contact_info, args);
    }
}