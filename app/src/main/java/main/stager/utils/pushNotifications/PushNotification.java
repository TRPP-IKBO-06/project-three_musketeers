package main.stager.utils.pushNotifications;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Singular;
import main.stager.SECRETS;
import main.stager.StagerApplication;

@Builder(setterPrefix = "set")
public class PushNotification {
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final Map<String, String> HEADERS =
            new HashMap<String, String>() {{
        put("Authorization", "key=" + SECRETS.SERVER_KEY);
        put("Content-Type", "application/json");
    }};
    private static final Response.ErrorListener ignoreError = (x)->{};
    private static final Response.Listener<JSONObject> ignoreResponse = (x)->{};

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    @Builder.Default
    protected Response.Listener<JSONObject> onResponseListener = ignoreResponse;
    @Builder.Default
    protected Response.ErrorListener onErrorListener = ignoreError;
    protected String title;
    protected String message;
    @Singular(value = "extra", ignoreNullCollections = true)
    protected Map<String, String> payload;

    public boolean send(String topic) {
        String TOPIC = "/topics/" + topic;
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            if (title != null)
                notificationBody.put(TITLE, title);

            if (message != null)
                notificationBody.put(MESSAGE, message);

            if (payload != null)
                for (Map.Entry<String, String> entry: payload.entrySet())
                    notificationBody.put(entry.getKey(), entry.getValue());

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) { return false; }
        sendNotification(notification);
        return true;
    }

    private void sendNotification(JSONObject notification) {
        StagerApplication.getRequestQueueController().addToRequestQueue(
                new JsonObjectRequest(FCM_API, notification,
                onResponseListener, onErrorListener) {
            @Override @NotNull public
            Map<String, String> getHeaders() { return HEADERS; }}
        );
    }
}
