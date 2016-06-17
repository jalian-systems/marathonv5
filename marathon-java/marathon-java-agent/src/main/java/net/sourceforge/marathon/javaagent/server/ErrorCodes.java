/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javaagent.server;

public class ErrorCodes {

    public static final String SUCCESS_STRING = "success";
    public static final int SUCCESS = 0;
    public static final int NO_SUCH_SESSION = 6;
    public static final int NO_SUCH_ELEMENT = 7;
    public static final int NO_SUCH_FRAME = 8;
    public static final int UNKNOWN_COMMAND = 9;
    public static final int STALE_ELEMENT_REFERENCE = 10;
    public static final int ELEMENT_NOT_VISIBLE = 11;
    public static final int INVALID_ELEMENT_STATE = 12;
    public static final int UNHANDLED_ERROR = 13;
    public static final int ELEMENT_NOT_SELECTABLE = 15;
    public static final int JAVASCRIPT_ERROR = 17;
    public static final int XPATH_LOOKUP_ERROR = 19;
    public static final int TIMEOUT = 21;
    public static final int NO_SUCH_WINDOW = 23;
    public static final int INVALID_COOKIE_DOMAIN = 24;
    public static final int UNABLE_TO_SET_COOKIE = 25;
    public static final int UNEXPECTED_ALERT_PRESENT = 26;
    public static final int NO_ALERT_PRESENT = 27;
    public static final int ASYNC_SCRIPT_TIMEOUT = 28;
    public static final int INVALID_ELEMENT_COORDINATES = 29;
    public static final int IME_NOT_AVAILABLE = 30;
    public static final int IME_ENGINE_ACTIVATION_FAILED = 31;
    public static final int INVALID_SELECTOR_ERROR = 32;
    public static final int SESSION_NOT_CREATED = 33;
    public static final int MOVE_TARGET_OUT_OF_BOUNDS = 34;
    public static final int SQL_DATABASE_ERROR = 35;
    public static final int INVALID_XPATH_SELECTOR = 51;
    public static final int INVALID_XPATH_SELECTOR_RETURN_TYPER = 52;
    // The following error codes are derived straight from HTTP return codes.
    public static final int METHOD_NOT_ALLOWED = 405;

}
