package xiaofei.library.hermestest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermestest.test.C;
import xiaofei.library.hermestest.test.NewInstance;

public class AnotherProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_process);
        Hermes.init(this);
        Hermes.register(NewInstance.class);
        Hermes.register(C.class);
        Hermes.register(UserManager.class);
        Hermes.register(LoadingTask.class);
        Hermes.register(FileUtils.class);
    }
}
