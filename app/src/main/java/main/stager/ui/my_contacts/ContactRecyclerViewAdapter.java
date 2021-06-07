package main.stager.ui.my_contacts;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import main.stager.R;
import main.stager.UserAvatar;
import main.stager.list.StagerListAdapter;
import main.stager.list.StagerViewHolder;
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
        return Utilits.getDefaultOnNullOrBlank(oldItem.getName(), "")
                      .equals(
               Utilits.getDefaultOnNullOrBlank(newItem.getName(), ""))
               &&
               Utilits.getDefaultOnNullOrBlank(oldItem.getEmail(), "")
                      .equals(
               Utilits.getDefaultOnNullOrBlank(newItem.getEmail(), ""));
        }
    };

    public ContactRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    protected Class<ViewHolder> getViewHolderType() {
        return ViewHolder.class;
    }

    @Override
    protected int getItemListLayout() {
        return R.layout.fragment_list_item_contact;
    }

    @Override
    protected void onBindViewHolderInner(final ViewHolder holder, int position) {
        TextView textView = (TextView) holder.mView.findViewById(R.id.item_email);
        if (Utilits.isNullOrBlank(holder.mItem.getName()))
            holder.mNameView.setText(R.string.ContactInfoFragment_message_AnonymousUser);
        else
            holder.mNameView.setText(holder.mItem.getName());

        if (Utilits.isNullOrBlank(holder.mItem.getEmail())) {
            holder.mEmailView.setVisibility(View.GONE);
            textView.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0f));
        } else {
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0f
            ));
            holder.mEmailView.setVisibility(View.VISIBLE);
            holder.mEmailView.setText(holder.mItem.getEmail());
        }

        holder.mAvatar.setEmail(holder.mItem.getEmail());
        holder.mAvatar.setUserName(holder.mItem.getName());
    }

    public static class ViewHolder extends StagerViewHolder<Contact> {
        public final TextView mNameView;
        public final TextView mEmailView;
        public final UserAvatar mAvatar;

        public ViewHolder(View view) {
            super(view);
            mNameView = view.findViewById(R.id.contact_item_name);
            mEmailView = view.findViewById(R.id.item_email);
            mAvatar = view.findViewById(R.id.item_avatar);
        }
    }
}