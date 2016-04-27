/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xiaofei.library.hermes.internal;

import android.os.Parcel;
import android.os.Parcelable;

import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/7.
 */
public class CallbackMail implements Parcelable {

    private long mTimeStamp;

    private int mIndex;

    private MethodWrapper mMethod;

    private ParameterWrapper[] mParameters;

    public static final Creator<CallbackMail> CREATOR
            = new Creator<CallbackMail>() {
        public CallbackMail createFromParcel(Parcel in) {
            CallbackMail mail = new CallbackMail();
            mail.readFromParcel(in);
            return mail;
        }
        public CallbackMail[] newArray(int size) {
            return new CallbackMail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mTimeStamp);
        parcel.writeInt(mIndex);
        parcel.writeParcelable(mMethod, flags);
        parcel.writeParcelableArray(mParameters, flags);
    }

    public void readFromParcel(Parcel in) {
        mTimeStamp = in.readLong();
        mIndex = in.readInt();
        ClassLoader classLoader = CallbackMail.class.getClassLoader();
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

    private CallbackMail() {

    }

    public CallbackMail(long timeStamp, int index, MethodWrapper method, ParameterWrapper[] parameters) {
        mTimeStamp = timeStamp;
        mIndex = index;
        mMethod = method;
        mParameters = parameters;
    }

    public ParameterWrapper[] getParameters() {
        return mParameters;
    }

    public int getIndex() {
        return mIndex;
    }

    public MethodWrapper getMethod() {
        return mMethod;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

}
