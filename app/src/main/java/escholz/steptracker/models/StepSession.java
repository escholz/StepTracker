package escholz.steptracker.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import escholz.steptracker.database.Record;

/**
 * Collection of steps
 */
@IgnoreExtraProperties
public class StepSession implements Record {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_CLOSED_AT = "closedAt";

    public String id;
    private final List<Step> steps = new ArrayList<>();
    public long createdAt;
    public long closedAt;

    public StepSession() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void close() {
        closedAt = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
    }

    public boolean isClosed() {
        return closedAt > 0;
    }

    public void addStep(@NonNull Step step) {
        steps.add(step);
    }

    public List<Step> getSteps() {
        return steps;
    }

    public float getDistance() {
        // TODO: Query Distance
        return 0.0f;
    }

    @Override
    public void setStartTime(Date startTime) {
        createdAt = startTime.getTime()/1000;
    }

    public Date getStartTime() {
        return new Date(createdAt*1000);
    }

    public Date getEndTime() {
        if (isClosed())
            return new Date(closedAt);
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>(2);
        map.put(FIELD_ID, id);
        map.put(FIELD_CREATED_AT, createdAt);
        map.put(FIELD_CLOSED_AT, closedAt);
        return map;
    }
}
