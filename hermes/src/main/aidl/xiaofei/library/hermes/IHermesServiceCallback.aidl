package xiaofei.library.hermes;

import xiaofei.library.hermes.internal.CallbackMail;
import xiaofei.library.hermes.internal.Reply;

interface IHermesServiceCallback {
    Reply callback(in CallbackMail mail);
}