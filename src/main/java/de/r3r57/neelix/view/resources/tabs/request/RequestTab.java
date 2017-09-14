package de.r3r57.neelix.view.resources.tabs.request;

import java.time.LocalDate;
import java.time.LocalTime;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.RequestType;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RequestTab extends Tab {

    private Model model;
    private Controller controller;
    private TextArea descriptionTextArea;
    private RequestTypeChooser requestTypes;
    private OperatingSystemChooser osToggleButtons;
    private FooterButtons buttonsHBox;

    public RequestTab(Model model) throws SystemException {
	super("Aufnehmen");
	if (model != null) {
	    setClosable(false);

	    this.model = model;

	    createContent();
	    initListenerAndEvents();
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "model=" + model);
	}
    }

    public void setController(Controller controller) throws SystemException {
	if (controller != null) {
	    this.controller = controller;
	    requestTypes.setController(controller);
	    osToggleButtons.setController(controller);
	    buttonsHBox.setController(controller);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setController").set("arg0", "controller=" + controller);
	}
    }

    public void setDescription(String description) throws SystemException {
	if (description != null) {
	    descriptionTextArea.setPromptText(description);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setDescription").set("arg0", "description=" + description);
	}
    }

    public void disableOSButtons() {
	osToggleButtons.disableOSButtons();
    }

    public void enableOSButtons() {
	osToggleButtons.enableOSButtons();
    }

    public void setDate(LocalDate date) throws SystemException {
	buttonsHBox.setDate(date);
    }

    public void setTime(LocalTime time) {
	buttonsHBox.setTime(time);
    }

    public void setDateButtonState(boolean bool) {
	buttonsHBox.setDateButtonsState(bool);

    }

    public void setQuantity(int quantity) {
	buttonsHBox.setQuantity(quantity);
    }

    public void resetFields() {
	requestTypes.reset();
	osToggleButtons.reset();
	descriptionTextArea.clear();
	controller.setTime(LocalTime.now());
	controller.setDate(LocalDate.now());
	controller.setUseCurrentDateTime(true);
	controller.setQuantity(1);

    }

    private void createContent() throws SystemException {

	VBox mainVBox = new VBox(10);
	mainVBox.getStyleClass().addAll("vbox", "center", "grey-background");

	mainVBox.getChildren().addAll(createRequestTypesChoiceBox(), createOSToggleButton(),
		createDescriptionTextArea(), createFooterButtons());

	this.setContent(mainVBox);

    }

    private ComboBox<RequestType> createRequestTypesChoiceBox() throws SystemException {

	requestTypes = new RequestTypeChooser(model);

	return requestTypes;
    }

    private HBox createOSToggleButton() {

	osToggleButtons = new OperatingSystemChooser();

	return osToggleButtons;
    }

    private TextArea createDescriptionTextArea() {

	descriptionTextArea = new TextArea();
	descriptionTextArea.setWrapText(true);

	return descriptionTextArea;
    }

    private HBox createFooterButtons() throws SystemException {

	buttonsHBox = new FooterButtons();

	return buttonsHBox;
    }

    private void initListenerAndEvents() {

	descriptionTextArea.textProperty()
		.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    controller.setComment(newValue);
		});

    }
}
