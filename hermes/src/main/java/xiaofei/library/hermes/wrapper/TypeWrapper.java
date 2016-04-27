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
import xiaofei.library.hermes.util.TypeUtils;

/**
 * Created by Xiaofei on 16/4/9.
 */
public class TypeWrapper extends BaseWrapper implements Parcelable {

    public static final Parcelable.Creator<TypeWrapper> CREATOR
            = new Parcelable.Creator<TypeWrapper>() {
        public TypeWrapper createFromParcel(Parcel in) {
            TypeWrapper typeWrapper = new TypeWrapper();
            typeWrapper.readFromParcel(in);
            return typeWrapper;
        }
        public TypeWrapper[] newArray(int size) {
            return new TypeWrapper[size];
        }
    };

    private TypeWrapper() {

    }

    public TypeWrapper(Class<?> clazz) {
        setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
    }

}
