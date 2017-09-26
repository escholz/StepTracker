package escholz.steptracker;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import escholz.steptracker.models.StepSession;

/**
 * Display a list of Location objects
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_SESSION_ID = "sessionId";

    private RecyclerView recyclerView;
    private String stepSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Intent callingIntent = getIntent();
        if (savedInstanceState != null) {
            stepSessionId = savedInstanceState.getString(EXTRA_SESSION_ID);
        } else if (callingIntent !=null) {
            stepSessionId = callingIntent.getStringExtra(EXTRA_SESSION_ID);
        }

        if (TextUtils.isEmpty(stepSessionId))
            finish();

        setTitle(stepSessionId);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ContentAdapter contentAdapter = new ContentAdapter();
        contentAdapter.setItems(Arrays.asList(
                createLocation(),
                createLocation(),
                createLocation(),
                createLocation(),
                createLocation(),
                createLocation()
        ));
        recyclerView.setAdapter(contentAdapter);
    }

    private Location createLocation() {
        final Location location = new Location("Canned");
        location.setTime(System.currentTimeMillis());
        location.setLatitude(0.0f);
        location.setLongitude(1.0f);
        return location;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_SESSION_ID, stepSessionId);
    }

    /**
     * ViewHolder for ContentAdapter
     * Holds onto View references and allows RecyclerView to re-use this pre-inflated instance
     * when scrolling new items into View.
     */
    public static final class ContentHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView timestampView;
        private final TextView longitudeView;
        private final TextView latitudeView;
        private final DateFormat dateFormat;

        public ContentHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            timestampView = itemView.findViewById(R.id.timestamp);
            longitudeView = itemView.findViewById(R.id.longitude);
            latitudeView = itemView.findViewById(R.id.latitude);
            dateFormat = SimpleDateFormat.getDateTimeInstance();
        }

        public void onBind(@Nullable Location location) {
            imageView.setImageResource(R.drawable.ic_location_default);
            if (location == null) {
                timestampView.setText(null);
                longitudeView.setText(null);
                latitudeView.setText(null);
            } else {
                timestampView.setText(dateFormat.format(new Date(location.getTime())));
                longitudeView.setText(String.format(Locale.getDefault(), "%1$f",
                        location.getLongitude()));
                latitudeView.setText(String.format(Locale.getDefault(), "%1$f",
                        location.getLatitude()));
            }
        }
    }

    /**
     * RecyclerView.Adapter for ContentHolder
     * Constructs and binds backing data to recycled ViewHolder
     */
    public static final class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {

        private List<Location> items = new ArrayList<>();

        @Override
        public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View rootView = inflater.inflate(R.layout.holder_location, parent, false);
            return new ContentHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ContentHolder holder, int position) {
            if (holder != null && position >= 0 && position < items.size())
                holder.onBind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setItems(List<Location> newItems) {
            items.clear();
            if (newItems != null)
                items.addAll(newItems);
            notifyDataSetChanged();
        }
    }
}
