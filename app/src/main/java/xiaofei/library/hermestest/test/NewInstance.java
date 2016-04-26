package xiaofei.library.hermestest.test;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/13.
 */
@ClassId("NewInstance")
public class NewInstance {
    public NewInstance() {

    }
    int p;
    public NewInstance(Integer p) {
        this.p = p;
    }
    public Integer getInt(B i, Integer j) {
        return i.i + j + p;
    }

    @MethodId("getDouble")
    public static Double getDouble(B i, int j) {
        if (i == null) {
            return 0.9;
        } else {
            return i.i + j + 0.1;
        }
    }
}
