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
import xiaofei.library.hermes.util.CodeUtils;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeUtils;

/**
 * Created by Xiaofei on 16/4/7.
 */
public class ParameterWrapper extends BaseWrapper implements Parcelable {

    private String mData;

    //only used here.
    private Class<?> mClass;

    public static final Parcelable.Creator<ParameterWrapper> CREATOR
            = new Parcelable.Creator<ParameterWrapper>() {
        public ParameterWrapper createFromParcel(Parcel in) {
            ParameterWrapper parameterWrapper = new ParameterWrapper();
            parameterWrapper.readFromParcel(in);
            return parameterWrapper;
        }
        public ParameterWrapper[] newArray(int size) {
            return new ParameterWrapper[size];
        }
    };

    private ParameterWrapper() {

    }

    public ParameterWrapper(Class<?> clazz, Object object) throws HermesException {
        mClass = clazz;
        setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
        mData = CodeUtils.encode(object);
    }

    public ParameterWrapper(Object object) throws HermesException{
        if (object == null) {
            setName(false, "");
            mData = null;
            mClass = null;
        } else {
            Class<?> clazz = object.getClass();
            mClass = clazz;
            setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
            mData = CodeUtils.encode(object);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(mData);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mData = in.readString();
    }

    public String getData() {
        return mData;
    }

    public boolean isNull() {
        return mData == null;
    }

    public Class<?> getClassType() {
        return mClass;
    }
}
