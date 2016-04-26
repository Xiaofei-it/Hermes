package xiaofei.library.hermestest.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermestest.R;


public class TestingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Hermes.init(getApplicationContext());

        findViewById(R.id.getInt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INewInstance iNewInstance = Hermes.newInstance(INewInstance.class);
                Toast.makeText(getApplicationContext(), "" + iNewInstance.getInt(new A(), 5), Toast.LENGTH_SHORT).show();
                iNewInstance = Hermes.newInstance(INewInstance.class, 6);
                Toast.makeText(getApplicationContext(), "" + iNewInstance.getInt(new A(), 5), Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.getDouble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INewInstance iNewInstance = Hermes.getUtilityClass(INewInstance.class);
                Toast.makeText(getApplicationContext(), "" + iNewInstance.getDouble(new A(), 5), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "" + iNewInstance.getDouble(null, 5), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.getInstance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IGetInstance iGetInstance = Hermes.getInstance(IGetInstance.class);
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(new A()), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(null), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "" + iGetInstance.getInt(new A(), new Call() {
                    @Override
                    public int g(A a) {
                        A a2 = new A();
                        a2.i = a.i + 1;
                        return a2.i;
                    }
                }, new Call() {
                    @Override
                    public int g(A a) {
                        A a2 = new A();
                        a2.i = a.i + 2;
                        return a2.i;
                    }
                }), Toast.LENGTH_SHORT).show();

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
    }

    /**
     * app
     *
     * json null
     *
     * parcel null
     */
}
