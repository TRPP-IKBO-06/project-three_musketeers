package main.stager.ui.monitored_action;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import static main.stager.utils.DataProvider.INVALID_ACTION_KEY;
import static main.stager.utils.DataProvider.INVALID_CONTACT_KEY;
import main.stager.R;
import main.stager.StagerApplication;
import main.stager.list.StagerList;
import main.stager.model.Stage;
import main.stager.ui.contact_info.ContactInfoFragment;
import main.stager.ui.edit_item.edit_action.ActionStageRecyclerViewAdapter;
import main.stager.utils.Utilits;
import main.stager.utils.pushNotifications.ListenedEventsController;

public class MonitoredActionFragment
        extends StagerList<MonitoredActionViewModel, ActionStageRecyclerViewAdapter, Stage> {
    static public final String ARG_ACTION_NAME = "Stager.monitored_action.param_action_name";
    static public final String ARG_ACTION_KEY = "Stager.monitored_action.param_action_key";
    static public final String ARG_ACTION_OWNER = "Stager.monitored_action.param_action_owner";
    private Animation animEnablingBtnAborted;
    private Animation animDisablingBtnAborted;
    private Animation animEnablingBtnSucceed;
    private Animation animDisablingBtnSucceed;
    private String mActionName;
    private String mActionKey;
    private String mActionOwner;
    private View btnToggleOnAbortedListen;
    private View btnToggleOnSuccessListen;
    private static final float OFF_STATE_ALPHA = 0.2f;
    private final ListenedEventsController mEvents = StagerApplication.getListenedEventsController();
    private String ACTION_ABORTED;
    private String ACTION_SUCCEED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActionName = Utilits.getDefaultOnNullOrBlank(getArguments().getString(ARG_ACTION_NAME),
                    getString(R.string.MonitoredActionFragment_message_UntitledAction));
            mActionKey = Utilits.getDefaultOnNullOrBlank(
                getArguments().getString(ARG_ACTION_KEY),
                INVALID_ACTION_KEY
            );
            mActionOwner = Utilits.getDefaultOnNullOrBlank(
                getArguments().getString(ARG_ACTION_OWNER),
                INVALID_CONTACT_KEY
            );
        } else {
            mActionName = getString(R.string.MonitoredActionFragment_message_UntitledAction);
            mActionKey = INVALID_ACTION_KEY;
            mActionOwner = INVALID_CONTACT_KEY;
        }
        ACTION_ABORTED = dataProvider.getActionCompleteAbortedEventName(
                mActionOwner, mActionKey);
        ACTION_SUCCEED = dataProvider.getActionCompleteSucceedEventName(
                mActionOwner, mActionKey);

        animEnablingBtnAborted = AnimationUtils.loadAnimation(getContext(), R.anim.enable_aborted);
        animDisablingBtnAborted = AnimationUtils.loadAnimation(getContext(), R.anim.disable_aborted);

        animEnablingBtnSucceed = AnimationUtils.loadAnimation(getContext(), R.anim.enable_succeed);
        animDisablingBtnSucceed = AnimationUtils.loadAnimation(getContext(), R.anim.disable_succeed);
    }

    @Override
    protected Class<MonitoredActionViewModel> getViewModelType() {
        return MonitoredActionViewModel.class;
    }

    @Override
    protected Class<ActionStageRecyclerViewAdapter> getAdapterType() {
        return ActionStageRecyclerViewAdapter.class;
    }

    @Override
    protected void setDependencies() {
        super.setDependencies();
        dependencies.add(dataProvider.getMonitoredAction(mActionOwner,
                                                         mActionKey));
        dependencies.add(dataProvider.getAction(mActionOwner, mActionKey));
    }

    @Override
    protected int getViewBaseLayoutId() {
        return R.layout.fragment_monitored_action;
    }

    @Override
    protected void setEventListeners() {
        super.setEventListeners();
        view.findViewById(R.id.btn_open_action_owner)
                .setOnClickListener(e -> {
                    Bundle args = new Bundle();
                    args.putString(ContactInfoFragment.ARG_CONTACT_KEY, mActionOwner);
                    navigator.navigate(R.id.transition_monitored_action_to_contact_info, args);
        });

        animEnablingBtnAborted.setAnimationListener(new OnAnimationEnded() {
            @Override
            public void onAnimationEnd(Animation animation) {
                btnToggleOnAbortedListen.setAlpha(1f);
            }
        });

        animDisablingBtnAborted.setAnimationListener(new OnAnimationEnded() {
            @Override
            public void onAnimationEnd(Animation animation) {
                btnToggleOnAbortedListen.setAlpha(OFF_STATE_ALPHA);
            }
        });

        animEnablingBtnSucceed.setAnimationListener(new OnAnimationEnded() {
            @Override
            public void onAnimationEnd(Animation animation) {
                btnToggleOnSuccessListen.setAlpha(1f);
            }
        });

        animDisablingBtnSucceed.setAnimationListener(new OnAnimationEnded() {
            @Override
            public void onAnimationEnd(Animation animation) {
                btnToggleOnSuccessListen.setAlpha(OFF_STATE_ALPHA);
            }
        });

        btnToggleOnAbortedListen.setOnClickListener(e -> {
            boolean isEventListenedNow = !mEvents.isEventListened(ACTION_ABORTED);
            dataProvider.setSubscribe(ACTION_ABORTED, isEventListenedNow);
            setStateAlpha(btnToggleOnAbortedListen, isEventListenedNow);
        });

        btnToggleOnSuccessListen.setOnClickListener(e -> {
            boolean isEventListenedNow = !mEvents.isEventListened(ACTION_SUCCEED);
            dataProvider.setSubscribe(ACTION_SUCCEED, isEventListenedNow);
            setStateAlpha(btnToggleOnSuccessListen, isEventListenedNow);
        });
    }

    private void setStateAlpha(View v, boolean full) {
        Animation animEnabling = null, animDisabling = null;
        if (v == btnToggleOnAbortedListen) {
            animEnabling = animEnablingBtnAborted;
            animDisabling = animDisablingBtnAborted;
        } else if (v == btnToggleOnSuccessListen) {
            animEnabling = animEnablingBtnSucceed;
            animDisabling = animDisablingBtnSucceed;
        }
        v.startAnimation(full ? animEnabling : animDisabling);
    }

    @Override
    protected void setObservers() {
        super.setObservers();
        bindData(viewModel.getActionName(), this::updateTitle);
    }

    private void updateTitle(String text) {
        if (getActionBar() != null)
            getActionBar().setTitle(getString(R.string.MonitoredActionFragment_title,
                Utilits.getDefaultOnNullOrBlank(text,
                getString(R.string.MonitoredActionFragment_message_UntitledAction)
            )));
    }

    @Override
    protected void prepareFragmentComponents() {
        super.prepareFragmentComponents();
        btnToggleOnAbortedListen = view.findViewById(R.id.btn_toggle_onAbortedListen);
        btnToggleOnAbortedListen.setAlpha(mEvents.isEventListened(ACTION_ABORTED) ? 1f : OFF_STATE_ALPHA);

        btnToggleOnSuccessListen = view.findViewById(R.id.btn_toggle_onSuccessListen);
        btnToggleOnSuccessListen.setAlpha(mEvents.isEventListened(ACTION_SUCCEED) ? 1f : OFF_STATE_ALPHA);

        updateTitle(mActionName);
    }

    @Override
    protected void setViewModelData() {
        super.setViewModelData();
        viewModel.setActionKey(mActionKey);
        viewModel.setActionOwner(mActionOwner);
    }

    static abstract class OnAnimationEnded implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
