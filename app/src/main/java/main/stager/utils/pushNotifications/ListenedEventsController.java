package main.stager.utils.pushNotifications;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.database.annotations.NotNull;
import java.util.Set;

public class ListenedEventsController {
    private SharedPreferences events;

    private static final String LISTENED_EVENTS = "Stager.main.settings.events.listened";

    public ListenedEventsController(Context c) {
        events = c.getApplicationContext()
                  .getSharedPreferences(LISTENED_EVENTS, Context.MODE_PRIVATE);
    }

    public @NotNull Set<String> getListenedEvents() {
        return events.getAll().keySet();
    }

    public boolean isEventListened(String name) {
        return events.contains(name);
    }

    public void setEventListened(String name) {
        events.edit().putBoolean(name, true).apply();
    }

    public void setEventNotListened(String name) {
        events.edit().remove(name).apply();
    }

    public void reset() {
        events.edit().clear().apply();
    }
}
