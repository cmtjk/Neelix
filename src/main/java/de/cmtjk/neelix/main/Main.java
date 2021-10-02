package de.cmtjk.neelix.main;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.logger.Logger;
import de.cmtjk.neelix.model.Model;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.ExceptionHandler;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import de.cmtjk.neelix.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This is the main class of Neelix. It initializes the
 * {@link Logger} and connects the
 * {@link Model}, the {@link View} and
 * the {@link Controller}.
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
