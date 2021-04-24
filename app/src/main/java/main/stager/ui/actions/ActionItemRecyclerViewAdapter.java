package main.stager.ui.actions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import main.stager.R;
import main.stager.list.StagerListAdapter;
import main.stager.model.UserAction;
import main.stager.utils.Utilits;

/**
 * {@link RecyclerView.Adapter} для отображения {@link UserAction}.
 */
public class ActionItemRecyclerViewAdapter
        extends StagerListAdapter<UserAction, ActionItemRecyclerViewAdapter.ViewHolder> {

    private static DiffUtil.ItemCallback<UserAction> DIFF_CALLBACK = new FBItemCallback<UserAction>() {
        @Override
        public boolean areContentsTheSame(@NonNull UserAction oldItem, @NonNull UserAction newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getStatus().equals(newItem.getStatus());
        }
    };

    public ActionItemRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_item,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = getItem(position);

        if (Utilits.isNullOrBlank(holder.mItem.getName()))
            holder.mContentView.setText(R.string.EditActionFragment_message_UntitledAction);
        else
            holder.mContentView.setText(holder.mItem.getName());

        switch (holder.mItem.getStatus()) {
            case ABORTED:
                holder.mStatusView.setImageResource(
                        R.drawable.ic_stage_status_abort);
                break;

            case SUCCEED:
                holder.mStatusView.setImageResource(
                        R.drawable.ic_stage_status_succes);
                break;

            case WAITING:
                holder.mStatusView.setImageResource(
                        R.drawable.ic_stage_status_wait);
                break;
        }
        bindOnItemClickListener(holder.mView, holder.mItem, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mStatusView;
        public UserAction mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.item_name);
            mStatusView = view.findViewById(R.id.item_icon);
        }
    }
}