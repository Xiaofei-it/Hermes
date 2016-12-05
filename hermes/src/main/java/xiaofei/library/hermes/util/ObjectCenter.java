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

package xiaofei.library.hermes.util;

import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class ObjectCenter {

    private static final String TAG = "ObjectCenter";

    private static volatile ObjectCenter sInstance = null;

    private final ConcurrentHashMap<Long, Object> mObjects;

    private ObjectCenter() {
        mObjects = new ConcurrentHashMap<Long, Object>();
    }

    public static ObjectCenter getInstance() {
        if (sInstance == null) {
            synchronized (ObjectCenter.class) {
                if (sInstance == null) {
                    sInstance = new ObjectCenter();
                }
            }
        }
        return sInstance;
    }

    public Object getObject(Long timeStamp) {
        return mObjects.get(timeStamp);
    }

    public void putObject(long timeStamp, Object object) {
        mObjects.put(timeStamp, object);
    }

    public void deleteObjects(List<Long> timeStamps) {
        for (Long timeStamp : timeStamps) {
            if (mObjects.remove(timeStamp) == null) {
                Log.e(TAG, "An error occurs in the GC.");
            }
        }
    }
}
