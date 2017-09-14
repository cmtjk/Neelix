package de.r3r57.neelix.view.resources;

import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.view.resources.tabs.statistic.StatisticTab;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateStatisticService extends Service<Void> {

    private StatisticTab statisticTab;
    private Model model;

    public UpdateStatisticService(Model model, StatisticTab statisticTab) {
	this.statisticTab = statisticTab;
	this.model = model;
    }

    @Override
    protected Task<Void> createTask() {
	return new Task<Void>() {

	    @Override
	    protected Void call() throws Exception {
		int[] result = model.getRequestStatistic();
		Platform.runLater(new Runnable() {

		    @Override
		    public void run() {
			statisticTab.populate(result);
		    }
		});
		return null;
	    }

	};
    }

}
