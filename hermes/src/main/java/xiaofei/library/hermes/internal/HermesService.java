package xiaofei.library.hermes.internal;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.widget.Toast;

import xiaofei.library.hermes.IHermesService;
import xiaofei.library.hermes.IHermesServiceCallback;
import xiaofei.library.hermes.receiver.Receiver;
import xiaofei.library.hermes.receiver.ReceiverDesignator;
import xiaofei.library.hermes.util.HermesException;

public class HermesService extends Service {

    private IHermesServiceCallback mCallback;

    private final IHermesService.Stub mBinder = new IHermesService.Stub() {
        @Override
        public Reply send(Mail mail) {
            try {
                Receiver receiver = ReceiverDesignator.getReceiver(mail.getObject());
                if (mCallback != null) {
                    receiver.setHermesServiceCallback(mCallback);
                }
                return receiver.action(mail.getTimeStamp(), mail.getMethod(), mail.getParameters());
            } catch (HermesException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getErrorMessage());
            }
        }

        @Override
        public void register(IHermesServiceCallback callback) throws RemoteException {
            synchronized (HermesService.class) {
                if (mCallback == null) {
                    mCallback = callback;
                }
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
