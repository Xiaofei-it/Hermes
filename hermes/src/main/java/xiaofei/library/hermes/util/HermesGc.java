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
import java.util.Map;
import java.util.Set;

import xiaofei.library.hermes.internal.Channel;
import xiaofei.library.hermes.service.HermesService;

/**
 * Created by Xiaofei on 16/4/29.
 */
public class HermesGc {

    private static HermesGc sInstance = null;

    private ReferenceQueue<Object> mReferenceQueue;

    private static final Channel CHANNEL = Channel.getInstance();

    private HashMap<PhantomReference<Object>, Long> mTimeStamps;

    private HashMap<Long, Class<? extends HermesService>> mServices;

    private HermesGc() {
        mReferenceQueue = new ReferenceQueue<Object>();
        mTimeStamps = new HashMap<PhantomReference<Object>, Long>();
        mServices = new HashMap<Long, Class<? extends HermesService>>();
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
            HashMap<Class<? extends HermesService>, ArrayList<Long>> timeStamps
                    = new HashMap<Class<? extends HermesService>, ArrayList<Long>>();
            //TODO Is the following class casting right?
            while ((reference = (Reference<Object>) mReferenceQueue.poll()) != null) {
                //TODO How about ConcurrentHashMap?
                synchronized (mTimeStamps) {
                    timeStamp = mTimeStamps.remove(reference);
                }
                if (timeStamp != null) {
                    Class<? extends HermesService> clazz = mServices.get(timeStamp);
                    if (clazz != null) {
                        ArrayList<Long> tmp = timeStamps.get(timeStamp);
                        if (tmp == null) {
                            tmp = new ArrayList<Long>();
                            timeStamps.put(clazz, tmp);
                        }
                        tmp.add(timeStamp);
                    }
                }
            }
            Set<Map.Entry<Class<? extends HermesService>, ArrayList<Long>>> set = timeStamps.entrySet();
            for (Map.Entry<Class<? extends HermesService>, ArrayList<Long>> entry : set) {
                ArrayList<Long> values = entry.getValue();
                if (!values.isEmpty()) {
                    CHANNEL.gc(entry.getKey(), values);
                }
            }
        }
    }

    public void register(Class<? extends HermesService> service, Object object, Long timeStamp) {
        gc();
        synchronized (mTimeStamps) {
            mTimeStamps.put(new PhantomReference<Object>(object, mReferenceQueue), timeStamp);
        }
        synchronized (mServices) {
            mServices.put(timeStamp, service);
        }
    }
}
