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

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;

import xiaofei.library.hermes.internal.Channel;

/**
 * Created by Xiaofei on 16/4/29.
 */
public class HermesGc {

    private static HermesGc sInstance = null;

    private ReferenceQueue<Object> mReferenceQueue;

    private static final Channel CHANNEL = Channel.getInstance();

    private HashMap<PhantomReference<Object>, Long> mMap;

    private HermesGc() {
        mReferenceQueue = new ReferenceQueue<Object>();
        mMap = new HashMap<PhantomReference<Object>, Long>();
    }

    public static synchronized HermesGc getInstance() {
        if (sInstance == null) {
            sInstance = new HermesGc();
        }
        return sInstance;
    }

    private void gc() {
        synchronized (mReferenceQueue) {
            Reference<Object> reference;
            Long timeStamp;
            ArrayList<Long> timeStamps = new ArrayList<Long>();
            //TODO Is the following class casting right?
            while ((reference = (Reference<Object>) mReferenceQueue.poll()) != null) {
                //TODO How about ConcurrentHashMap?
                synchronized (mMap) {
                    timeStamp = mMap.remove(reference);
                }
                if (timeStamp != null) {
                    timeStamps.add(timeStamp);
                }
            }
            if (!timeStamps.isEmpty()) {
                CHANNEL.gc(timeStamps);
            }
        }
    }

    public void register(Object object, Long timeStamp) {
        gc();
        synchronized (mMap) {
            mMap.put(new PhantomReference<Object>(object, mReferenceQueue), timeStamp);
        }
    }
}
