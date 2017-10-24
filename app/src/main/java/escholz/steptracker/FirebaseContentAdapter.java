package escholz.steptracker;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import escholz.steptracker.database.Record;
import escholz.steptracker.database.RecordAvailabilityListener;
import escholz.steptracker.database.RecordChangeListener;
import escholz.steptracker.database.RecordSet;
import escholz.steptracker.models.StepSession;

public final class FirebaseContentAdapter<T extends Record>
        extends RecyclerView.Adapter<FirebaseContentAdapter.ContentHolder<T>> {

    private final List<String> keySet = new LinkedList<>();
    private final RecordSet<T> sessionRecordSet;
    private View.OnClickListener onClickListener;
    private ContentHolderFactory<T> contentHolderFactory;
    private final RecordChangeListener<T> changeListener = new RecordChangeListener<T>() {
        @Override
        public void onRecordChanged(T record, int action, String previousKey) {
            switch (action) {
                case RecordSet.ACTION_CREATE:
                    keySet.add(record.getId());
                    notifyItemInserted(keySet.size());
                    break;
                case RecordSet.ACTION_UPDATE:
                    notifyItemChanged(keySet.indexOf(record.getId()));
                    break;
                case RecordSet.ACTION_DELETE:
                    notifyItemRemoved(keySet.indexOf(record.getId()));
                    keySet.remove(record.getId());
                    break;
                case RecordSet.ACTION_MOVE:
                    notifyItemMoved(keySet.indexOf(record.getId()), keySet.indexOf(previousKey));
                    break;
            }
        }
    };

    public FirebaseContentAdapter(RecordSet<T> sessionRecordSet,
                                  ContentHolderFactory<T> contentHolderFactory) {
        this.sessionRecordSet = sessionRecordSet;
        sessionRecordSet.setRecordChangeListener(changeListener);
        this.contentHolderFactory = contentHolderFactory;
    }

    @Override
    public ContentHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View rootView = inflater.inflate(contentHolderFactory.layoutResId, parent, false);
        return contentHolderFactory.create(rootView, onClickListener);
    }

    @Override
    public void onBindViewHolder(final ContentHolder<T> holder, int position) {
        final String key = keySet.get(position);
        if (key != null)
            sessionRecordSet.read(key, new RecordAvailabilityListener<T>() {
                @Override
                public void onRecordAvailable(T record) {
                    if (holder != null)
                        holder.onBind(record);
                }
            });
        else
            holder.onBind(null);
    }

    public void insert(T newRecord) {
        sessionRecordSet.create(newRecord);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return keySet.size();
    }

    /**
     *
     * @param <T>
     */
    public static abstract class ContentHolder<T extends Record> extends RecyclerView.ViewHolder {

        private final View.OnClickListener onClickListener;

        public ContentHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(onClickListener);
        }

        /**
         *
         * @param record
         */
        abstract void onBind(@Nullable T record);
    }

    /**
     *
     * @param <T>
     */
    public static abstract class ContentHolderFactory<T extends Record> {

        @LayoutRes
        public final int layoutResId;

        public ContentHolderFactory(@LayoutRes int layoutResId) {
            this.layoutResId = layoutResId;
        }
        /**
         *
         * @param rootView
         * @param onClickListener
         * @return
         */
        public abstract ContentHolder<T> create(View rootView, View.OnClickListener onClickListener);
    }
}
