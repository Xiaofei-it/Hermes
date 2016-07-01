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

import android.os.RemoteException;
import android.support.v4.util.Pair;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import xiaofei.library.hermes.internal.IHermesServiceCallback;

/**
 * Created by Xiaofei on 16/7/1.
 *
 * This works in the main process.
 */
public class HermesCallbackGc {

    private static volatile HermesCallbackGc sInstance = null;

    private final ReferenceQueue<Object> mReferenceQueue;

    private final ConcurrentHashMap<PhantomReference<Object>, Triple<IHermesServiceCallback, Long, Integer>> mTimeStamps;

    private HermesCallbackGc() {
        mReferenceQueue = new ReferenceQueue<Object>();
        mTimeStamps = new ConcurrentHashMap<PhantomReference<Object>, Triple<IHermesServiceCallback, Long, Integer>>();
    }

    public static HermesCallbackGc getInstance() {
        if (sInstance == null) {
            synchronized (HermesCallbackGc.class) {
                if (sInstance == null) {
                    sInstance = new HermesCallbackGc();
                }
            }
        }
        return sInstance;
    }

    private void gc() {
        synchronized (mReferenceQueue) {
            PhantomReference<Object> reference;
            Triple<IHermesServiceCallback, Long, Integer> triple;
            HashMap<IHermesServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>> timeStamps
                    = new HashMap<IHermesServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>>();
            while ((reference = (PhantomReference<Object>) mReferenceQueue.poll()) != null) {
                triple = mTimeStamps.remove(reference);
                if (triple != null) {
                    Pair<ArrayList<Long>, ArrayList<Integer>> tmp = timeStamps.get(triple.first);
                    if (tmp == null) {
                        tmp = new Pair<ArrayList<Long>, ArrayList<Integer>>(new ArrayList<Long>(), new ArrayList<Integer>());
                        timeStamps.put(triple.first, tmp);
                    }
                    tmp.first.add(triple.second);
                    tmp.second.add(triple.third);
                }
            }
            Set<Map.Entry<IHermesServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>>> set = timeStamps.entrySet();
            for (Map.Entry<IHermesServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>> entry : set) {
                Pair<ArrayList<Long>, ArrayList<Integer>> values = entry.getValue();
                if (!values.first.isEmpty()) {
                    try {
                        entry.getKey().gc(values.first, values.second);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void register(IHermesServiceCallback callback, Object object, long timeStamp, int index) {
        gc();
        mTimeStamps.put(new PhantomReference<Object>(object, mReferenceQueue), Triple.create(callback, timeStamp, index));
    }
}
