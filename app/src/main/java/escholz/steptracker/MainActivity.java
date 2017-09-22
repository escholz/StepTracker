package escholz.steptracker;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import escholz.steptracker.models.StepAggregate;

/**
 * Launch Activity
 * Displays aggregate distance filtered by time as a dashboard
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GRID_COLUMN_COUNT = 2;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        ContentAdapter contentAdapter = new ContentAdapter();
        contentAdapter.setItems(Arrays.asList(
            new StepAggregate(
                    R.id.aggregate_day_view_id,
                    R.string.aggregate_day,
                    R.drawable.ic_aggregate_day,
                    R.integer.aggregate_day_duration),
            new StepAggregate(
                    R.id.aggregate_week_view_id,
                    R.string.aggregate_week,
                    R.drawable.ic_aggregate_week,
                    R.integer.aggregate_week_duration),
            new StepAggregate(
                    R.id.aggregate_month_view_id,
                    R.string.aggregate_month,
                    R.drawable.ic_aggregate_month,
                    R.integer.aggregate_month_duration),
            new StepAggregate(
                    R.id.aggregate_year_view_id,
                    R.string.aggregate_year,
                    R.drawable.ic_aggregate_year,
                    R.integer.aggregate_year_duration),
            new StepAggregate(
                    R.id.aggregate_all_view_id,
                    R.string.aggregate_all,
                    R.drawable.ic_aggregate_all,
                    R.integer.aggregate_all_duration)
        ));
        contentAdapter.setOnClickListener(this);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, GRID_COLUMN_COUNT));
    }

    @Override
    public void onClick(View view) {
        if (view == null)
            return;

        final Object item = view.getTag(R.id.tag_item);
        if (item instanceof StepAggregate) {
            switch (view.getId()) {
                case R.id.aggregate_day_view_id:
                case R.id.aggregate_week_view_id:
                case R.id.aggregate_month_view_id:
                case R.id.aggregate_year_view_id:
                case R.id.aggregate_all_view_id:
                    startFilteredListActivity((StepAggregate)item);
            }
        }
    }

    private void startFilteredListActivity(StepAggregate stepAggregate) {
        final Intent intent = new Intent(this, FilteredListActivity.class);
        intent.putExtra(FilteredListActivity.EXTRA_STEP_AGGREGATE, stepAggregate);
        startActivity(intent);
    }

    /**
     * ViewHolder for ContentAdapter
     * Holds onto View references and allows RecyclerView to re-use this pre-inflated instance
     * when scrolling new items into View.
     */
    protected static final class ContentHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView labelView;

        public ContentHolder(View itemView, @Nullable View.OnClickListener onClickListener) {
            super(itemView);
            itemView.setOnClickListener(onClickListener);

            imageView = itemView.findViewById(R.id.image_view);
            labelView = itemView.findViewById(R.id.label);
        }

        public void onBind(@Nullable StepAggregate item) {
            itemView.setTag(R.id.tag_item, item);

            if (item == null) {
                itemView.setId(View.NO_ID);
                imageView.setImageResource(R.drawable.ic_aggregate_unknown);
                labelView.setText(null);
            } else {
                itemView.setId(item.viewResourceId);
                imageView.setImageResource(item.iconResourceId);
                labelView.setText(item.labelResourceId);
            }
        }
    }

    /**
     * RecyclerView.Adapter for ContentHolder
     * Constructs and binds backing data to recycled ViewHolder
     */
    protected static final class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {

        private final List<StepAggregate> items = new ArrayList<>();
        private View.OnClickListener onClickListener;

        @Override
        public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View rootView = inflater.inflate(R.layout.holder_aggregate, parent, false);
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

        public void setItems(List<StepAggregate> newItems) {
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
