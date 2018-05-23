package com.qa.utils;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomWriterAppender extends WriterAppender {

    private static Map<String, StringBuffer> buffers = new ConcurrentHashMap<>();

    @Override
    public void append(LoggingEvent event) {
        String toAppend = this.layout.format(event);
        StringBuffer stringBuffer = getBuffer(event.getThreadName());
        stringBuffer.append(toAppend);
        stringBuffer.append("</br>");
    }

    public static String getBufferContents(String threadName) {
        StringBuffer stringBuffer = buffers.get(threadName);
        if (stringBuffer == null) {
            return "";
        } else {
            return stringBuffer.toString();
        }
    }

    private static StringBuffer getBuffer(String threadName) {
        StringBuffer stringBuffer = buffers.get(threadName);
        if (stringBuffer == null) {
            stringBuffer = createBuffer(threadName);
        }
        return stringBuffer;
    }

    private static StringBuffer createBuffer(String threadName) {
        StringBuffer stringBuffer = new StringBuffer();
        buffers.put(threadName, stringBuffer);
        return stringBuffer;
    }
}
