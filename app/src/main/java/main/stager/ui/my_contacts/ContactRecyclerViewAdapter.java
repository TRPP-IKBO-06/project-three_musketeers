package main.stager.ui.my_contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import main.stager.R;
import main.stager.UserAvatar;
import main.stager.list.StagerListAdapter;
import main.stager.model.Contact;
import main.stager.utils.Utilits;

/**
 * {@link RecyclerView.Adapter} для отображения {@link Contact}.
 */
public class ContactRecyclerViewAdapter
        extends StagerListAdapter<Contact, ContactRecyclerViewAdapter.ViewHolder> {

    private static DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new FBItemCallback<Contact>() {
        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public ContactRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_item_contact,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = getItem(position);

        TextView textView = (TextView) holder.mView.findViewById(R.id.item_email);
        if (Utilits.isNullOrBlank(holder.mItem.getName()))
            holder.mNameView.setText(R.string.ContactInfoFragment_message_AnonymousUser);
        else
            holder.mNameView.setText(holder.mItem.getName());

        if (Utilits.isNullOrBlank(holder.mItem.getEmail())) {
            holder.mEmailView.setVisibility(View.GONE);
            textView.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0f));
        }
        else
            holder.mEmailView.setText(holder.mItem.getEmail());

        holder.mAvatar.setEmail(holder.mItem.getEmail());
        holder.mAvatar.setUserName(holder.mItem.getName());
        bindOnItemClickListener(holder.mView, holder.mItem, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mEmailView;
        public final UserAvatar mAvatar;
        public Contact mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.item_name);
            mEmailView = view.findViewById(R.id.item_email);
            mAvatar = view.findViewById(R.id.item_avatar);
        }
    }
}