package xiaofei.library.hermestest.test;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.GetInstance;

/**
 * Created by Xiaofei on 16/4/13.
 */
@ClassId("GetInstance")
public class C {
    int i;
    private C() {

    }

    @GetInstance
    public static C getInstan() {
        C c = new C();
        c.i = 9;
        return c;
    }

    public static C getInstance(int i) {
        C c = new C();
        c.i = i;
        return c;
    }

    public Integer getInt(A a) {
        if (a == null) {
            return 90;
        } else {
            return a.i + i;
        }
    }

    public int getInt(A a, Call c1, Call c2) {
        return c1.g(a) * 10 + c2.g(a);
    }

    public A getInt2(A a, Call c1, Call c2) {
        A r = new A();
        r.i = c1.g(a) * 10;
        return r;
    }

    public A getInt3(A a, Call2 c) {
        A r = new A();
        if (a == null) {
            r = c.g(a);
            return r;
        }
        if (c.g(a) == null) {
            r.i = 1000;
            return r;
        }
        return null;
    }
}
