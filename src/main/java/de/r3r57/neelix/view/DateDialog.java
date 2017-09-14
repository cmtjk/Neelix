package de.r3r57.neelix.view;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DateDialog extends Stage {

    // private final Model model;
    private final View view;
    private final Controller controller;
    private Button continueButton;
    private DatePicker datePicker;
    private ToggleGroup dayTimeGroup;
    private ToggleButton morning;
    private ToggleButton afternoon;
    private ToggleButton currentDateTime;
    private boolean useCurrentDateTime;

    public DateDialog(View view, Model model, Controller controller) throws SystemException {
	if (view != null && model != null && controller != null) {
	    // this.model = model;
	    this.view = view;
	    this.controller = controller;

	    useCurrentDateTime = true;

	    setFrameOptions();

	    Pane rootPane = createContentPane();

	    initListenerAndEvents();

	    Scene scene = new Scene(rootPane, 300, 150);
	    scene.getStylesheets().add(Paths.get("./config/neelix.css").toAbsolutePath().toUri().toString());

	    this.setScene(scene);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "view=" + view).set("arg1", "model=" + model)
		    .set("arg2", "controller=" + controller);
	}

    }

    private VBox createContentPane() {

	VBox mainVBox = new VBox();
	mainVBox.getStyleClass().addAll("vbox", "center", "grey-background");

	datePicker = new DatePicker(LocalDate.now());
	datePicker.setDisable(true);
	continueButton = new Button("Weiter");

	mainVBox.getChildren().addAll(createDayTimeButtons(), datePicker, new Separator(), continueButton);

	return mainVBox;
    }

    private HBox createDayTimeButtons() {

	HBox dayTimeHBox = new HBox();
	dayTimeHBox.getStyleClass().add("center");

	morning = new ToggleButton("vormittags");
	afternoon = new ToggleButton("nachmittags");

	currentDateTime = new ToggleButton("Jetzt");
	currentDateTime.setSelected(true);

	currentDateTime.getStyleClass().add("left-pill");
	morning.getStyleClass().add("center-pill");
	afternoon.getStyleClass().add("right-pill");

	dayTimeGroup = new ToggleGroup();

	morning.setToggleGroup(dayTimeGroup);
	afternoon.setToggleGroup(dayTimeGroup);
	currentDateTime.setToggleGroup(dayTimeGroup);

	dayTimeHBox.getChildren().addAll(currentDateTime, morning, afternoon);

	return dayTimeHBox;

    }

    private LocalTime getTime() {
	if (dayTimeGroup.getSelectedToggle() != null) {
	    if (morning.isSelected()) {
		return LocalTime.of(8, 1);
	    }
	    if (afternoon.isSelected()) {
		return LocalTime.of(13, 1);
	    }
	}
	return LocalTime.now();
    }

    private void initListenerAndEvents() {

	continueButton.setOnAction(actionEvent -> {

	    if (!useCurrentDateTime) {
		controller.setDate(datePicker.getValue());
		controller.setTime(getTime());
		controller.setUseCurrentDateTime(useCurrentDateTime);
	    } else {
		controller.setDate(LocalDate.now());
		controller.setUseCurrentDateTime(useCurrentDateTime);
	    }

	    this.close();
	    actionEvent.consume();
	});

	currentDateTime.selectedProperty().addListener(listener -> {
	    if (currentDateTime.isSelected()) {
		datePicker.setDisable(true);
		useCurrentDateTime = true;

	    } else {
		datePicker.setDisable(false);
		useCurrentDateTime = false;
	    }
	});
    }

    private void setFrameOptions() {
	this.setTitle("Datum wählen");
	this.getIcons().add(new Image(Paths.get("./config/icon.png").toAbsolutePath().toUri().toString()));
	initModality(Modality.WINDOW_MODAL);
	initOwner(view.getScene().getWindow());
    }

}
