package main.stager.ui.edit_item.edit_subscribers;

import android.os.Bundle;
import android.view.View;
import main.stager.R;
import main.stager.list.StagerExtendableList;
import main.stager.model.Contact;
import main.stager.ui.contact_info.ContactInfoFragment;
import main.stager.ui.find_contacts.FindContactsFragment;
import main.stager.ui.my_contacts.ContactRecyclerViewAdapter;
import main.stager.utils.Utilits;

public class EditSubscribersFragment
        extends StagerExtendableList<EditSubscribersViewModel, ContactRecyclerViewAdapter, Contact> {
    static public final String ARG_ACTION_NAME = "Stager.stages_list.param_action_name";
    static public final String ARG_ACTION_KEY = "Stager.stages_list.param_action_key";
    private String mActionName;
    private String mActionKey;

    @Override
    public boolean ALLOW_DRAG_AND_DROP() { return false; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActionName = Utilits.getDefaultOnNullOrBlank(getArguments().getString(ARG_ACTION_NAME),
                    getString(R.string.EditActionFragment_message_UntitledAction));
            mActionKey = getArguments().getString(ARG_ACTION_KEY);
        } else {
            mActionName = getString(R.string.EditActionFragment_message_UntitledAction);
            mActionKey = "";
        }
    }

    @Override
    protected Class<EditSubscribersViewModel> getViewModelType() {
        return EditSubscribersViewModel.class;
    }

    @Override
    protected Class<ContactRecyclerViewAdapter> getAdapterType() {
        return ContactRecyclerViewAdapter.class;
    }

    @Override
    protected void setDependencies() {
        super.setDependencies();
        dependencies.add(dataProvider.getAction(mActionKey));
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_edit_subscribers;
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        bindData(viewModel.getActionName(), this::updateTitle);
    }

    private void updateTitle(String text) {
        getActionBar().setTitle(getString(R.string.EditSubscribersFragment_title,
                Utilits.getDefaultOnNullOrBlank(text,
                        getString(R.string.EditActionFragment_message_UntitledAction
                        ))));
    }

    @Override
    protected void onItemClick(Contact item, int pos, View view) {
        super.onItemClick(item, pos, view);
        Bundle args = new Bundle();
        args.putString(ContactInfoFragment.ARG_CONTACT_NAME, item.getName());
        args.putString(ContactInfoFragment.ARG_CONTACT_KEY, item.getKey());
        navigator.navigate(R.id.transition_action_subscribers_to_contact_info, args);
    }

    @Override
    protected void onButtonAddClicked(View v) {
        Bundle args = new Bundle();
        args.putString(FindContactsFragment.ARG_ACTION_KEY, mActionKey);
        navigator.navigate(R.id.transition_action_subscribers_to_find_contacts, args);
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        updateTitle(mActionName);
    }

    @Override
    protected void setViewModelData() {
        super.setViewModelData();
        viewModel.setActionKey(mActionKey);
    }
}