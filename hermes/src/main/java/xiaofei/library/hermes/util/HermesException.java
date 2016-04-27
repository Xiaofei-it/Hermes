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

package xiaofei.library.hermes.util;

/**
 * Created by Xiaofei on 16/4/15.
 */
public class HermesException extends Exception {

    private int mErrorCode;

    private String mErrorMessage;

    public HermesException(int errorCode, String errorMessage) {
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    public HermesException(int errorCode, String errorMessage, Throwable t) {
        super(t);
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
