package xiaofei.library.hermes.internal;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.sender.Sender;
import xiaofei.library.hermes.sender.SenderDesignator;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.util.TypeUtils;
import xiaofei.library.hermes.wrapper.ObjectWrapper;

/**
 * Created by Xiaofei on 16/4/11.
 */
public class HermesInvocationHandler implements InvocationHandler {

    private static final String TAG = "HERMES";

    private Sender mSender;

    public HermesInvocationHandler(ObjectWrapper object) {
        mSender = SenderDesignator.getPostOffice(SenderDesignator.TYPE_INVOKE_METHOD, object);
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
