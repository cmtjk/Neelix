package de.r3r57.neelix.view;

import java.nio.file.Paths;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QuantityDialog extends Stage {

    private final View view;
    private final Controller controller;
    private Button continueButton;
    private TextField quantityTextField;

    public QuantityDialog(View view, Controller controller) throws SystemException {
	if (view != null && controller != null) {
	    this.view = view;
	    this.controller = controller;

	    setFrameOptions();

	    Pane rootPane = createContentPane();

	    initListenerAndEvents();

	    Scene scene = new Scene(rootPane, 300, 100);
	    scene.getStylesheets().add(Paths.get("./config/neelix.css").toAbsolutePath().toUri().toString());

	    this.setScene(scene);

	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "controller=" + controller).set("arg1", "view=" + view);

	}
    }

    private VBox createContentPane() {

	VBox mainVBox = new VBox();
	mainVBox.getStyleClass().addAll("vbox", "center", "grey-background");

	continueButton = new Button("Weiter");

	mainVBox.getChildren().addAll(createQuantityControl(), new Separator(), continueButton);

	return mainVBox;
    }

    private HBox createQuantityControl() {

	HBox quantityControlHBox = new HBox();
	quantityControlHBox.getStyleClass().addAll("hbox", "center");

	Label quantityLabel = new Label("/ 20");
	quantityLabel.getStyleClass().add("white-text");
	quantityTextField = new TextField("1");
	quantityTextField.getStyleClass().add("quantity");

	quantityControlHBox.getChildren().addAll(quantityTextField, quantityLabel);

	return quantityControlHBox;

    }

    private boolean isValidNumber(String string) {
	try {
	    int quantity = Integer.parseInt(string);
	    return quantity <= 20 && quantity > 0;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    private void initListenerAndEvents() {

	continueButton.setOnAction(actionEvent -> {
	    try {
		controller.setQuantity(Integer.parseInt(quantityTextField.getText()));
		this.close();
	    } catch (NumberFormatException e) {
		controller.setQuantity(1);
	    }
	    actionEvent.consume();
	});

	quantityTextField.textProperty()
		.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    if (!isValidNumber(newValue)) {
			continueButton.setDisable(true);
		    } else {
			continueButton.setDisable(false);
		    }
		});

    }

    private void setFrameOptions() {
	this.setTitle("Anzahl wählen");
	this.getIcons().add(new Image(Paths.get("./config/icon.png").toAbsolutePath().toUri().toString()));
	initModality(Modality.WINDOW_MODAL);
	initOwner(view.getScene().getWindow());
    }

}
