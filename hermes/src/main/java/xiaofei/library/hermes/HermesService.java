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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import xiaofei.library.hermes.internal.IHermesService;
import xiaofei.library.hermes.internal.IHermesServiceCallback;
import xiaofei.library.hermes.internal.Mail;
import xiaofei.library.hermes.internal.Reply;
import xiaofei.library.hermes.receiver.Receiver;
import xiaofei.library.hermes.receiver.ReceiverDesignator;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.ObjectCenter;

public abstract class HermesService extends Service {

    private static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    private ConcurrentHashMap<Integer, IHermesServiceCallback> mCallbacks = new ConcurrentHashMap<Integer, IHermesServiceCallback>();

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
            mCallbacks.put(pid, callback);
        }

        @Override
        public void gc(List<Long> timeStamps) throws RemoteException {
            OBJECT_CENTER.deleteObjects(timeStamps);
        }
    };

    public HermesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static class HermesService0 extends HermesService {}

    public static class HermesService1 extends HermesService {}

    public static class HermesService2 extends HermesService {}

    public static class HermesService3 extends HermesService {}

    public static class HermesService4 extends HermesService {}

    public static class HermesService5 extends HermesService {}

    public static class HermesService6 extends HermesService {}

    public static class HermesService7 extends HermesService {}

    public static class HermesService8 extends HermesService {}

    public static class HermesService9 extends HermesService {}

}
