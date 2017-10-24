package escholz.steptracker.database;

import android.support.annotation.Nullable;

public interface RecordSet<T extends Record> {
    int ACTION_CREATE = 1;
    int ACTION_UPDATE = 2;
    int ACTION_DELETE = 3;
    int ACTION_MOVE = 4;

    @Nullable
    String create(T newItem);

    void update(T item);

    void read(String id, RecordAvailabilityListener<T> listener);

    void delete(String id);

    void setRecordChangeListener(RecordChangeListener<T> recordChangeListener);
}
