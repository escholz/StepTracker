package escholz.steptracker.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;

/**
 * Model object that contains display and filter information for aggregate step data
 */
public class StepAggregate implements Parcelable {

    @IdRes
    public final int viewResourceId;
    @StringRes
    public final int labelResourceId;
    @IntegerRes
    public final int durationInMsResourceId;
    @DrawableRes
    public final int iconResourceId;

    public StepAggregate(@IdRes int viewResourceId, @StringRes int labelResourceId,
                         @DrawableRes int iconResourceId, @IntegerRes int durationInMsResourceId) {
        this.viewResourceId = viewResourceId;
        this.labelResourceId = labelResourceId;
        this.durationInMsResourceId = durationInMsResourceId;
        this.iconResourceId = iconResourceId;
    }

    private StepAggregate(Parcel parcel) {
        viewResourceId = parcel.readInt();
        labelResourceId = parcel.readInt();
        durationInMsResourceId = parcel.readInt();
        iconResourceId = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(viewResourceId);
        parcel.writeInt(labelResourceId);
        parcel.writeInt(durationInMsResourceId);
        parcel.writeInt(iconResourceId);
    }

    public static final Parcelable.Creator<StepAggregate> CREATOR
            = new Parcelable.Creator<StepAggregate>() {

        @Override
        public StepAggregate createFromParcel(Parcel parcel) {
            return new StepAggregate(parcel);
        }

        @Override
        public StepAggregate[] newArray(int i) {
            return new StepAggregate[0];
        }
    };
}
