package de.r3r57.neelix.view.resources.tabs.request;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SaveRequestTaskService {

    private Service<Void> service = null;

    public void initTask(Controller controller) {
	service = new Service<Void>() {

	    @Override
	    protected Task<Void> createTask() {
		return new Task<Void>() {

		    @Override
		    protected Void call() throws Exception {
			controller.saveRequest();
			return null;
		    }

		};
	    }

	};
    }

    public Service<Void> getService() throws SystemException {
	if (service == null) {
	    throw new SystemException(ErrorCode.ILLEGAL_STATE).set("class", this.getClass())
		    .set("method", "getService()").set("messasge", "controller not set");
	}
	return service;
    }

}
