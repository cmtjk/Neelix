package de.r3r57.neelix.model.resources.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SystemException extends Exception {

    private static final long serialVersionUID = 1L;

    public static SystemException wrap(Throwable exception, ErrorCode errorCode) {
	if (exception instanceof SystemException) {
	    SystemException se = (SystemException) exception;
	    if (errorCode != null && errorCode != se.getErrorCode()) {
		return new SystemException(exception.getMessage(), exception, errorCode);
	    }
	    return se;
	} else {
	    return new SystemException(exception.getMessage(), exception, errorCode);
	}
    }

    public static SystemException wrap(Throwable exception) {
	return wrap(exception, null);
    }

    private ErrorCode errorCode;
    private final Map<String, Object> properties = new TreeMap<String, Object>();

    public SystemException(ErrorCode errorCode) {
	this.errorCode = errorCode;
    }

    public SystemException(String message, ErrorCode errorCode) {
	super(message);
	this.errorCode = errorCode;
    }

    public SystemException(Throwable cause, ErrorCode errorCode) {
	super(cause);
	this.errorCode = errorCode;
    }

    public SystemException(String message, Throwable cause, ErrorCode errorCode) {
	super(message, cause);
	this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
	return errorCode;
    }

    public SystemException setErrorCode(ErrorCode errorCode) {
	this.errorCode = errorCode;
	return this;
    }

    public Map<String, Object> getProperties() {
	return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
	return (T) properties.get(name);
    }

    public SystemException set(String name, Object value) {
	properties.put(name, value);
	return this;
    }

    public void printStackTrace(PrintStream s) {
	synchronized (s) {
	    printStackTrace(new PrintWriter(s));
	}
    }

    public void printStackTrace(PrintWriter s) {
	synchronized (s) {
	    s.println(this);
	    s.println("\t-------------------------------");
	    if (errorCode != null) {
		s.println("\t" + errorCode.getClass().getName() + ":" + errorCode.getCode());
	    }
	    for (Entry<String, Object> entry : properties.entrySet()) {
		s.println("\t" + entry.getKey() + ": " + entry.getValue() + "");
	    }
	    s.println("\t-------------------------------");
	    StackTraceElement[] trace = getStackTrace();
	    for (int i = 0; i < trace.length; i++)
		s.println("\tat " + trace[i]);

	    Throwable ourCause = getCause();
	    if (ourCause != null) {
		ourCause.printStackTrace(s);
	    }
	    s.flush();
	}
	s.close();
    }

}