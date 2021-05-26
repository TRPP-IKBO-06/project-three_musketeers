package main.stager.ui.actions;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import main.stager.R;
import main.stager.linkers.LStatus;
import main.stager.list.StagerListAdapter;
import main.stager.list.StagerViewHolder;
import main.stager.model.Status;
import main.stager.model.UserAction;
import main.stager.utils.Utilits;

/**
 * {@link RecyclerView.Adapter} для отображения {@link UserAction}.
 */
public class ActionItemRecyclerViewAdapter
        extends StagerListAdapter<UserAction, ActionItemRecyclerViewAdapter.ViewHolder> {

    private Animation rotateAnimation;

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

    @Override
    protected Class<ViewHolder> getViewHolderType() {
        return ViewHolder.class;
    }

    @Override
    protected void onViewHolderCreated(View view, ViewHolder holder) {
        super.onViewHolderCreated(view, holder);
        if (rotateAnimation == null)
            rotateAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotation);
    }

    @Override
    protected void onBindViewHolderInner(final ViewHolder holder, int position) {
        if (Utilits.isNullOrBlank(holder.mItem.getName()))
            holder.mContentView.setText(R.string.EditActionFragment_message_UntitledAction);
        else
            holder.mContentView.setText(holder.mItem.getName());

        holder.mStatusView.setImageResource(LStatus.toIcon(
                holder.mItem.getStatus()));

        if (holder.mItem.getStatus() == Status.WAITING)
            holder.mStatusView.startAnimation(rotateAnimation);
    }

    public static class ViewHolder extends StagerViewHolder<UserAction> {
        public final TextView mContentView;
        public final ImageView mStatusView;

        public ViewHolder(View view) {
            super(view);
            mContentView = view.findViewById(R.id.item_name);
            mStatusView = view.findViewById(R.id.item_icon);
        }
    }
}