package xiaofei.library.hermes.sender;

import xiaofei.library.hermes.util.ErrorCodes;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.wrapper.ObjectWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class SenderDesignator {

    public static final int TYPE_NEW_INSTANCE = 0;

    public static final int TYPE_GET_INSTANCE = 1;

    public static final int TYPE_GET_UTILITY_CLASS = 2;

    public static final int TYPE_INVOKE_METHOD = 3;

    public static Sender getPostOffice(int type, ObjectWrapper object) {
        switch (type) {
            case TYPE_NEW_INSTANCE:
                return new InstanceCreatingSender(object);
            case TYPE_GET_INSTANCE:
                return new InstanceGettingSender(object);
            case TYPE_GET_UTILITY_CLASS:
                return new UtilityGettingSender(object);
            case TYPE_INVOKE_METHOD:
                return new ObjectSender(object);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

}
