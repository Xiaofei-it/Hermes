package xiaofei.library.hermestest;

import android.content.Context;

import xiaofei.library.hermes.annotation.ClassId;

/**
 * Created by Xiaofei on 16/4/28.
 */
@ClassId("FileUtils")
public interface IFileUtils {

    String getExternalCacheDir(Context context);
}
