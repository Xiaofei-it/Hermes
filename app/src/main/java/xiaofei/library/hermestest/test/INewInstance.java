package xiaofei.library.hermestest.test;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/13.
 */
@ClassId("NewInstance")
public interface INewInstance {
    int getInt(B i, int j);
    @MethodId("getDouble")
    Double getDouble(B i, int j);
}
