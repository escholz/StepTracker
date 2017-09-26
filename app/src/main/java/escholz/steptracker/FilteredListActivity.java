package escholz.steptracker;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import escholz.steptracker.models.StepAggregate;
import escholz.steptracker.models.StepSession;

/**
 * Display a list of sessions
 */
public class FilteredListActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_STEP_AGGREGATE = "sessionFilterId";

    private RecyclerView recyclerView;
    private StepAggregate stepAggregate;

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
        final ContentAdapter contentAdapter = new ContentAdapter();
        contentAdapter.setItems(Arrays.asList(
                new StepSession(UUID.randomUUID().toString()),
                new StepSession(UUID.randomUUID().toString()),
                new StepSession(UUID.randomUUID().toString()),
                new StepSession(UUID.randomUUID().toString()),
                new StepSession(UUID.randomUUID().toString())
        ));
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

        final Object item = view.getTag(R.id.tag_item);
        if (item instanceof StepSession) {
            launchSessionDetail(((StepSession) item).id);
        }
    }

    private void launchSessionDetail(String stepSessionId) {
        final Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_SESSION_ID, stepSessionId);
        startActivity(intent);
    }

    /**
     * ViewHolder for ContentAdapter
     * Holds onto View references and allows RecyclerView to re-use this pre-inflated instance
     * when scrolling new items into View.
     */
    public static final class ContentHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView sessionIdView;
        private final TextView distanceView;
        private final TextView startTimeView;
        private final TextView endTimeView;
        private final View.OnClickListener onClickListener;
        private final DateFormat dateFormat;

        public ContentHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            this.onClickListener = onClickListener;

            imageView = itemView.findViewById(R.id.image_view);
            sessionIdView = itemView.findViewById(R.id.session_id);
            distanceView = itemView.findViewById(R.id.distance);
            startTimeView = itemView.findViewById(R.id.start_time);
            endTimeView = itemView.findViewById(R.id.end_time);
            dateFormat = SimpleDateFormat.getDateTimeInstance();
        }

        public void onBind(@Nullable StepSession stepSession) {
            final Resources resources = itemView.getContext().getResources();
            itemView.setOnClickListener(onClickListener);
            itemView.setTag(R.id.tag_item, stepSession);
            imageView.setImageResource(R.drawable.ic_session_default);

            if (stepSession == null) {
                sessionIdView.setText(null);
                distanceView.setText(null);
                startTimeView.setText(null);
                endTimeView.setText(null);
            } else {
                sessionIdView.setText(stepSession.id);
                distanceView.setText(resources.getQuantityString(R.plurals.miles,
                        Math.round(stepSession.getDistance()), stepSession.getDistance()));
                startTimeView.setText(dateFormat.format(stepSession.getStartTime()));
                endTimeView.setText(dateFormat.format(stepSession.getEndTime()));
            }
        }
    }

    /**
     * RecyclerView.Adapter for ContentHolder
     * Constructs and binds backing data to recycled ViewHolder
     */
    public static final class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {

        private final List<StepSession> items = new ArrayList<>();
        private View.OnClickListener onClickListener;

        @Override
        public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View rootView = inflater.inflate(R.layout.holder_session, parent, false);
            return new ContentHolder(rootView, onClickListener);
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

        public void setItems(List<StepSession> newItems) {
            items.clear();
            if (newItems != null)
                items.addAll(newItems);
            notifyDataSetChanged();
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            notifyDataSetChanged();
        }
    }
}
