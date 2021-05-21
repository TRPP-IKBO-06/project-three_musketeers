package main.stager.utils;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import org.jetbrains.annotations.Contract;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

class BatchUpdate {
    @Getter private HashMap<String, Object> map;
    @Getter private DatabaseReference root;
    private String rootPath;

    private BatchUpdate(Query root) {
        map = new HashMap<>();
        if (root == null) return;
        this.root = root.getRef();
        rootPath = getPath(root);
    }

    @NonNull
    @Contract(" -> new")
    public static BatchUpdate init() { return new BatchUpdate(null); }

    @NonNull
    @Contract("!null -> new")
    public static BatchUpdate init(Query root) { return new BatchUpdate(root); }

    public BatchUpdate set(@NonNull Query ref, Object value) {
        String path = getPath(ref);
        if (root != null)
            path = trimPath(path, rootPath);
        map.put(path, value);
        return this;
    }

    public BatchUpdate setTrue(@NonNull Query ref) {
        return set(ref, true);
    }

    public BatchUpdate remove(@NonNull Query ref) {
        return set(ref, null);
    }

    public Task<Void> apply() {
        return root.getRef().updateChildren(trimPath(map, rootPath));
    }

    public void apply(DatabaseReference.CompletionListener callback) {
        root.getRef().updateChildren(trimPath(map, rootPath), callback);
    }

    public Task<Void> apply(@NonNull Query root) {
        return root.getRef().updateChildren(trimPath(map, getPath(root)));
    }

    public void apply(@NonNull Query root,
                      DatabaseReference.CompletionListener callback) {
        root.getRef().updateChildren(trimPath(map, getPath(root)), callback);
    }

    @NonNull
    private String trimPath(@NonNull String path,
                            @NonNull String rootPath) {
        return path.startsWith(rootPath)
                ? path.substring(rootPath.length())
                : path;
    }

    @NonNull
    private HashMap<String, Object> trimPath(@NonNull HashMap<String, Object> map,
                                             @NonNull String rootPath) {
        HashMap<String, Object> newMap = new HashMap<>();
        for (Map.Entry<String, Object> pair: map.entrySet())
            newMap.put(trimPath(pair.getKey(), rootPath), pair.getValue());
        return newMap;
    }

    @NonNull
    private String getPath(@NonNull Query ref) {
        String path = ref.getRef().toString();
        String repo = ref.getRef().getRoot().toString();
        return path.substring(repo.length());
    }
}
