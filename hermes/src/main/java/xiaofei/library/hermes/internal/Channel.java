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

package xiaofei.library.hermes.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.util.Pair;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import xiaofei.library.hermes.HermesListener;
import xiaofei.library.hermes.service.HermesService;
import xiaofei.library.hermes.util.CallbackManager;
import xiaofei.library.hermes.util.CodeUtils;
import xiaofei.library.hermes.util.ErrorCodes;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeCenter;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/11.
 */
public class Channel {

    private static final String TAG = "Channel";

    private static Channel sInstance = null;

    private HashMap<Class<? extends HermesService>, IHermesService> mHermesServices = new HashMap<Class<? extends HermesService>, IHermesService>();

    private HashMap<Class<? extends HermesService>, HermesServiceConnection> mHermesServiceConnections = new HashMap<Class<? extends HermesService>, HermesServiceConnection>();

    private HashMap<Class<? extends HermesService>, Boolean> mBindings = new HashMap<Class<? extends HermesService>, Boolean>();

    private HashMap<Class<? extends HermesService>, Boolean> mBounds = new HashMap<Class<? extends HermesService>, Boolean>();

    private HermesListener mListener = null;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private static final CallbackManager CALLBACK_MANAGER = CallbackManager.getInstance();

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private IHermesServiceCallback mHermesServiceCallback = new IHermesServiceCallback.Stub() {

        private Object[] getParameters(ParameterWrapper[] parameterWrappers) throws HermesException {
            if (parameterWrappers == null) {
                parameterWrappers = new ParameterWrapper[0];
            }
            int length = parameterWrappers.length;
            Object[] result = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    result[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);
                    //TODO check
                    String data = parameterWrapper.getData();
                    if (data == null) {
                        result[i] = null;
                    } else {
                        result[i] = CodeUtils.decode(data, clazz);
                    }
                }
            }
            return result;
        }

        public Reply callback(CallbackMail mail) {
            Log.v("eric zhao", "callback");
            final Pair<Boolean, Object> pair = CALLBACK_MANAGER.getCallback(mail.getTimeStamp(), mail.getIndex());
            if (pair == null) {
                return null;
            }
            final Object callback = pair.second;
            if (callback == null) {
                return new Reply(ErrorCodes.CALLBACK_NOT_ALIVE, "");
            }
            boolean uiThread = pair.first;
            try {
                final Method method = TYPE_CENTER.getMethod(callback.getClass(), mail.getMethod());
                final Object[] parameters = getParameters(mail.getParameters());
                if (uiThread) {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                method.invoke(callback, parameters);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return null;
                }
                Object result = method.invoke(callback, parameters);
                if (result == null) {
                    return null;
                }
                return new Reply(new ParameterWrapper(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    private Channel() {

    }

    public static synchronized Channel getInstance() {
        if (sInstance == null) {
            sInstance = new Channel();
        }
        return sInstance;
    }

    public void bind(Context context, Class<? extends HermesService> service) {
        Log.v("eric zhao", "bind");
        synchronized (mBounds) {
            if (mBounds.get(service)) {
                return;
            }
        }
        synchronized (mBindings) {
            if (mBindings.get(service)) {
                return;
            }
            mBindings.put(service, true);
        }
        HermesServiceConnection connection = new HermesServiceConnection(service);
        synchronized (mHermesServiceConnections) {
            mHermesServiceConnections.put(service, connection);
        }
        Intent intent = new Intent(context, service);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context, Class<? extends HermesService> service) {
        synchronized (mBounds) {
            if (mBounds.get(service)) {
                synchronized (mHermesServiceConnections) {
                    HermesServiceConnection connection = mHermesServiceConnections.get(service);
                    if (connection != null) {
                        context.unbindService(connection);
                    }
                }
            }
        }
    }

    public Reply send(Class<? extends HermesService> service, Mail mail) {
        IHermesService hermesService;
        synchronized (mHermesServices) {
            hermesService = mHermesServices.get(service);
        }
        try {
            if (hermesService == null) {
                return new Reply(ErrorCodes.SERVICE_UNAVAILABLE,
                        "Service Unavailable: Check whether you have init Hermes.");
            }
            return hermesService.send(mail);
        } catch (RemoteException e) {
            return new Reply(ErrorCodes.REMOTE_EXCEPTION, "Remote Exception: Check whether "
                    + "the process you are communicating with is still alive.");
        }
    }

    public void gc(Class<? extends HermesService> service, List<Long> timeStamps) {
        IHermesService hermesService;
        synchronized (mHermesServices) {
            hermesService = mHermesServices.get(service);
        }
        try {
            hermesService.gc(timeStamps);
        } catch (RemoteException e) {

        }
    }

    public void setHermesListener(HermesListener listener) {
        mListener = listener;
    }

    public boolean isConnected(Class<? extends HermesService> service) {
        IHermesService hermesService;
        synchronized (mHermesServices) {
            hermesService = mHermesServices.get(service);
        }
        if (hermesService == null) {
            return false;
        }
        return hermesService.asBinder().pingBinder();
    }

    private class HermesServiceConnection implements ServiceConnection {

        private Class<? extends HermesService> mClass;

        HermesServiceConnection(Class<? extends HermesService> service) {
            mClass = service;
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v("eric zhao", "onServiceConnected");
            synchronized (mBounds) {
                mBounds.put(mClass, true);
            }
            synchronized (mBindings) {
                mBindings.put(mClass, false);
            }
            IHermesService hermesService = IHermesService.Stub.asInterface(service);;
            synchronized (mHermesServices) {
                mHermesServices.put(mClass, hermesService);
            }
            try {
                hermesService.register(mHermesServiceCallback, Process.myPid());
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "Remote Exception: Check whether "
                        + "the process you are communicating with is still alive.");
                return;
            }
            if (mListener != null) {
                mListener.onInitSuccess(mClass);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mHermesServices = null;
            synchronized (mBounds) {
                mBounds.put(mClass, false);
            }
            synchronized (mBindings) {
                mBindings.put(mClass, false);
            }
            if (mListener != null) {
                mListener.onDisconnected(mClass);
            }
        }
    }
}
