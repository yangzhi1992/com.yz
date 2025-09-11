package com.commons.common.utils;

import com.commons.common.support.FrameworkConstants;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 *
 */
public final class ExceptionTool {

    private ExceptionTool() {

    }

    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    public static String getCurrentStackTrack() {
        StringBuilder sb = new StringBuilder();
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            int length = stackTraceElements.length;
            if (length > FrameworkConstants.MAX_STACK_DEPTH) {
                length = FrameworkConstants.MAX_STACK_DEPTH;
            }
            for (int i = 0; i < length; i++) {
                sb.append(stackTraceElements[i]).append(FrameworkConstants.NEXT_DELIM);
                if (sb.length() > FrameworkConstants.MAX_LOG_MSG_LENGTH) {
                    break;
                }
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

}
