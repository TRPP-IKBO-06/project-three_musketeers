package main.stager.linkers;

import androidx.annotation.DrawableRes;
import org.jetbrains.annotations.NotNull;
import main.stager.R;
import main.stager.model.Status;

public class LStatus {
    public static @DrawableRes int toIcon(@NotNull Status status) {
        switch (status) {
            case ABORTED: return R.drawable.ic_status_aborted;
            case SUCCEED: return R.drawable.ic_status_succed;
            case EVALUATING:
            case WAITING: return R.drawable.ic_status_waiting;
            case LOCKED:  return R.drawable.ic_status_locked;
            default: throw new IllegalArgumentException();
        }
    }
}
