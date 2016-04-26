package xiaofei.library.hermes.util;

import android.support.v4.util.Pair;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Xiaofei on 16/4/14.
 */
public class CallbackManager {

    private static CallbackManager sInstance = null;

    private HashMap<Long, CallbackWrapper> mCallbackWrappers;

    private CallbackManager() {
        mCallbackWrappers = new HashMap<Long, CallbackWrapper>();
    }

    public static synchronized CallbackManager getInstance() {
        if (sInstance == null) {
            sInstance = new CallbackManager();
        }
        return sInstance;
    }

    private static long getKey(long timeStamp, int index) {
        return timeStamp * 10 + index;
    }

    public void addCallback(long timeStamp, int index, Object callback, boolean isWeakRef, boolean uiThread) {
        synchronized (mCallbackWrappers) {
            Log.v("eric zhao", "put timeStamp = " + timeStamp + " index = " + index);
            long key = getKey(timeStamp, index);
            mCallbackWrappers.put(key, new CallbackWrapper(isWeakRef, callback, uiThread));
        }
    }

    public Pair<Boolean, Object> getCallback(long timeStamp, int index) {
        synchronized (mCallbackWrappers) {
            long key = getKey(timeStamp, index);
            CallbackWrapper callbackWrapper = mCallbackWrappers.get(key);
            if (callbackWrapper == null) {
                return null;
            }
            Pair<Boolean, Object> pair = callbackWrapper.get();
            if (pair.second == null) {
                mCallbackWrappers.remove(key);
            }
            return pair;
        }
    }

    private static class CallbackWrapper {

        private Object mCallback;

        private boolean mUiThread;

        CallbackWrapper(boolean isWeakRef, Object callback, boolean uiThread) {
            if (isWeakRef) {
                mCallback = new WeakReference<Object>(callback);
            } else {
                mCallback = callback;
            }
            mUiThread = uiThread;
        }

        public Pair<Boolean, Object> get() {
            Object callback;
            if (mCallback instanceof WeakReference) {
                callback = ((WeakReference<Object>) mCallback).get();
            } else {
                callback = mCallback;
            }
            return new Pair<Boolean, Object>(mUiThread, callback);
        }
    }
}
