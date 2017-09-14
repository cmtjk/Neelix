package de.r3r57.neelix.logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.ExceptionHandler;
import de.r3r57.neelix.model.resources.exception.SystemException;

/**
 * This class represents the logger.
 *
 * @author Cornelius Matejka
 */

public class Logger {

    private static org.apache.log4j.Logger instance = null;

    private Logger() {
    }

    public static org.apache.log4j.Logger getInstance() {
	if (instance == null) {
	    instance = org.apache.log4j.Logger.getRootLogger();
	    initLogger();
	}
	return instance;
    }

    private static void initLogger() {
	Properties props = new Properties();
	try {
	    props.load(new FileInputStream("./config/log4j.properties"));
	    PropertyConfigurator.configure(props);
	} catch (FileNotFoundException e) {
	    ExceptionHandler.getInstance().print(new SystemException(e, ErrorCode.FILE_EXCEPTION).set("class", "Logger")
		    .set("method", "initLogger"));
	} catch (IOException e) {
	    ExceptionHandler.getInstance().print(
		    new SystemException(e, ErrorCode.IO_EXCEPTION).set("class", "Logger").set("method", "initLogger"));
	}

    }
}
