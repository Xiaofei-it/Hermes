package xiaofei.library.hermes.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;

import xiaofei.library.hermes.internal.Channel;

/**
 * Created by Eric on 16/4/29.
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
            while ((reference = (Reference<Object>) mReferenceQueue.poll()) != null) {
                timeStamp = mMap.remove(reference);
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
