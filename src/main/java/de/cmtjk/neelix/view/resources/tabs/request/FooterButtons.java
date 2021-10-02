package de.cmtjk.neelix.view.resources.tabs.request;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.model.resources.Location;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.ExceptionHandler;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import de.cmtjk.neelix.view.resources.EventHandlerService;
import de.cmtjk.neelix.view.resources.SuccessImage;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FooterButtons extends HBox {

    private final SuccessImage successImage;
    private final DateTimeFormatter dformatter;
    private final SaveRequestTaskService saveRequestTaskService = new SaveRequestTaskService();
    private Controller controller;
    private Button dateButton;
    private ToggleButton rzButton, tb4Button, otrsTelButton;
    private Button saveButton;
    private Button quantityButton;
    private ToggleGroup locationGroup;

    public FooterButtons() throws SystemException {
        super();
        this.getStyleClass().addAll("hbox", "center");

        successImage = new SuccessImage();
        successImage.setTransition(2.0);

        dformatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");

        this.getChildren().addAll(createButtons());
        this.getChildren().add(successImage);

        initListenerAndEvents();
    }

    public void setController(Controller controller) throws SystemException {
        if (controller != null) {
            this.controller = controller;
            saveRequestTaskService.initTask(controller);
            saveRequestTaskService.getService().setOnFailed(EventHandlerService.getInstance().getFailed());
            saveRequestTaskService.getService().setOnRunning(EventHandlerService.getInstance().getRunning());
            saveRequestTaskService.getService().setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                @Override
                public void handle(WorkerStateEvent event) {
                    EventHandlerService.getInstance().getProgressIndicator().setProgress(100.0);
                    EventHandlerService.getInstance().getProgressIndicator().setVisible(false);
                    successImage.play();
                    event.consume();

                }

            });
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "setController").set("arg0", "controller=" + controller);
        }
    }

    public void setDate(LocalDate date) throws SystemException {
        if (date != null) {
            dateButton.setText("" + date.format(dformatter));
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass()).set("method", "setDate")
                    .set("arg0", "date=" + date);
        }
    }

    public void setTime(LocalTime time) {
        // Not necessary anymore?
    }

    public void setDateButtonsState(boolean bool) {

        dateButton.getStyleClass().remove("neutral");
        dateButton.getStyleClass().remove("warning");

        if (!bool) {
            dateButton.getStyleClass().add("warning");
        } else {
            dateButton.getStyleClass().add("neutral");
        }
    }

    public void setQuantity(int quantity) {

        quantityButton.setText("Anzahl: " + quantity);

        if (quantity > 1) {
            quantityButton.getStyleClass().remove("neutral");
            quantityButton.getStyleClass().add("warning");
        } else {
            quantityButton.getStyleClass().remove("warning");
            quantityButton.getStyleClass().add("neutral");
        }
    }

    public void reset() throws SystemException {
        controller.setDate(LocalDate.now());
        controller.setQuantity(1);
        controller.setTime(LocalTime.now());
        controller.setUseCurrentDateTime(true);
    }

    private HBox createButtons() {

        HBox buttonsHBox = new HBox(10);

        dateButton = new Button("" + LocalDate.now().format(dformatter));
        quantityButton = new Button("Anzahl: 1");
        saveButton = new Button("Speichern");

        rzButton = new ToggleButton("RZ");
        tb4Button = new ToggleButton("TB:lokal");
        otrsTelButton = new ToggleButton("TB:tel/OTRS");

        locationGroup = new ToggleGroup();

        rzButton.setToggleGroup(locationGroup);
        tb4Button.setToggleGroup(locationGroup);
        otrsTelButton.setToggleGroup(locationGroup);

        rzButton.setSelected(true);

        rzButton.getStyleClass().addAll("left-pill", "footer");
        tb4Button.getStyleClass().addAll("center-pill", "footer");
        otrsTelButton.getStyleClass().addAll("right-pill", "footer");

        dateButton.getStyleClass().addAll("neutral", "footer");
        quantityButton.getStyleClass().addAll("neutral", "footer");

        HBox locationHBox = new HBox(0);
        locationHBox.getChildren().addAll(rzButton, tb4Button, otrsTelButton);

        buttonsHBox.getChildren().addAll(dateButton, locationHBox, quantityButton, saveButton);

        return buttonsHBox;

    }

    private void initListenerAndEvents() {

        dateButton.setOnAction(actionEvent -> {
            controller.showDateDialog();
            actionEvent.consume();
        });

        locationGroup.selectedToggleProperty().addListener(changeListener -> {
            if (rzButton.isSelected()) {
                controller.setLocation(Location.RZ);
            } else if (tb4Button.isSelected()) {
                controller.setLocation(Location.TB4LOCAL);
            } else if (otrsTelButton.isSelected()) {
                controller.setLocation(Location.TB4TELOTRS);
            }
        });

        quantityButton.setOnAction(actionEvent -> {
            controller.showQuantityDialog();
            actionEvent.consume();
        });

        saveButton.setOnAction(actionEvent -> {
            try {
                saveRequestTaskService.getService().restart();
            } catch (SystemException e) {
                ExceptionHandler.getInstance().print(e);
            }
            actionEvent.consume();
        });

    }
}
