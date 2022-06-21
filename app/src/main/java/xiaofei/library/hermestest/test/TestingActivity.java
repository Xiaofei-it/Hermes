package xiaofei.library.hermestest.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import timber.log.Timber;
import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.HermesService;
import xiaofei.library.hermestest.R;


public class TestingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Hermes.connect(getApplicationContext(), HermesService.HermesService0.class);

        findViewById(R.id.getInt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INewInstance iNewInstance = Hermes.newInstance(INewInstance.class);

                int i = iNewInstance.getInt(new A(), 5);
                Timber.i("第1个应为5，实际结果: %d", i);
                Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();

                iNewInstance = Hermes.newInstance(INewInstance.class, 6);

                i = iNewInstance.getInt(new A(), 5);
                Timber.i("第2个应为11，实际结果: %d", i);
                Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();

                A aIns = new A();
                aIns.i = 1;
                i = iNewInstance.getInt(aIns, 2);
                Timber.i("第3个应为9，实际结果: %d", i);
                Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.getDouble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INewInstance iNewInstance = Hermes.getUtilityClass(INewInstance.class);
                // 得数是5.1
                Toast.makeText(getApplicationContext(), "" + iNewInstance.getDouble(new A(), 5), Toast.LENGTH_SHORT).show();
                // 得数是0.9
                Toast.makeText(getApplicationContext(), "" + iNewInstance.getDouble(null, 5), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.getInstance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IGetInstance iGetInstance = Hermes.getInstance(IGetInstance.class);
                // 得数是9
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(new A()), Toast.LENGTH_SHORT).show();
                // 得数是90
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(null), Toast.LENGTH_SHORT).show();
                // 得数是109
                iGetInstance = Hermes.getInstance(IGetInstance.class, 100);
                A a = new A();
                a.i = 9;
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(a), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.callback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IGetInstance iGetInstance = Hermes.getInstance(IGetInstance.class);
                Timber.i("显示12");
                // 显示12
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(new A(), new Call() {
                    @Override
                    public int g(A a) {
                        A a2 = new A();
                        a2.i = a.i + 1;
                        Timber.i("a2.i=1，实际：%d", a2.i);
                        return a2.i;
                    }
                }, new Call() {
                    @Override
                    public int g(A a) {
                        A a2 = new A();
                        a2.i = a.i + 2;
                        Timber.i("a2.i=2，实际：%d", a2.i);
                        return a2.i;
                    }
                }), Toast.LENGTH_SHORT).show();

                Timber.i("显示50");
                // 显示50
                A a = new A();
                a.i = 2;
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt2(a, new Call() {
                    @Override
                    public int g(A a) {
                        A a2 = new A();
                        a2.i = a.i + 3;
                        return a2.i;
                    }
                }, null).i, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.null_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IGetInstance iGetInstance = Hermes.getInstance(IGetInstance.class);
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt3(null, new Call2() {
                    @Override
                    public A g(A a) {
                        if (a == null) {
                            A a2 = new A();
                            a2.i = 333;
                            return a2;
                        } else {
                            return null;
                        }

                    }
                }).i, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt3(new A(), new Call2() {
                    @Override
                    public A g(A a) {
                        if (a == null) {
                            A a2 = new A();
                            a2.i = 333;
                            return a2;
                        } else {
                            return null;
                        }

                    }
                }).i, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt3(new A(), new Call2() {
                    @Override
                    public A g(A a) {
                        return new A();
                    }
                }), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.test_gc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 1000; ++i) {
                    INewInstance iNewInstance = Hermes.newInstance(INewInstance.class);
                }

            }
        });
        findViewById(R.id.test_callback_gc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IGetInstance iGetInstance = Hermes.getInstance(IGetInstance.class);
                Call2 call = new Call2() {
                    @Override
                    public A g(A a) {
                        return a;
                    }
                };
                for (int i = 0; i < 1000; ++i) {
                    iGetInstance.getInt3(new A(), call);
                }

            }
        });
    }

    /**
     * app
     *
     * json null
     *
     * parcel null
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hermes.disconnect(this);
    }
}
