package escholz.steptracker.database;

public interface RecordAvailabilityListener<T extends Record> {

    /**
     *
     *
     * @param record
     */
    void onRecordAvailable(T record);
}
