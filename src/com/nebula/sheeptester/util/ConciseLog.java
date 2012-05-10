/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.util.Date;
import org.apache.commons.logging.impl.SimpleLog;

/**
 *
 * @author shevek
 */
public class ConciseLog extends SimpleLog {

    private String shortLogName = null;

    public ConciseLog(String name) {
        super(name);
    }

    @Override
    protected void log(int type, Object message, Throwable t) {
        // Use a string buffer for better performance
        StringBuffer buf = new StringBuffer();

        // Append date-time if so configured
        if (showDateTime) {
            Date now = new Date();
            String dateText;
            synchronized (dateFormatter) {
                dateText = dateFormatter.format(now);
            }
            buf.append(dateText);
            buf.append(" ");
        }

        // Append a readable representation of the log level
        switch (type) {
            case SimpleLog.LOG_LEVEL_TRACE:
                buf.append("[TRACE] ");
                break;
            case SimpleLog.LOG_LEVEL_DEBUG:
                buf.append("[DEBUG] ");
                break;
            case SimpleLog.LOG_LEVEL_INFO:
                buf.append("[INFO] ");
                break;
            case SimpleLog.LOG_LEVEL_WARN:
                buf.append("[WARN] ");
                break;
            case SimpleLog.LOG_LEVEL_ERROR:
                buf.append("[ERROR] ");
                break;
            case SimpleLog.LOG_LEVEL_FATAL:
                buf.append("[FATAL] ");
                break;
        }

        // Append the name of the log instance if so configured
        if (showShortName) {
            if (shortLogName == null) {
                // Cut all but the last component of the name for both styles
                String name = logName.substring(logName.lastIndexOf(".") + 1);
                shortLogName = name.substring(name.lastIndexOf("/") + 1);
            }
            buf.append(String.valueOf(shortLogName)).append(" - ");
        } else if (showLogName) {
            buf.append(String.valueOf(logName)).append(" - ");
        }

        // Append the message
        buf.append(String.valueOf(message));

        // Append stack trace if not null
        if (t != null) {
            buf.append("\n");
            java.io.StringWriter sw = new java.io.StringWriter(1024);
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }

        // Print to the appropriate destination
        write(buf);
    }
}