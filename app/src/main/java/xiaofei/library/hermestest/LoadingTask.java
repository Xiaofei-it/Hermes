package xiaofei.library.hermestest;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/25.
 */
@ClassId("LoadingTask")
public class LoadingTask implements ILoadingTask {

    public LoadingTask(String url) {

    }

    @MethodId("start")
    @Override
    public void start(final LoadingCallback loadingCallback) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int time = 0;
            @Override
            public void run() {
                time += 50;
                if (time > 100) {
                    time = 100;
                }
                loadingCallback.callback(time);
                if (time == 100) {
                    timer.cancel();
                }
            }
        }, 0, 100);
    }
}
