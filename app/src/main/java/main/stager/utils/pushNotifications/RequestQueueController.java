package main.stager.utils.pushNotifications;

import android.content.Context;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import lombok.Getter;

public class RequestQueueController {
    @Getter private RequestQueue requestQueue;

    public RequestQueueController(@NonNull Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}