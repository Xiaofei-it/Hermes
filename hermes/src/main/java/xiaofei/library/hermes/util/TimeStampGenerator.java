package xiaofei.library.hermes.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Xiaofei on 16/4/7.
 */
public class TimeStampGenerator {
    private static AtomicLong sTimeStamp = new AtomicLong();

    public static long getTimeStamp() {
        return sTimeStamp.incrementAndGet();
    }
}
