package de.cmtjk.neelix.view.resources.tabs.evaluate;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.model.resources.exception.ExceptionHandler;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import de.cmtjk.neelix.view.resources.EventHandlerService;
import de.cmtjk.neelix.view.resources.SuccessImage;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class EvaluateTab extends Tab {

    private final String CSVALL = ".csv (alle Einträge, roh)";
    private final String CSVRTMO = ".csv (Summe Anfragetyp/Monat)";
    private final String HTML = ".html (visuell aufbereitet)";
    private final String DESCRIPTION = "Wertet die Anfragen aus und erstellt entsprechende Dateien im Home-Verzeichnis */html/ und */csv/.";
    private final EvaluateRequestsService evaluateRequestsService = new EvaluateRequestsService(CSVALL, CSVRTMO, HTML);
    private Button evaluateButton;
    private ComboBox<String> fileType;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private SuccessImage successImage;

    public EvaluateTab() throws SystemException {
        super("Auswerten");
        setClosable(false);

        this.setContent(createContent());
        initListenerAndEvents();

        initServices();
    }

    public void setController(Controller controller) {
        evaluateRequestsService.setController(controller);
    }

    private VBox createContent() throws SystemException {

        VBox evaluateVBox = new VBox();
        evaluateVBox.getStyleClass().addAll("vbox", "center", "grey-background");

        evaluateVBox.getChildren().addAll(createDescription(), createLabel(), createDatePicker(), new Separator(),
                createEvaluateButton());

        return evaluateVBox;
    }

    private HBox createEvaluateButton() throws SystemException {

        HBox evaluateButtonHBox = new HBox();
        evaluateButtonHBox.getStyleClass().addAll("hbox", "center");

        evaluateButton = new Button("Starten");

        fileType = new ComboBox<>(FXCollections.observableArrayList(CSVRTMO, CSVALL, HTML));
        fileType.getSelectionModel().selectFirst();

        // TODO
        fileType.setStyle("-fx-font-size: 10pt");

        successImage = new SuccessImage();
        successImage.setTransition(2.0);

        evaluateButtonHBox.getChildren().addAll(fileType, evaluateButton, successImage);

        return evaluateButtonHBox;

    }

    private HBox createDescription() {

        HBox descriptionHBox = new HBox();
        descriptionHBox.getStyleClass().addAll("hbox", "center");

        Label description = new Label();
        description.setWrapText(true);
        description.getStyleClass().add("white-text");
        description.setText(DESCRIPTION);
        descriptionHBox.getChildren().add(description);

        return descriptionHBox;
    }

    private HBox createLabel() {

        HBox labelHBox = new HBox();
        labelHBox.getStyleClass().addAll("hbox", "center", "text-bold");

        Label beginLabel = new Label("Von...");
        Label endLabel = new Label("...bis");
        beginLabel.getStyleClass().add("white-text");
        endLabel.getStyleClass().add("white-text");

        labelHBox.getChildren().addAll(beginLabel, endLabel);

        return labelHBox;

    }

    private HBox createDatePicker() {

        HBox dateHBox = new HBox();
        dateHBox.getStyleClass().addAll("hbox", "center");

        fromDatePicker = new DatePicker(LocalDate.now());
        toDatePicker = new DatePicker(LocalDate.now());

        dateHBox.getChildren().addAll(fromDatePicker, toDatePicker);

        return dateHBox;
    }

    private void initServices() {
        evaluateRequestsService.setOnFailed(EventHandlerService.getInstance().getFailed());
        evaluateRequestsService.setOnRunning(EventHandlerService.getInstance().getRunning());
        evaluateRequestsService.setOnSucceeded(EventHandlerService.getInstance().getSucceed());
    }

    private void initListenerAndEvents() {
        evaluateButton.setOnAction(actionEvent -> {
            try {
                evaluateRequestsService.evaluate(fileType.getValue(), fromDatePicker.getValue(),
                        toDatePicker.getValue());
            } catch (SystemException e) {
                ExceptionHandler.getInstance().print(e);
            }
            actionEvent.consume();
        });

        fromDatePicker.valueProperty().addListener(listener -> {
            if (toDatePicker.getValue().isBefore(fromDatePicker.getValue())) {
                fromDatePicker.setStyle("-fx-background-color:red; -fx-color:red");
                evaluateButton.setDisable(true);
            } else {
                toDatePicker.setStyle("");
                fromDatePicker.setStyle("");
                evaluateButton.setDisable(false);
            }
        });

        toDatePicker.valueProperty().addListener(listener -> {
            if (toDatePicker.getValue().isBefore(fromDatePicker.getValue())) {
                toDatePicker.setStyle("-fx-background-color:red; -fx-color:red");
                evaluateButton.setDisable(true);
            } else {
                toDatePicker.setStyle("");
                fromDatePicker.setStyle("");
                evaluateButton.setDisable(false);
            }
        });

    }

}
