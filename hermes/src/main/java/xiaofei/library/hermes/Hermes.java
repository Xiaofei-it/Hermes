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

package xiaofei.library.hermes;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Proxy;

import xiaofei.library.hermes.internal.Channel;
import xiaofei.library.hermes.internal.HermesInvocationHandler;
import xiaofei.library.hermes.internal.Reply;
import xiaofei.library.hermes.sender.Sender;
import xiaofei.library.hermes.sender.SenderDesignator;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeCenter;
import xiaofei.library.hermes.util.TypeUtils;
import xiaofei.library.hermes.wrapper.ObjectWrapper;

/**
 * Created by Xiaofei on March 31, 2016.
 *
 */
public class Hermes {

    private static final String TAG = "HERMES";

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private static final Channel CHANNEL = Channel.getInstance();

    public static void register(Object object) {
        register(object.getClass());
    }

    public static void register(Class<?> clazz) {
        TYPE_CENTER.register(clazz);
    }

    private static <T> T getProxy(ObjectWrapper object) {
        Class<?> clazz = object.getObjectClass();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                    new HermesInvocationHandler(object));

    }

    public static <T> T newInstance(Class<T> clazz, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_NEW);
        Sender sender = SenderDesignator.getPostOffice(SenderDesignator.TYPE_NEW_INSTANCE, object);
        try {
            Reply reply = sender.send(null, parameters);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during creating instance. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(object);
    }

    public static <T> T getInstance(Class<T> clazz, Object... parameters) {
        return getInstanceWithMethodName(clazz, "", parameters);
    }

    public static <T> T getInstanceWithMethodName(Class<T> clazz, String methodName, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(SenderDesignator.TYPE_GET_INSTANCE, object);
        if (parameters == null) {
            parameters = new Object[0];
        }
        int length = parameters.length;
        Object[] tmp = new Object[length + 1];
        tmp[0] = methodName;
        for (int i = 0; i < length; ++i) {
            tmp[i + 1] = parameters[i];
        }
        try {
            Reply reply = sender.send(null, tmp);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during getting instance. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(object);
    }

    public static <T> T getUtilityClass(Class<T> clazz) {
        TypeUtils.validateServiceInterface(clazz);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_CLASS_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(SenderDesignator.TYPE_GET_UTILITY_CLASS, object);
        try {
            Reply reply = sender.send(null, null);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during getting utility class. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_CLASS);
        return getProxy(object);
    }

    public static void init(Context context) {
        CHANNEL.bind(context);
    }

    public static void destroy(Context context) {
        CHANNEL.unbind(context);
    }

    public static boolean isConnected() {
        return CHANNEL.isConnected();
    }

    public static void setHermesListener(HermesListener listener) {
        CHANNEL.setHermesListener(listener);
    }

}
