package escholz.steptracker.models;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import escholz.steptracker.database.Record;

public class Step implements Record {

    private static final String FIELD_ID = "id";
    private static final String FIELD_SESSION_ID = "sessionId";
    private static final String FIELD_LATITUDE = "latitude";
    private static final String FIELD_LONGITUDE = "longitude";
    private static final String FIELD_CREATED_AT = "created_at";

    public String id;
    public String sessionId;
    public String latitude;
    public String longitude;
    public long createdAt;

    public Step() {
    }

    public Step(@NonNull String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    public Step(String id, String sessionId, String latitude, String longitude, long createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setStartTime(Date startTime) {
        createdAt = startTime.getTime()/1000;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, id);
        map.put(FIELD_SESSION_ID, sessionId);
        map.put(FIELD_LATITUDE, latitude);
        map.put(FIELD_LONGITUDE, longitude);
        map.put(FIELD_CREATED_AT, createdAt);
        return map;
    }
}
