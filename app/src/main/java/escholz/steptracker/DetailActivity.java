package escholz.steptracker;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import escholz.steptracker.database.DataStore;
import escholz.steptracker.database.FirebaseDataStore;
import escholz.steptracker.database.RecordSet;
import escholz.steptracker.models.Step;
import escholz.steptracker.models.StepSession;

/**
 * Display a list of Location objects
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_SESSION_ID = "sessionId";

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private String stepSessionId;
    private FirebaseContentAdapter<Step> contentAdapter;

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
        floatingActionButton = findViewById(R.id.add_location_button);
        floatingActionButton.setOnClickListener(this);

        FirebaseDataStore dataStore = new FirebaseDataStore(FirebaseDatabase.getInstance(), FirebaseAuth.getInstance());
        RecordSet<Step> recordSet = dataStore.getRecordSet(DataStore.TABLE_LOCATION, EXTRA_SESSION_ID, stepSessionId, Step.class);
        contentAdapter = new FirebaseContentAdapter<>(recordSet, new ContentHolderFactory());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contentAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_SESSION_ID, stepSessionId);
    }

    @Override
    public void onClick(View view) {
        if (view == null)
            return;

        switch (view.getId()) {
            case R.id.add_location_button:
                contentAdapter.insert(new Step(stepSessionId));
                break;
        }
    }

    public static final class ContentHolderFactory extends FirebaseContentAdapter.ContentHolderFactory<Step> {

        public ContentHolderFactory() {
            super(R.layout.holder_location);
        }

        @Override
        public FirebaseContentAdapter.ContentHolder<Step> create(View rootView, View.OnClickListener onClickListener) {
            return new ContentHolder(rootView, onClickListener);
        }

        public static final class ContentHolder extends FirebaseContentAdapter.ContentHolder<Step> {
            private final ImageView imageView;
            private final TextView timestampView;
            private final TextView longitudeView;
            private final TextView latitudeView;
            private final DateFormat dateFormat;

            public ContentHolder(View itemView, View.OnClickListener onClickListener) {
                super(itemView, onClickListener);

                imageView = itemView.findViewById(R.id.image_view);
                timestampView = itemView.findViewById(R.id.timestamp);
                longitudeView = itemView.findViewById(R.id.longitude);
                latitudeView = itemView.findViewById(R.id.latitude);
                dateFormat = SimpleDateFormat.getDateTimeInstance();
            }

            @Override
            void onBind(@Nullable Step record) {
                imageView.setImageResource(R.drawable.ic_location_default);
                if (record == null) {
                    timestampView.setText(null);
                    longitudeView.setText(null);
                    latitudeView.setText(null);
                } else {
                    timestampView.setText(dateFormat.format(new Date(record.getCreatedAt())));
                    longitudeView.setText(String.format(Locale.getDefault(), "%1$s",
                            record.getLongitude()));
                    latitudeView.setText(String.format(Locale.getDefault(), "%1$s",
                            record.getLatitude()));
                }
            }
        }
    }
}
