package xiaofei.library.hermes.internal;

import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import xiaofei.library.hermes.IHermesServiceCallback;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeUtils;
import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/11.
 */
public class HermesCallbackInvocationHandler implements InvocationHandler {

    private static final String TAG = "HERMES";

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
                return reply.getResult();
            } else {
                Log.e(TAG, "Error occurs: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
        //TODO 应该先注册
    }
}
