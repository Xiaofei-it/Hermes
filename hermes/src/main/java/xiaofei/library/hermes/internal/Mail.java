package xiaofei.library.hermes.internal;

import android.os.Parcel;
import android.os.Parcelable;

import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/7.
 */
public class Mail implements Parcelable {

    private long mTimeStamp;

    private ObjectWrapper mObject;

    private MethodWrapper mMethod;

    private ParameterWrapper[] mParameters;

    public static final Parcelable.Creator<Mail> CREATOR
            = new Parcelable.Creator<Mail>() {
        public Mail createFromParcel(Parcel in) {
            Mail mail = new Mail();
            mail.readFromParcel(in);
            return mail;
        }
        public Mail[] newArray(int size) {
            return new Mail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mTimeStamp);
        parcel.writeParcelable(mObject, flags);
        parcel.writeParcelable(mMethod, flags);
        parcel.writeParcelableArray(mParameters, flags);
    }

    public void readFromParcel(Parcel in) {
        mTimeStamp = in.readLong();
        ClassLoader classLoader = Mail.class.getClassLoader();
        mObject = in.readParcelable(classLoader);
        mMethod = in.readParcelable(classLoader);
        Parcelable[] parcelables = in.readParcelableArray(classLoader);
        if (parcelables == null) {
            mParameters = null;
        } else {
            int length = parcelables.length;
            mParameters = new ParameterWrapper[length];
            for (int i = 0; i < length; ++i) {
                mParameters[i] = (ParameterWrapper) parcelables[i];
            }
        }

    }

    private Mail() {

    }

    public Mail(long timeStamp, ObjectWrapper object, MethodWrapper method, ParameterWrapper[] parameters) {
        mTimeStamp = timeStamp;
        mObject = object;
        mMethod = method;
        mParameters = parameters;
    }

    public ParameterWrapper[] getParameters() {
        return mParameters;
    }

    public ObjectWrapper getObject() {
        return mObject;
    }

    public MethodWrapper getMethod() {
        return mMethod;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

}
