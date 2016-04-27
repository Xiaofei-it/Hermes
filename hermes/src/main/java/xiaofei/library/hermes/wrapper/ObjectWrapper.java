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

package xiaofei.library.hermes.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.util.TimeStampGenerator;
import xiaofei.library.hermes.util.TypeUtils;

/**
 * Created by Xiaofei on 16/4/7.
 */
public class ObjectWrapper extends BaseWrapper implements Parcelable {

    public static final int TYPE_OBJECT_TO_NEW = 0;

    public static final int TYPE_OBJECT_TO_GET = 1;

    public static final int TYPE_OBJECT = 3;

    public static final int TYPE_CLASS = 4;

    public static final int TYPE_CLASS_TO_GET = 5;

    private long mTimeStamp;

    //only used here
    private Class<?> mClass;

    private int mType;

    public static final Parcelable.Creator<ObjectWrapper> CREATOR
            = new Parcelable.Creator<ObjectWrapper>() {
        public ObjectWrapper createFromParcel(Parcel in) {
            ObjectWrapper objectWrapper = new ObjectWrapper();
            objectWrapper.readFromParcel(in);
            return objectWrapper;
        }
        public ObjectWrapper[] newArray(int size) {
            return new ObjectWrapper[size];
        }
    };

    private ObjectWrapper() {}

    public ObjectWrapper(Class<?> clazz, int type) {
        setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
        mClass = clazz;
        mTimeStamp = TimeStampGenerator.getTimeStamp();
        mType = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeLong(mTimeStamp);
        parcel.writeInt(mType);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mTimeStamp = in.readLong();
        mType = in.readInt();
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public Class<?> getObjectClass() {
        return mClass;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }
}
