package escholz.steptracker;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import escholz.steptracker.database.DataStore;
import escholz.steptracker.database.FirebaseDataStore;
import escholz.steptracker.database.RecordSet;
import escholz.steptracker.models.StepAggregate;
import escholz.steptracker.models.StepSession;

/**
 * Display a list of sessions
 */
public class FilteredListActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_STEP_AGGREGATE = "sessionFilterId";

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private StepAggregate stepAggregate;
    private FirebaseContentAdapter<StepSession> contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_list);

        final Intent callingIntent = getIntent();
        if (savedInstanceState != null) {
            stepAggregate = savedInstanceState.getParcelable(EXTRA_STEP_AGGREGATE);
        } else if (callingIntent != null) {
            stepAggregate = callingIntent.getParcelableExtra(EXTRA_STEP_AGGREGATE);
        }

        if (stepAggregate == null)
            finish();

        setTitle(stepAggregate.labelResourceId);

        recyclerView = findViewById(R.id.recycler_view);
        floatingActionButton = findViewById(R.id.add_session_button);
        floatingActionButton.setOnClickListener(this);

        FirebaseDataStore dataStore = new FirebaseDataStore(FirebaseDatabase.getInstance(),
                FirebaseAuth.getInstance());
        final long nowInSec = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()/1000;
        RecordSet<StepSession> sessions = dataStore.getRecordSet(DataStore.TABLE_SESSION,
                StepSession.FIELD_CREATED_AT, stepAggregate.getEndTime(this, nowInSec), nowInSec,
                StepSession.class);
        contentAdapter = new FirebaseContentAdapter<>(sessions, new ContentHolderFactory());
        contentAdapter.setOnClickListener(this);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_STEP_AGGREGATE, stepAggregate);
    }

    @Override
    public void onClick(View view) {
        if (view == null)
            return;

        switch (view.getId()) {
            case R.id.add_session_button:
                contentAdapter.insert(new StepSession());
                break;
            case R.id.session_summary:
                final Object item = view.getTag(R.id.tag_item);
                if (item instanceof StepSession) {
                    launchSessionDetail(((StepSession) item).id);
                }
                break;
        }
    }

    private void launchSessionDetail(String stepSessionId) {
        final Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_SESSION_ID, stepSessionId);
        startActivity(intent);
    }

    public static final class ContentHolderFactory extends FirebaseContentAdapter.ContentHolderFactory<StepSession> {

        public ContentHolderFactory() {
            super(R.layout.holder_session);
        }

        @Override
        public FirebaseContentAdapter.ContentHolder<StepSession> create(View rootView, View.OnClickListener onClickListener) {
            return new ContentHolder(rootView, onClickListener);
        }

        public static final class ContentHolder extends FirebaseContentAdapter.ContentHolder<StepSession> {
            private final ImageView imageView;
            private final TextView sessionIdView;
            private final TextView distanceView;
            private final TextView startTimeView;
            private final TextView endTimeView;
            private final DateFormat dateFormat;

            public ContentHolder(View itemView, View.OnClickListener onClickListener) {
                super(itemView, onClickListener);

                imageView = itemView.findViewById(R.id.image_view);
                sessionIdView = itemView.findViewById(R.id.session_id);
                distanceView = itemView.findViewById(R.id.distance);
                startTimeView = itemView.findViewById(R.id.start_time);
                endTimeView = itemView.findViewById(R.id.end_time);
                dateFormat = SimpleDateFormat.getDateTimeInstance();
            }

            @Override
            public void onBind(@Nullable StepSession record) {
                final Resources resources = itemView.getContext().getResources();
                itemView.setTag(R.id.tag_item, record);
                imageView.setImageResource(R.drawable.ic_session_default);

                if (record == null) {
                    sessionIdView.setText(null);
                    distanceView.setText(null);
                    startTimeView.setText(null);
                    endTimeView.setText(null);
                } else {
                    sessionIdView.setText(record.id);
                    distanceView.setText(resources.getQuantityString(R.plurals.miles,
                            Math.round(record.getDistance()), record.getDistance()));
                    Date startTime = record.getStartTime();
                    if (startTime != null)
                        startTimeView.setText(dateFormat.format(startTime));
                    else
                        startTimeView.setText(null);
                    Date endTime = record.getEndTime();
                    if (endTime != null)
                        endTimeView.setText(dateFormat.format(endTime));
                    else
                        endTimeView.setText(null);
                }
            }
        }
    }
}
