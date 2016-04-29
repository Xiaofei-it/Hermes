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
import java.util.List;

import xiaofei.library.hermes.HermesListener;
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

    private IHermesService mHermesService = null;

    private HermesServiceConnection mHermesServiceConnection = null;

    private volatile boolean mBinding = false;

    private volatile boolean mBound = false;

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

    public void bind(Context context) {
        Log.v("eric zhao", "bind");
        if (mBinding || mBound) {
            return;
        }
        mBinding = true;
        mHermesServiceConnection = new HermesServiceConnection();
        Intent intent = new Intent(context, HermesService.class);
        context.bindService(intent, mHermesServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context) {
        if (mBound) {
            context.unbindService(mHermesServiceConnection);
        }
    }

    public Reply send(Mail mail) {
        try {
            if (mHermesService == null) {
                return new Reply(ErrorCodes.SERVICE_UNAVAILABLE,
                        "Service Unavailable: Check whether you have init Hermes.");
            }
            return mHermesService.send(mail);
        } catch (RemoteException e) {
            return new Reply(ErrorCodes.REMOTE_EXCEPTION, "Remote Exception: Check whether "
                    + "the process you are communicating with is still alive.");
        }
    }

    public void gc(List<Long> timeStamps) {
        try {
            mHermesService.gc(timeStamps);
        } catch (RemoteException e) {

        }
    }

    public void setHermesListener(HermesListener listener) {
        mListener = listener;
    }

    public boolean isConnected() {
        if (mHermesService == null) {
            return false;
        }
        return mHermesService.asBinder().pingBinder();
    }

    private class HermesServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v("eric zhao", "onServiceConnected");
            mBound = true;
            mBinding = false;
            mHermesService = IHermesService.Stub.asInterface(service);
            try {
                mHermesService.register(mHermesServiceCallback, Process.myPid());
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "Remote Exception: Check whether "
                        + "the process you are communicating with is still alive.");
                return;
            }
            if (mListener != null) {
                mListener.onInitSuccess();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mHermesService = null;
            mBound = false;
            mBinding = false;
            if (mListener != null) {
                mListener.onDisconnected();
            }
        }
    }
}
