package xiaofei.library.hermestest;

import android.content.Context;

import java.io.IOException;

import xiaofei.library.hermes.annotation.ClassId;

/**
 * Created by Xiaofei on 16/4/28.
 */
@ClassId("FileUtils")
public class FileUtils {

    public static String getExternalCacheDir(Context context) {
        try {
            return context.getExternalCacheDir().getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }
}
