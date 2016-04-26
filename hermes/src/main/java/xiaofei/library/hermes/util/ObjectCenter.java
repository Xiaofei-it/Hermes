package xiaofei.library.hermes.util;

import java.util.HashMap;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class ObjectCenter {

    private static ObjectCenter sInstance = null;

    private HashMap<Long, Object> mObjects;

    private ObjectCenter() {
        mObjects = new HashMap<Long, Object>();
    }

    public static synchronized ObjectCenter getInstance() {
        if (sInstance == null) {
            sInstance = new ObjectCenter();
        }
        return sInstance;
    }

    public Object getObject(Long timeStamp) {
        return mObjects.get(timeStamp);
    }

    public void putObject(long timeStamp, Object object) {
        mObjects.put(timeStamp, object);
    }
}
