package escholz.steptracker.database;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDataStore implements DataStore {

    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

    /**
     *
     * @param database
     */
    public FirebaseDataStore(FirebaseDatabase database, FirebaseAuth auth) {
        this.database = database;
        this.auth = auth;
    }

    /**
     *
     *
     * @param name
     * @param orderByField
     * @param matchValue
     * @param classReference
     * @param <T>
     * @return
     */
    @Nullable
    public <T extends Record> RecordSet<T> getRecordSet(final String name,
                                                        final String orderByField,
                                                        final String matchValue,
                                                        final Class<T> classReference) {
        if (auth.getCurrentUser() != null)
        {
            final String userId = auth.getCurrentUser().getUid();
            DatabaseReference recordSet = database.getReference(userId).child(name);
            if (!TextUtils.isEmpty(orderByField))
                recordSet = recordSet.orderByChild(orderByField).equalTo(matchValue).getRef();
            return new FirebaseRecordSet<>(recordSet, classReference);
        }
        return null;
    }

    /**
     *
     *
     * @param name
     * @param orderByField
     * @param startValue
     * @param endValue
     * @param classReference
     * @param <T>
     * @return
     */
    public <T extends Record> RecordSet<T> getRecordSet(final String name,
                                                        final String orderByField,
                                                        final double startValue,
                                                        final double endValue,
                                                        final Class<T> classReference) {
        if (auth.getCurrentUser() != null)
        {
            final String userId = auth.getCurrentUser().getUid();
            DatabaseReference recordSet = database.getReference(userId).child(name);
            if (!TextUtils.isEmpty(orderByField))
                recordSet = recordSet.orderByChild(orderByField)
                        .startAt(startValue)
                        .endAt(endValue)
                        .getRef();
            return new FirebaseRecordSet<>(recordSet, classReference);
        }
        return null;
    }
}