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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;

import xiaofei.library.hermes.receiver.Receiver;
import xiaofei.library.hermes.receiver.ReceiverDesignator;
import xiaofei.library.hermes.util.HermesException;

public class HermesService extends Service {

    private HashMap<Integer, IHermesServiceCallback> mCallbacks = new HashMap<Integer, IHermesServiceCallback>();

    private final IHermesService.Stub mBinder = new IHermesService.Stub() {
        @Override
        public Reply send(Mail mail) {
            try {
                Receiver receiver = ReceiverDesignator.getReceiver(mail.getObject());
                int pid = mail.getPid();
                IHermesServiceCallback callback = mCallbacks.get(pid);
                if (callback != null) {
                    receiver.setHermesServiceCallback(callback);
                }
                return receiver.action(mail.getTimeStamp(), mail.getMethod(), mail.getParameters());
            } catch (HermesException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getErrorMessage());
            }
        }

        @Override
        public void register(IHermesServiceCallback callback, int pid) throws RemoteException {
            synchronized (HermesService.class) {
                mCallbacks.put(pid, callback);
            }
        }
    };

    public HermesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
