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

package xiaofei.library.hermes.receiver;

import xiaofei.library.hermes.util.ErrorCodes;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.wrapper.ObjectWrapper;

/**
 * Created by Xiaofei on 16/4/10.
 */
public class ReceiverDesignator {
    public static Receiver getReceiver(ObjectWrapper objectWrapper) throws HermesException {
        int type = objectWrapper.getType();
        switch (type) {
            case ObjectWrapper.TYPE_OBJECT_TO_NEW:
                return new InstanceCreatingReceiver(objectWrapper);
            case ObjectWrapper.TYPE_OBJECT_TO_GET:
                return new InstanceGettingReceiver(objectWrapper);
            case ObjectWrapper.TYPE_CLASS:
                return new UtilityReceiver(objectWrapper);
            case ObjectWrapper.TYPE_OBJECT:
                return new ObjectReceiver(objectWrapper);
            case ObjectWrapper.TYPE_CLASS_TO_GET:
                return new UtilityGettingReceiver(objectWrapper);
            default:
                throw new HermesException(ErrorCodes.ILLEGAL_PARAMETER_EXCEPTION,
                        "Type " + type + " is not supported.");
        }
    }
}
