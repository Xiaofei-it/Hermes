/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xiaofei.library.hermes.sender;

import android.content.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import xiaofei.library.hermes.annotation.Background;
import xiaofei.library.hermes.annotation.WeakRef;
import xiaofei.library.hermes.internal.Channel;
import xiaofei.library.hermes.internal.Mail;
import xiaofei.library.hermes.internal.Reply;
import xiaofei.library.hermes.HermesService;
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

    private Class<? extends HermesService> mService;

    public Sender(Class<? extends HermesService> service, ObjectWrapper object) {
        mService = service;
        mObject = object;
    }

    protected abstract MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) throws HermesException;

    protected void setParameterWrappers(ParameterWrapper[] parameterWrappers) {
        mParameters = parameterWrappers;
    }

    /**
     * constructor is like method.
     *
     * method: parameter --> no need to register, but should be registered in the remote process (* by hand, or user will forget to add annotation), especially when the type is subclass.
     *                       should have the same class id and can be inverted by json.
     *         callback parameter --> see below
     *         return type --> should be registered (**)(esp subclass), no need to registered in the remote process.
     *                         should have the same class id and can be inverted by json.
     *
     * callback: parameter --> should be registered (***)(esp subclass), no need to registered in the remote process.
     *           return type --> no need to register, but should be registered in the remote process (****)(esp subclass).
     *
     * In Hermes, we can control the registration of classes, but the subclasses should be registered by users.
     */


    private void registerClass(Method method) throws HermesException {
        if (method == null) {
            return;
        }
        Class<?>[] classes = method.getParameterTypes();
        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) {
                TYPE_CENTER.register(clazz);
                registerCallbackMethodParameterTypes(clazz);
            }
        }
        TYPE_CENTER.register(method.getReturnType()); //**
    }

    private void registerCallbackMethodParameterTypes(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                TYPE_CENTER.register(parameterType); //***
            }
        }
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
                } else if (Context.class.isAssignableFrom(classes[i])) {
                    parameterWrappers[i] = new ParameterWrapper(TypeUtils.getContextClass(classes[i]), null);
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


    public synchronized final Reply send(Method method, Object[] parameters) throws HermesException {
        mTimeStamp = TimeStampGenerator.getTimeStamp();
        if (parameters == null) {
            parameters = new Object[0];
        }
        ParameterWrapper[] parameterWrappers = getParameterWrappers(method, parameters);
        MethodWrapper methodWrapper = getMethodWrapper(method, parameterWrappers);
        registerClass(method);
        setParameterWrappers(parameterWrappers);
        Mail mail = new Mail(mTimeStamp, mObject, methodWrapper, mParameters);
        mMethod = methodWrapper;
        return CHANNEL.send(mService, mail);
    }

    public ObjectWrapper getObject() {
        return mObject;
    }
}
