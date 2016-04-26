package xiaofei.library.hermes.sender;

import java.lang.reflect.Method;

import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class UtilityGettingSender extends Sender {

    public UtilityGettingSender(ObjectWrapper object) {
        super(object);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) {
        return null;
    }
}
