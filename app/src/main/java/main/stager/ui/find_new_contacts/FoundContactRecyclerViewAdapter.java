package main.stager.ui.find_new_contacts;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import main.stager.model.Contact;
import main.stager.ui.my_contacts.ContactRecyclerViewAdapter;

/**
 * {@link RecyclerView.Adapter} для специфического отображения {@link Contact}.
 */
public class FoundContactRecyclerViewAdapter
        extends ContactRecyclerViewAdapter {

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (!holder.mItem.isIgnored()) {
            enable(holder.mView);
            holder.mView.setAlpha(holder.mItem.isOutgoing() ? 0.6f : 1f);
        } else disable(holder.mView);
    }

    public static void disable(View view) {
        view.setEnabled(false);
        view.setAlpha(0.2f);
    }

    public static void enable(View view) {
        view.setEnabled(true);
        view.setAlpha(1f);
    }
}