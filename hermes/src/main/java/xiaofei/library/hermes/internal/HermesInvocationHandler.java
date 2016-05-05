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

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import xiaofei.library.hermes.sender.Sender;
import xiaofei.library.hermes.sender.SenderDesignator;
import xiaofei.library.hermes.HermesService;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.wrapper.ObjectWrapper;

/**
 * Created by Xiaofei on 16/4/11.
 */
public class HermesInvocationHandler implements InvocationHandler {

    private static final String TAG = "HERMES_INVOCATION";

    private Sender mSender;

    public HermesInvocationHandler(Class<? extends HermesService> service, ObjectWrapper object) {
        mSender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_INVOKE_METHOD, object);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) {
        try {
            Reply reply = mSender.send(method, objects);
            if (reply == null) {
                return null;
            }
            if (reply.success()) {
                return reply.getResult();
            } else {
                Log.e(TAG, "Error occurs. Error " + reply.getErrorCode() + ": " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurs. Error " + e.getErrorCode() + ": " + e.getErrorMessage());
            return null;
        }
    }
}
