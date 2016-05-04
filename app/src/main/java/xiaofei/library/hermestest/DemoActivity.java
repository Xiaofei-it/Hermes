package xiaofei.library.hermestest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.service.HermesService;


public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Hermes.connect(getApplicationContext(), HermesService.HermesService0.class);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
        findViewById(R.id.get_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IUserManager userManager = Hermes.getInstance(IUserManager.class);
                Toast.makeText(getApplicationContext(), userManager.getUser(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.get_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IFileUtils fileUtils = Hermes.getUtilityClass(IFileUtils.class);
                Toast.makeText(getApplicationContext(), fileUtils.getExternalCacheDir(DemoActivity.this), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
