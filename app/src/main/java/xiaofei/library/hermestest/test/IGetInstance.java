package xiaofei.library.hermestest.test;

import xiaofei.library.hermes.annotation.Background;
import xiaofei.library.hermes.annotation.ClassId;

/**
 * Created by Xiaofei on 16/4/13.
 */
@ClassId("GetInstance")
public interface IGetInstance {

    Integer getInt(A a);

    Integer getInt(A a, @Background Call c1, @Background Call c2);

    A getInt2(A a, @Background Call c1, Call c2);

    A getInt3(A a, @Background Call2 c);
}
