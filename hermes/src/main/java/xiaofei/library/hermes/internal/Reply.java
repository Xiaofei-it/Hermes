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

import xiaofei.library.hermes.util.CodeUtils;
import xiaofei.library.hermes.util.ErrorCodes;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeCenter;
import xiaofei.library.hermes.wrapper.ParameterWrapper;
import xiaofei.library.hermes.wrapper.TypeWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class Reply implements Parcelable {

    private final static TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private int mErrorCode;

    private String mErrorMessage;

    private TypeWrapper mClass;

    private Object mResult;

    public static final Parcelable.Creator<Reply> CREATOR
            = new Parcelable.Creator<Reply>() {
        public Reply createFromParcel(Parcel in) {
            Reply reply = new Reply();
            reply.readFromParcel(in);
            return reply;
        }

        public Reply[] newArray(int size) {
            return new Reply[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mErrorCode);
        parcel.writeString(mErrorMessage);
        parcel.writeParcelable(mClass, flags);
        try {
            parcel.writeString(CodeUtils.encode(mResult));
        } catch (HermesException e) {
            e.printStackTrace();
        }
    }


    public void readFromParcel(Parcel in) {
        mErrorCode = in.readInt();
        ClassLoader classLoader = Reply.class.getClassLoader();
        mErrorMessage = in.readString();
        mClass = in.readParcelable(classLoader);
        try {
            Class<?> clazz = TYPE_CENTER.getClassType(mClass);
            mResult = CodeUtils.decode(in.readString(), clazz);
        } catch (Exception e) {

        }
    }

    private Reply() {

    }

    public Reply(ParameterWrapper parameterWrapper) {
        try {
            Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);
            mResult = CodeUtils.decode(parameterWrapper.getData(), clazz);
            mErrorCode = ErrorCodes.SUCCESS;
            mErrorMessage = null;
            mClass = new TypeWrapper(clazz);
        } catch (HermesException e) {
            e.printStackTrace();
            mErrorCode = e.getErrorCode();
            mErrorMessage = e.getMessage();
            mResult = null;
            mClass = null;
        }
    }

    public Reply(int errorCode, String message) {
        mErrorCode = errorCode;
        mErrorMessage = message;
        mResult = null;
        mClass = null;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public boolean success() {
        return mErrorCode == ErrorCodes.SUCCESS;
    }

    public String getMessage() {
        return mErrorMessage;
    }

    public Object getResult() {
        return mResult;
    }
}
