package xiaofei.library.hermestest.test;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/13.
 */
@ClassId("NewInstance")
public interface INewInstance {
    Integer getInt(B i, Integer j);
    @MethodId("getDouble")
    Double getDouble(B i, int j);
}
