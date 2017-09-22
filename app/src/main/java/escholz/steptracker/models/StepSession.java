package escholz.steptracker.models;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Collection of locations
 */
public class StepSession {

    public final String id;
    public final List<Location> locations = new ArrayList<>();
    private boolean isClosed;

    public StepSession(@NonNull String id) {
        this.id = id;
    }

    public void close() {
        isClosed = true;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void addLocation(@NonNull Location location) {
        locations.add(location);
    }

    public float getDistance() {
        return 0.0f;
    }

    public Date getStartTime() {
        return new Date();
    }

    public Date getEndTime() {
        return new Date();
    }
}
