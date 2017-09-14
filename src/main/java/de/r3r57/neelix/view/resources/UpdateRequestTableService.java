package de.r3r57.neelix.view.resources;

import java.util.List;

import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.Request;
import de.r3r57.neelix.view.resources.tabs.requestlist.RequestListTab;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateRequestTableService extends Service<Void> {

    private RequestListTab requestTableTab;
    private Model model;

    public UpdateRequestTableService(Model model, RequestListTab requestListTab) {
	this.requestTableTab = requestListTab;
	this.model = model;
    }

    @Override
    protected Task<Void> createTask() {
	return new Task<Void>() {

	    @Override
	    protected Void call() throws Exception {
		List<Request> result = model.getRequestsFromLastSevenDays();
		Platform.runLater(new Runnable() {

		    @Override
		    public void run() {
			requestTableTab.populate(result);
		    }
		});
		return null;
	    }

	};
    }
}
