package de.cmtjk.neelix.view.resources;

import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.ExceptionHandler;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class EventHandlerService {

    private static EventHandlerService instance = null;
    private ProgressIndicator progressIndicator = new ProgressIndicator();
    EventHandler<WorkerStateEvent> failed = new EventHandler<WorkerStateEvent>() {

        @Override
        public void handle(WorkerStateEvent event) {
            SystemException ex;
            if (event.getSource().getException() instanceof RuntimeException) {
                ex = SystemException.wrap(event.getSource().getException(), ErrorCode.CRITICAL);
            } else {
                ex = (SystemException) event.getSource().getException();
            }

            ExceptionHandler.getInstance().print(ex);
            progressIndicator.setVisible(false);
            event.consume();
        }

    };
    EventHandler<WorkerStateEvent> running = new EventHandler<WorkerStateEvent>() {

        @Override
        public void handle(WorkerStateEvent event) {
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(0);
            progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            event.consume();
        }

    };
    EventHandler<WorkerStateEvent> succeed = new EventHandler<WorkerStateEvent>() {

        @Override
        public void handle(WorkerStateEvent event) {
            progressIndicator.setProgress(100.0);
            progressIndicator.setVisible(false);
            event.consume();
        }

    };

    private EventHandlerService() {
    }

    public static EventHandlerService getInstance() {
        if (instance == null) {
            instance = new EventHandlerService();
        }
        return instance;
    }

    public EventHandler<WorkerStateEvent> getFailed() {
        return failed;
    }

    public EventHandler<WorkerStateEvent> getRunning() {
        return running;
    }

    public EventHandler<WorkerStateEvent> getSucceed() {
        return succeed;
    }

    public ProgressIndicator getProgressIndicator() {
        return this.progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

}
