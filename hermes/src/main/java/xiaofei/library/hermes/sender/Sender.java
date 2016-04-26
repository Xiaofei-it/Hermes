package xiaofei.library.hermes.sender;

import java.io.ObjectStreamException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import xiaofei.library.hermes.annotation.Background;
import xiaofei.library.hermes.annotation.WeakRef;
import xiaofei.library.hermes.internal.Channel;
import xiaofei.library.hermes.internal.Mail;
import xiaofei.library.hermes.internal.Reply;
import xiaofei.library.hermes.util.CallbackManager;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TimeStampGenerator;
import xiaofei.library.hermes.util.TypeCenter;
import xiaofei.library.hermes.util.TypeUtils;
import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/7.
 */
public abstract class Sender {

    protected static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private static final Channel CHANNEL = Channel.getInstance();

    private static final CallbackManager CALLBACK_MANAGER = CallbackManager.getInstance();

    private long mTimeStamp;

    private ObjectWrapper mObject;

    private MethodWrapper mMethod;

    private ParameterWrapper[] mParameters;

    public Sender(ObjectWrapper object) {
        mObject = object;
    }

    protected abstract MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) throws HermesException;

    protected void setParameterWrappers(ParameterWrapper[] parameterWrappers) {
        mParameters = parameterWrappers;
    }

    private void registerClass(Method method) throws HermesException {
        if (method == null) {
            return;
        }
        Class<?>[] classes = method.getParameterTypes();
        for (Class<?> clazz : classes) {
            TYPE_CENTER.register(clazz);
        }
        TYPE_CENTER.register(method.getReturnType());
    }

    private final ParameterWrapper[] getParameterWrappers(Method method, Object[] parameters) throws HermesException {
        int length = parameters.length;
        ParameterWrapper[] parameterWrappers = new ParameterWrapper[length];
        if (method != null) {
            Class<?>[] classes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < length; ++i) {
                if (classes[i].isInterface()) {
                    Object parameter = parameters[i];
                    if (parameter != null) {
                        parameterWrappers[i] = new ParameterWrapper(classes[i], null);
                    } else {
                        parameterWrappers[i] = new ParameterWrapper(null);
                    }
                    if (parameterAnnotations[i] != null && parameter != null) {
                        CALLBACK_MANAGER.addCallback(
                                mTimeStamp, i, parameter,
                                TypeUtils.arrayContainsAnnotation(parameterAnnotations[i], WeakRef.class),
                                !TypeUtils.arrayContainsAnnotation(parameterAnnotations[i], Background.class));
                    }
                } else {
                    parameterWrappers[i] = new ParameterWrapper(parameters[i]);
                }
            }
        } else {
            for (int i = 0; i < length; ++i) {
                parameterWrappers[i] = new ParameterWrapper(parameters[i]);
            }
        }
        return parameterWrappers;
    }


    public final Reply send(Method method, Object[] parameters) throws HermesException {
        mTimeStamp = TimeStampGenerator.getTimeStamp();
        if (parameters == null) {
            parameters = new Object[0];
        }
        ParameterWrapper[] parameterWrappers = getParameterWrappers(method, parameters);
        mMethod = getMethodWrapper(method, parameterWrappers);
        registerClass(method);
        setParameterWrappers(parameterWrappers);
        Mail mail = new Mail(mTimeStamp, mObject, mMethod, mParameters);
        return CHANNEL.send(mail);
    }

    public ObjectWrapper getObject() {
        return mObject;
    }
}
