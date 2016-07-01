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

package xiaofei.library.hermes.receiver;

import android.content.Context;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.internal.IHermesServiceCallback;
import xiaofei.library.hermes.internal.HermesCallbackInvocationHandler;
import xiaofei.library.hermes.internal.Reply;
import xiaofei.library.hermes.util.CodeUtils;
import xiaofei.library.hermes.util.HermesCallbackGc;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.ObjectCenter;
import xiaofei.library.hermes.util.TypeCenter;
import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public abstract class Receiver {

    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    protected static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    protected static final HermesCallbackGc HERMES_CALLBACK_GC = HermesCallbackGc.getInstance();

    private long mObjectTimeStamp;

    private Object[] mParameters;

    private IHermesServiceCallback mCallback;

    public Receiver(ObjectWrapper objectWrapper) {
        mObjectTimeStamp = objectWrapper.getTimeStamp();
    }

    protected long getObjectTimeStamp() {
        return mObjectTimeStamp;
    }

    protected Object[] getParameters() {
        return mParameters;
    }

    public void setHermesServiceCallback(IHermesServiceCallback callback) {
        mCallback = callback;
    }

    private Object getProxy(Class<?> clazz, int index, long methodInvocationTimeStamp) {
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new HermesCallbackInvocationHandler(methodInvocationTimeStamp, index, mCallback));
    }

    private static void registerCallbackReturnTypes(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            TYPE_CENTER.register(method.getReturnType());
        }
    }

    private void setParameters(long methodInvocationTimeStamp, ParameterWrapper[] parameterWrappers) throws HermesException {
        if (parameterWrappers == null) {
            mParameters = null;
        } else {
            int length = parameterWrappers.length;
            mParameters = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    mParameters[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);
                    if (clazz != null && clazz.isInterface()) {
                        registerCallbackReturnTypes(clazz); //****
                        mParameters[i] = getProxy(clazz, i, methodInvocationTimeStamp);
                        HERMES_CALLBACK_GC.register(mCallback, mParameters[i], methodInvocationTimeStamp, i);
                    } else if (clazz != null && Context.class.isAssignableFrom(clazz)) {
                        mParameters[i] = Hermes.getContext();
                    } else {
                        String data = parameterWrapper.getData();
                        if (data == null) {
                            mParameters[i] = null;
                        } else {
                            mParameters[i] = CodeUtils.decode(data, clazz);
                        }
                    }
                }
            }
        }
    }

    protected abstract void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws HermesException;

    protected abstract Object invokeMethod() throws HermesException;

    public final Reply action(long methodInvocationTimeStamp, MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) throws HermesException{
        setMethod(methodWrapper, parameterWrappers);
        setParameters(methodInvocationTimeStamp, parameterWrappers);
        Object result = invokeMethod();
        if (result == null) {
            return null;
        } else {
            return new Reply(new ParameterWrapper(result));
        }
    }

}
