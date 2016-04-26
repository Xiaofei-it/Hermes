package xiaofei.library.hermes.receiver;

import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeUtils;
import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class UtilityGettingReceiver extends Receiver {

    public UtilityGettingReceiver(ObjectWrapper objectWrapper) throws HermesException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
    }

    @Override
    protected void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) {

    }

    @Override
    protected Object invokeMethod() {
        return null;
    }

}
