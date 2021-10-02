package de.cmtjk.neelix.model.resources.exception;

import de.cmtjk.neelix.logger.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler {

    private static ExceptionHandler instance = null;

    private final StringWriter sw;
    private final PrintWriter pw;
    private final ExceptionAlert ea;

    private ExceptionHandler() {
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        ea = new ExceptionAlert();
    }

    public static ExceptionHandler getInstance() {
        if (instance == null) {
            instance = new ExceptionHandler();
        }
        return instance;
    }

    public void print(SystemException ex) {

        // convert stack trace to string
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        // print in Logfile
        Logger.getInstance().error(exceptionText);

        // show Alert
        ea.showAlert(ex, exceptionText);
    }

    public void closeResources() {
        if (pw != null)
            pw.close();
    }

}
