package de.cmtjk.neelix.view.resources;

import de.cmtjk.neelix.model.Model;
import de.cmtjk.neelix.view.resources.tabs.statistic.StatisticTab;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateStatisticService extends Service<Void> {

    private final StatisticTab statisticTab;
    private final Model model;

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
