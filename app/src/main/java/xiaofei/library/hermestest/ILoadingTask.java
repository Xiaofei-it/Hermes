package xiaofei.library.hermestest;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/25.
 */
@ClassId("LoadingTask")
public interface ILoadingTask {

    @MethodId("start")
    void start(LoadingCallback loadingCallback);
}
