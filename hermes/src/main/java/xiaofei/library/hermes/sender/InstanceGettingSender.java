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

package xiaofei.library.hermes.sender;

import java.lang.reflect.Method;

import xiaofei.library.hermes.HermesService;
import xiaofei.library.hermes.util.ErrorCodes;
import xiaofei.library.hermes.util.CodeUtils;
import xiaofei.library.hermes.util.HermesException;
import xiaofei.library.hermes.wrapper.MethodWrapper;
import xiaofei.library.hermes.wrapper.ObjectWrapper;
import xiaofei.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class InstanceGettingSender extends Sender {

    public InstanceGettingSender(Class<? extends HermesService> service, ObjectWrapper object) {
        super(service, object);
    }

    @Override
    protected void setParameterWrappers(ParameterWrapper[] parameterWrappers) {
        int length = parameterWrappers.length;
        ParameterWrapper[] tmp = new ParameterWrapper[length - 1];
        for (int i = 1; i < length; ++i) {
            tmp[i - 1] = parameterWrappers[i];
        }
        super.setParameterWrappers(tmp);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) throws HermesException {
        ParameterWrapper parameterWrapper = parameterWrappers[0];
        String methodName;
        try {
            methodName = CodeUtils.decode(parameterWrapper.getData(), String.class);
        } catch (HermesException e) {
            e.printStackTrace();
            throw new HermesException(ErrorCodes.GSON_DECODE_EXCEPTION,
                    "Error occurs when decoding the method name.");
        }
        int length = parameterWrappers.length;
        Class<?>[] parameterTypes = new Class[length - 1];
        for (int i = 1; i < length; ++i) {
            parameterWrapper = parameterWrappers[i];
            parameterTypes[i - 1] = parameterWrapper == null ? null : parameterWrapper.getClassType();
        }
        return new MethodWrapper(methodName, parameterTypes);
    }
}
