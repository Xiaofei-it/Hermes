package xiaofei.library.hermes;

import xiaofei.library.hermes.IHermesServiceCallback;
import xiaofei.library.hermes.internal.Mail;
import xiaofei.library.hermes.internal.Reply;

interface IHermesService {
    Reply send(in Mail mail);
    void register(IHermesServiceCallback callback);
}