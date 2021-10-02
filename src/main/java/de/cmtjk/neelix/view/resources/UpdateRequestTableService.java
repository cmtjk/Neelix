package de.cmtjk.neelix.view.resources;

import de.cmtjk.neelix.model.Model;
import de.cmtjk.neelix.model.resources.Request;
import de.cmtjk.neelix.view.resources.tabs.requestlist.RequestListTab;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class UpdateRequestTableService extends Service<Void> {

    private final RequestListTab requestTableTab;
    private final Model model;

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
