package xiaofei.library.hermestest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.HermesService;


public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Hermes.connect(getApplicationContext(), HermesService.HermesService0.class);
        Hermes.connect(getApplicationContext(), HermesService.HermesService1.class);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        /**
         * If access remote object here, it is useless.
         *
         * We can bind service in non-ui thread, but what to do next? It will cause the dead lock.
         *
         * If sleep here, it is useless.
         *
         * What if use invocation handler in non-ui thread?
         */

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILoadingTask loadingTask = Hermes.newInstance(ILoadingTask.class, "pic.png");
                loadingTask.start(new LoadingCallback() {
                    @Override
                    public void callback(int progress) {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });

        findViewById(R.id.download2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILoadingTask loadingTask = Hermes.newInstanceInService(HermesService.HermesService1.class, ILoadingTask.class, "pic.png");
                loadingTask.start(new LoadingCallback() {
                    @Override
                    public void callback(int progress) {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        findViewById(R.id.get_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IUserManager userManager = Hermes.getInstance(IUserManager.class);
                Toast.makeText(getApplicationContext(), userManager.getUser(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.get_user2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IUserManager userManager = Hermes.getInstanceInService(HermesService.HermesService1.class, IUserManager.class);
                Toast.makeText(getApplicationContext(), userManager.getUser(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.get_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IFileUtils fileUtils = Hermes.getUtilityClass(IFileUtils.class);
                Toast.makeText(getApplicationContext(), fileUtils.getExternalCacheDir(DemoActivity.this), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.get_file2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IFileUtils fileUtils = Hermes.getUtilityClassInService(HermesService.HermesService1.class, IFileUtils.class);
                Toast.makeText(getApplicationContext(), fileUtils.getExternalCacheDir(DemoActivity.this), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hermes.disconnect(getApplicationContext());
        Hermes.disconnect(this, HermesService.HermesService1.class);
    }
}
