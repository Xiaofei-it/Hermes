package xiaofei.library.hermestest;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by Xiaofei on 16/4/25.
 */

@ClassId("UserManager")
public class UserManager implements IUserManager {

    private static UserManager sInstance = null;

    private UserManager() {

    }

    public static synchronized UserManager getInstance() {
        if (sInstance == null) {
            sInstance = new UserManager();
        }
        return sInstance;
    }

    @MethodId("getUser")
    @Override
    public String getUser() {
        return "Xiaofei";
    }
}
