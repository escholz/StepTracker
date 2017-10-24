package escholz.steptracker.database;

import java.util.Date;
import java.util.Map;

public interface Record {

    String getId();

    void setId(String id);

    void setStartTime(Date startTime);

    Map<String, Object> toMap();
}
