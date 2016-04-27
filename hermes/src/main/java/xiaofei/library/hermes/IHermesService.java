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

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import xiaofei.library.hermes.internal.Mail;
import xiaofei.library.hermes.internal.Reply;

public interface IHermesService extends IInterface {

    abstract class Stub extends Binder implements IHermesService {

        private static final String DESCRIPTOR = "xiaofei.library.hermes.IHermesService";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
            Log.v("eric zhao", "Stub init");
        }

        public static IHermesService asInterface(IBinder obj) {
            Log.v("eric zhao", "asInterface");
            if ((obj==null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof IHermesService))) {
                Log.v("eric zhao", "asInterface branch 1");
                return ((IHermesService)iin);
            }
            Log.v("eric zhao", "asInterface branch 2");
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            Log.v("eric zhao", "asBinder");
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTOR);
                    return true;
                case TRANSACTION_send:
                    data.enforceInterface(DESCRIPTOR);
                    Mail _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = Mail.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Reply _result = this.send(_arg0);
                    reply.writeNoException();
                    if ((_result!=null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_register:
                    data.enforceInterface(DESCRIPTOR);
                    IHermesServiceCallback _arg1;
                    IBinder iBinder = data.readStrongBinder();
                    _arg1 = IHermesServiceCallback.Stub.asInterface(iBinder);
                    int pid = data.readInt();
                    this.register(_arg1, pid);
                    reply.writeNoException();
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IHermesService {

            private IBinder mRemote;

            Proxy(IBinder remote) {
                Log.v("eric zhao", "Proxy init");
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                Log.v("eric zhao", "Proxy asBinder");
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public Reply send(Mail mail) throws RemoteException {
                Log.v("eric zhao", "proxy send");
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                Reply _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((mail!=null)) {
                        _data.writeInt(1);
                        mail.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
                    _reply.readException();
                    if ((0!=_reply.readInt())) {
                        _result = Reply.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void register(IHermesServiceCallback callback, int pid) throws RemoteException {
                Log.v("eric zhao", "register");
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    _data.writeInt(pid);
                    mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_send = (IBinder.FIRST_CALL_TRANSACTION + 0);

        static final int TRANSACTION_register = (IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    Reply send(Mail mail) throws RemoteException;

    void register(IHermesServiceCallback callback, int pid) throws RemoteException;
}
