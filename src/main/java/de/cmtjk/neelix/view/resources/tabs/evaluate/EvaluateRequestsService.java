package de.cmtjk.neelix.view.resources.tabs.evaluate;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.time.LocalDate;

public class EvaluateRequestsService {

    private final String CSVALL;
    private final String CSVRTMO;
    private final String HTML;

    private Controller controller;

    private EventHandler<WorkerStateEvent> onSucceed;
    private EventHandler<WorkerStateEvent> onFailure;
    private EventHandler<WorkerStateEvent> onRunning;

    public EvaluateRequestsService(String csvallPattern, String csvRTMOPattern, String htmlPattern) {
        CSVALL = csvallPattern;
        CSVRTMO = csvRTMOPattern;
        HTML = htmlPattern;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public EventHandler<WorkerStateEvent> getOnSucceed() {
        return onSucceed;
    }

    public void setOnSucceeded(EventHandler<WorkerStateEvent> onSucceed) {
        this.onSucceed = onSucceed;
    }

    public EventHandler<WorkerStateEvent> getOnFailure() {
        return onFailure;
    }

    public void setOnFailed(EventHandler<WorkerStateEvent> onFailure) {
        this.onFailure = onFailure;
    }

    public EventHandler<WorkerStateEvent> getOnRunning() {
        return onRunning;
    }

    public void setOnRunning(EventHandler<WorkerStateEvent> onRunning) {
        this.onRunning = onRunning;
    }

    public void evaluate(String fileType, LocalDate beginDate, LocalDate endDate) throws SystemException {

        if (controller == null) {
            throw new SystemException(ErrorCode.ILLEGAL_STATE).set("class", this.getClass()).set("method", "evaluate")
                    .set("messasge", "controller not set");
        }

        Task<Void> evaluateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (HTML.equals(fileType)) {
                    controller.evaluateRequests(beginDate, endDate);
                } else if (CSVALL.equals(fileType)) {
                    controller.createCsvFileAll(beginDate, endDate);
                } else if (CSVRTMO.equals(fileType)) {
                    controller.createCsvFileOverview(beginDate, endDate);
                }
                return null;
            }
        };

        if (onFailure != null) {
            evaluateTask.setOnFailed(onFailure);
        }
        if (onRunning != null) {
            evaluateTask.setOnRunning(onRunning);
        }
        if (onSucceed != null) {
            evaluateTask.setOnSucceeded(onSucceed);
        }

        Thread thread = new Thread(evaluateTask);
        thread.setDaemon(true);
        thread.start();

    }

}
