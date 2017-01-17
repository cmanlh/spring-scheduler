package com.lifeonwalden.springscheduling.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface ExceptionUtil {
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
