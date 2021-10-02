package de.cmtjk.neelix.view.resources.tabs.statistic;

import de.cmtjk.neelix.model.resources.exception.SystemException;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StatisticTab extends Tab {

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.");

    private int highestValue = 0;

    private CustomBarChart<String, Number> chart;
    private XYChart.Series<String, Number> series;

    public StatisticTab() throws SystemException {
        super("Statistik");
        setClosable(false);

        this.setContent(createContent());
        initListenerAndEvents();

    }

    private VBox createContent() throws SystemException {

        VBox statisticVBox = new VBox();
        statisticVBox.getStyleClass().addAll("vbox", "center", "grey-background");

        statisticVBox.getChildren().add(createChart());

        return statisticVBox;
    }

    private BarChart<String, Number> createChart() throws SystemException {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new CustomBarChart<>(xAxis, yAxis);

        xAxis.setLabel("Datum");
        yAxis.setLabel("Anfragen");

        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setVerticalGridLinesVisible(false);

        series = new XYChart.Series<>();
        series.setName("Anfragen");

        chart.getData().add(series);

        return chart;
    }

    public void populate(int[] statistic) {

        highestValue = 0;

        LocalDate now = LocalDate.now();

        series.getData().clear();
        for (int i = statistic.length - 1; i >= 0; i--) {
            series.getData().add(new XYChart.Data<>((df.format(now.minusDays(i))), statistic[i]));
            highestValue = (statistic[i] > highestValue) ? statistic[i] : highestValue;
        }

        colorize();

        chart.getData().clear();
        chart.getData().add(series);

    }

    private void colorize() {

        series.getData().forEach(data -> {
            double relValue = data.getYValue().intValue() / (double) highestValue;
            if (relValue > 0.75) {
                data.getNode().setStyle("-fx-bar-fill: rgba(0, 153, 204, 1);");
            } else if (relValue > 0.5) {
                data.getNode().setStyle("-fx-bar-fill: rgba(0, 153, 204, 0.8);");
            } else if (relValue > 0.25) {
                data.getNode().setStyle("-fx-bar-fill: rgba(0, 153, 204, 0.6);");
            } else {
                data.getNode().setStyle("-fx-bar-fill: rgba(0, 153, 204, 0.4);");
            }
        });

    }

    private void initListenerAndEvents() {

    }

    private class CustomBarChart<X, Y> extends BarChart<X, Y> {

        Map<Node, TextFlow> nodeMap = new HashMap<>();

        public CustomBarChart(Axis<X> xAxis, Axis<Y> yAxis) {
            super(xAxis, yAxis);
            this.setBarGap(0.0);
        }

        @Override
        protected void seriesAdded(Series<X, Y> series, int seriesIndex) {

            super.seriesAdded(series, seriesIndex);

            for (int j = 0; j < series.getData().size(); j++) {

                Data<X, Y> item = series.getData().get(j);

                Text text = new Text(item.getYValue().toString());
                text.setStyle("-fx-font-size: 10pt;");

                TextFlow textFlow = new TextFlow(text);
                textFlow.setTextAlignment(TextAlignment.CENTER);

                nodeMap.put(item.getNode(), textFlow);
                this.getPlotChildren().add(textFlow);

            }

        }

        @Override
        protected void seriesRemoved(final Series<X, Y> series) {

            for (Node bar : nodeMap.keySet()) {

                Node text = nodeMap.get(bar);
                this.getPlotChildren().remove(text);

            }

            this.nodeMap.clear();
            super.seriesRemoved(series);
        }

        @Override
        protected void layoutPlotChildren() {

            super.layoutPlotChildren();

            for (Node bar : nodeMap.keySet()) {

                TextFlow textFlow = nodeMap.get(bar);

                if (bar.getBoundsInParent().getHeight() > 30) {
                    ((Text) textFlow.getChildren().get(0)).setFill(Color.WHITE);
                    textFlow.resize(bar.getBoundsInParent().getWidth(), 200);
                    textFlow.relocate(bar.getBoundsInParent().getMinX(), bar.getBoundsInParent().getMinY() + 10);
                } else {
                    ((Text) textFlow.getChildren().get(0)).setFill(Color.GRAY);
                    textFlow.resize(bar.getBoundsInParent().getWidth(), 200);
                    textFlow.relocate(bar.getBoundsInParent().getMinX(), bar.getBoundsInParent().getMinY() - 20);
                }
            }

        }
    }

}
