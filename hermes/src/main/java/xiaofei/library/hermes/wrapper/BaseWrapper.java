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

/**
 * Created by Xiaofei on 16/4/7.
 */
public class BaseWrapper {

    private boolean mIsName;

    private String mName;

    protected void setName(boolean isName, String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        mIsName = isName;
        mName = name;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mIsName ? 1 : 0);
        parcel.writeString(mName);
    }

    public void readFromParcel(Parcel in) {
        mIsName = in.readInt() == 1;
        mName = in.readString();
    }

    public boolean isName() {
        return mIsName;
    }

    public String getName() {
        return mName;
    }
}
