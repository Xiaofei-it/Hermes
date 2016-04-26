package xiaofei.library.hermestest;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/25.
 */

@ClassId("UserManager")
public interface IUserManager {

    @MethodId("getUser")
    String getUser();

}
