package escholz.steptracker.database;

/**
 * TODO: Write me
 * @param <T>
 */
public interface RecordChangeListener<T extends Record> {

    /**
     * TODO: Write me!
     *
     * @param record
     * @param action
     * @param previousKey
     */
    void onRecordChanged(T record, int action, String previousKey);
}
