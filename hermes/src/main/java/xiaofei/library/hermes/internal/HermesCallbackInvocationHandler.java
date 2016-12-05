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

import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeUtils;
import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/11.
 */
public class HermesCallbackInvocationHandler implements InvocationHandler {

    private static final String TAG = "HERMES_CALLBACK";

    private long mTimeStamp;

    private int mIndex;

    private IHermesServiceCallback mCallback;

    public HermesCallbackInvocationHandler(long timeStamp, int index, IHermesServiceCallback callback) {
        mTimeStamp = timeStamp;
        mIndex = index;
        mCallback = callback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) {
        try {
            MethodWrapper methodWrapper = new MethodWrapper(method);
            ParameterWrapper[] parameterWrappers = TypeUtils.objectToWrapper(objects);
            CallbackMail callbackMail = new CallbackMail(mTimeStamp, mIndex, methodWrapper, parameterWrappers);
            Reply reply = mCallback.callback(callbackMail);
            if (reply == null) {
                return null;
            }
            if (reply.success()) {
                /**
                 * Note that the returned type should be registered in the remote process.
                 */
                return reply.getResult();
            } else {
                Log.e(TAG, "Error occurs: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            Log.e(TAG, "Error occurs but does not crash the app.", e);
        } catch (RemoteException e) {
            Log.e(TAG, "Error occurs but does not crash the app.", e);
        }
        return null;
    }
}
