package de.cmtjk.neelix.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

/**
 * This class represents the logger.
 *
 * @author Cornelius Matejka
 */

public class Logger {

    private static org.apache.logging.log4j.Logger instance = null;

    private Logger() {
    }

    public static org.apache.logging.log4j.Logger getInstance() {
        if (instance == null) {
            instance = org.apache.logging.log4j.LogManager.getRootLogger();
            initLogger();
        }
        return instance;
    }

    private static void initLogger() {

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("./config/log4j2.xml");
        context.setConfigLocation(file.toURI());

    }
}
