package escholz.steptracker.database;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

public class FirebaseRecordSet<T extends Record> implements RecordSet<T> {

    private final DatabaseReference databaseReference;
    private final Class<T> classReference;
    private RecordChangeListener<T> recordChangeListener;

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousName) {
            if (recordChangeListener != null)
                recordChangeListener.onRecordChanged(dataSnapshot.getValue(classReference), ACTION_CREATE, previousName);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousName) {
            if (recordChangeListener != null)
                recordChangeListener.onRecordChanged(dataSnapshot.getValue(classReference), ACTION_UPDATE, previousName);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            if (recordChangeListener != null)
                recordChangeListener.onRecordChanged(dataSnapshot.getValue(classReference), ACTION_DELETE, null);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousName) {
            if (recordChangeListener != null)
                recordChangeListener.onRecordChanged(dataSnapshot.getValue(classReference), ACTION_MOVE, previousName);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public FirebaseRecordSet(final DatabaseReference databaseReference,
                             final Class<T> classReference) {
        this.databaseReference = databaseReference;
        this.classReference = classReference;

        databaseReference.addChildEventListener(childEventListener);
    }

    /**
     * Store {@link Record} in this {@link RecordSet}
     *
     * @param newItem Record to store
     * @return Record identifier or null on failure
     */
    @Override
    @Nullable
    public String create(final T newItem) {
        DatabaseReference itemRef = databaseReference.push();
        String key = itemRef.getKey();
        newItem.setId(key);
        newItem.setStartTime(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
        itemRef.setValue(newItem.toMap());
        return key;
    }

    /**
     * Update {@link Record} with id {@link Record#getId()}
     *
     * @param item Updated record
     */
    @Override
    public void update(final T item) {
        DatabaseReference itemRef = databaseReference.child(item.getId());
        itemRef.updateChildren(item.toMap());
    }

    /**
     * Read {@link Record} with id {@code id}
     *
     * @param id Record identifier
     * @param listener Callback listener
     */
    @Override
    public void read(final String id, final RecordAvailabilityListener<T> listener) {
        DatabaseReference itemRef = databaseReference.child(id);
        if (listener != null) {
            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (TextUtils.equals(dataSnapshot.getKey(), id))
                        listener.onRecordAvailable(dataSnapshot.getValue(classReference));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    /**
     * Delete {@link Record} from {@link RecordSet} by id {@code id}
     *
     * @param id Record identifier
     */
    @Override
    public void delete(final String id) {
        DatabaseReference itemRef = databaseReference.child(id);
        itemRef.removeValue();
    }

    /**
     * Set {@link RecordChangeListener} for {@link RecordSet} callbacks.
     *
     * @param recordChangeListener Implementation of a callback for data change events
     */
    @Override
    public void setRecordChangeListener(RecordChangeListener<T> recordChangeListener) {
        this.recordChangeListener = recordChangeListener;
    }
}
