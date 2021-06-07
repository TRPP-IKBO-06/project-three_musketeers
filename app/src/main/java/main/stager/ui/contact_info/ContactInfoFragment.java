package main.stager.ui.contact_info;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import static main.stager.utils.DataProvider.INVALID_CONTACT_KEY;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.Query;
import main.stager.Base.StagerVMFragment;
import main.stager.R;
import main.stager.UserAvatar;
import main.stager.model.ContactType;
import main.stager.utils.Utilits;

public class ContactInfoFragment extends
        StagerVMFragment<ContactInfoViewModel> {
    static public final String ARG_CONTACT_NAME = "Stager.contact_info.param_contact_name";
    static public final String ARG_CONTACT_KEY = "Stager.contact_info.param_contact_key";
    static public final String ARG_CONTACT_TYPE = "Stager.contact_info.param_contact_type";
    static public final int DESCRIPTION_ONE_LINE_LIMIT = 20;

    private String key;
    private String name;
    private ContactType type;

    private TextView nameView;
    private TextView descriptionView;
    private UserAvatar mAvatar;

    private FloatingActionButton btnYES, btnNO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = Utilits.getDefaultOnNullOrBlank(
                    getArguments().getString(ARG_CONTACT_NAME),
                    getString(R.string.ContactInfoFragment_message_AnonymousUser));
            type = ContactType.valueOf(Utilits.getDefaultOnNullOrBlank(
                    getArguments().getString(ARG_CONTACT_TYPE),
                    ContactType.ACCEPTED.toString()));
            key = Utilits.getDefaultOnNullOrBlank(
                getArguments().getString(ARG_CONTACT_KEY),
                INVALID_CONTACT_KEY
            );
        } else {
            name = getString(R.string.ContactInfoFragment_message_AnonymousUser);
            key = INVALID_CONTACT_KEY;
            type = ContactType.ACCEPTED;
        }
    }

    private @StringRes int getHeaderTitle() {
        switch (type) {
            case ACCEPTED:
                return R.string.ContactInfoFragment_header_type_accepted;
            case IGNORED:
                return R.string.ContactInfoFragment_header_type_ignored;
            case INCOMING:
                return R.string.ContactInfoFragment_header_type_incoming;
            case OUTGOING:
                return R.string.ContactInfoFragment_header_type_outgoing;
            default:
                throw new IllegalStateException("Unsupported ContactType");
        }
    }

    @Override
    protected Class<ContactInfoViewModel> getViewModelType() {
        return ContactInfoViewModel.class;
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_contact_info;
    }

    @Override
    protected void setEventListeners() {
        super.setEventListeners();
        if (type == ContactType.INCOMING || type == ContactType.IGNORED) {
            btnYES.setOnClickListener((v) -> dataProvider.acceptContactRequest(key));
            btnNO.setOnClickListener(
                    type != ContactType.IGNORED
                    ? v -> dataProvider.ignoreContactRequest(key)
                    : v -> dataProvider.removeIgnoredContactRequest(key));
        } else if (type == ContactType.OUTGOING)
            btnNO.setOnClickListener(v -> dataProvider.removeOutgoingContactRequest(key));
        else if (type == ContactType.ACCEPTED)
            btnNO.setOnClickListener(v -> dataProvider.deleteContact(key));
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        mAvatar = view.findViewById(R.id.contact_user_avatar);
        mAvatar.setUserName(name);
        nameView = view.findViewById(R.id.contact_name);
        descriptionView = view.findViewById(R.id.contact_description);
        descriptionView.setMovementMethod(new ScrollingMovementMethod());

        btnNO = view.findViewById(R.id.btn_NO);
        if (type == ContactType.INCOMING || type == ContactType.IGNORED) {
            btnYES = view.findViewById(R.id.btn_YES);
            btnYES.setVisibility(View.VISIBLE);
        }

        if (type == ContactType.IGNORED)
            view.findViewById(R.id.controls).setAlpha(0.7f);

        ((TextView)view.findViewById(R.id.contact_header_title)).setText(getHeaderTitle());
        updateTitle(name);
    }

    private void updateTitle(String text) {
        if (getActionBar() != null)
            getActionBar().setTitle(getString(R.string.ContactInfoFragment_title,
            Utilits.getDefaultOnNullOrBlank(text,
                getString(R.string.ContactInfoFragment_message_AnonymousUser
            ))));
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        bindData(viewModel.getContact(), (contact) -> {
            name = Utilits.getDefaultOnNullOrBlank(contact.getName(),
                    getString(R.string.ContactInfoFragment_message_AnonymousUser));
            nameView.setText(name);
            mAvatar.setEmail(contact.getEmail());
            mAvatar.setUserName(name);

            updateField(view.findViewById(R.id.linearLayout_email),
                        view.findViewById(R.id.contact_email),
                        contact.getEmail());

            String description = contact.getDescription();
            LinearLayout descriptionLayout = view.findViewById(R.id.linearLayout_description);
            if (updateField(descriptionLayout, descriptionView, description))
                descriptionLayout.setOrientation(
                    description.trim().length() > DESCRIPTION_ONE_LINE_LIMIT
                    ? LinearLayout.VERTICAL
                    : LinearLayout.HORIZONTAL
                );

            updateTitle(name);
        });
    }

    private boolean updateField(@NonNull ViewGroup holder, TextView field, String text) {
        boolean isEmpty = Utilits.isNullOrBlank(text);
        holder.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (!isEmpty) field.setText(text.trim());
        return !isEmpty;
    }

    @Override
    protected void setViewModelData() {
        super.setViewModelData();
        viewModel.setKey(key);
    }

    @Override
    protected void setDependencies() {
        super.setDependencies();
        dependencies.add(dataProvider.getUserInfo(key));
        dependencies.add(getDependencyByType());
    }

    private Query getDependencyByType() {
        switch (type) {
            case IGNORED: return dataProvider.getIgnoredContactRequest(key);
            case INCOMING: return dataProvider.getIncomingContactRequest(key);
            case OUTGOING: return dataProvider.getOutgoingContactRequest(key);
            case ACCEPTED: return dataProvider.getContact(key);
        }
        throw new IllegalStateException("ContactType incorrect");
    }
}