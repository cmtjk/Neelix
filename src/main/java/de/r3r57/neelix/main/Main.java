package de.r3r57.neelix.main;

import java.io.IOException;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.logger.Logger;
import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.ExceptionHandler;
import de.r3r57.neelix.model.resources.exception.SystemException;
import de.r3r57.neelix.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * This is the main class of Neelix. It initializes the
 * {@link de.r3r57.neelix.logger.Logger} and connects the
 * {@link de.r3r57.neelix.model.Model}, the {@link de.r3r57.neelix.view.View} and
 * the {@link de.r3r57.neelix.controller.Controller}.
 *
 * @author Cornelius Matejka
 */

public class Main extends Application {

    ExceptionHandler eh = ExceptionHandler.getInstance();

    public static void main(String[] args) {
	launch();
    }

    @Override
    public void start(Stage primaryStage) {

	Logger.getInstance().trace("Starting Neelix (user:" + System.getProperty("user.name") + ")");

	try {

	    Model model = new Model();
	    model.init();

	    View view = new View(model);
	    Controller controller = new Controller(model, view);

	    view.setController(controller);

	} catch (IOException e) {
	    eh.print(new SystemException(e.getMessage(), e, ErrorCode.IO_EXCEPTION).set("class", this.getClass())
		    .set("method", "start()"));
	    Platform.exit();
	} catch (SystemException e) {
	    eh.print(e);
	    Platform.exit();
	} catch (Throwable e) {
	    eh.print(new SystemException(e.getMessage(), ErrorCode.CRITICAL).set("class", this.getClass())
		    .set("method", "start()").set("cause", "unknown")
		    .set("IMPORTANT", "Bitte ungebdingt Bescheid geben. Danke!"));
	    Platform.exit();
	}
    }

    @Override
    public void stop() {
	eh.closeResources();
    }

}
