package xiaofei.library.hermes.sender;

import java.lang.reflect.Method;

import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class InstanceCreatingSender extends Sender {

    private Class<?>[] mConstructorParameterTypes;

    public InstanceCreatingSender(ObjectWrapper object) {
        super(object);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) {
        int length = parameterWrappers == null ? 0 : parameterWrappers.length;
        mConstructorParameterTypes = new Class<?>[length];
        for (int i = 0; i < length; ++i) {
            try {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                mConstructorParameterTypes[i] = parameterWrapper == null ? null : parameterWrapper.getClassType();
            } catch (Exception e) {

            }
        }
        return new MethodWrapper(mConstructorParameterTypes);
    }


}
