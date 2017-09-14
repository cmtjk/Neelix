package de.r3r57.neelix.view;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.ExceptionHandler;
import de.r3r57.neelix.model.resources.exception.SystemException;
import de.r3r57.neelix.view.resources.EventHandlerService;
import de.r3r57.neelix.view.resources.UpdateRequestTableService;
import de.r3r57.neelix.view.resources.UpdateStatisticService;
import de.r3r57.neelix.view.resources.tabs.debug.DebugTab;
import de.r3r57.neelix.view.resources.tabs.evaluate.EvaluateTab;
import de.r3r57.neelix.view.resources.tabs.info.InfoTab;
import de.r3r57.neelix.view.resources.tabs.request.RequestTab;
import de.r3r57.neelix.view.resources.tabs.requestlist.RequestListTab;
import de.r3r57.neelix.view.resources.tabs.statistic.StatisticTab;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class View extends Stage {

    private final String VERSION;

    private boolean infoTabHighlight = false;

    private final Model model;
    private Controller controller;

    private final ExceptionHandler eh = ExceptionHandler.getInstance();

    private RequestTab requestTab;
    private DebugTab debugTab;
    private RequestListTab requestListTab;
    private StatisticTab statisticTab;
    private InfoTab releaseNotesTab;

    private final ProgressBar progressBar = new ProgressBar();

    private TabPane mainTabPane;

    private EvaluateTab evaluateTab;

    private UpdateStatisticService updateStatisticService;
    private UpdateRequestTableService updateRequestTableService;

    public View(Model model) throws SystemException, IOException {
	if (model != null) {

	    this.model = model;
	    this.VERSION = this.model.getVersion();

	    setFrameOptions();

	    Pane rootPane = createContentPane();
	    initListenerAndEvents();
	    initBindings();

	    initServices();

	    Scene scene = new Scene(rootPane, 600, 400);
	    scene.getStylesheets().add(Paths.get("./config/neelix.css").toAbsolutePath().toUri().toString());

	    this.setScene(scene);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "model=" + model);
	}
    }

    private void initServices() {

	updateStatisticService = new UpdateStatisticService(model, statisticTab);
	updateStatisticService.setOnRunning(EventHandlerService.getInstance().getRunning());
	updateStatisticService.setOnSucceeded(EventHandlerService.getInstance().getSucceed());
	updateStatisticService.setOnFailed(EventHandlerService.getInstance().getFailed());

	updateRequestTableService = new UpdateRequestTableService(model, requestListTab);
	updateRequestTableService.setOnRunning(EventHandlerService.getInstance().getRunning());
	updateRequestTableService.setOnSucceeded(EventHandlerService.getInstance().getSucceed());
	updateRequestTableService.setOnFailed(EventHandlerService.getInstance().getFailed());

    }

    public void setController(Controller controller) throws SystemException {
	if (controller != null) {
	    this.controller = controller;
	    requestTab.setController(this.controller);
	    requestListTab.setController(this.controller);
	    evaluateTab.setController(this.controller);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setController").set("arg0", "controller=" + controller);
	}
    }

    public void setDescription(String description) throws SystemException {
	if (description != null) {
	    requestTab.setDescription(description);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "description=" + description);
	}
    }

    public void disableOSButtons() {
	requestTab.disableOSButtons();
    }

    public void enableOSButtons() {
	requestTab.enableOSButtons();
    }

    public void setDate(LocalDate date) throws SystemException {
	if (date != null) {
	    requestTab.setDate(date);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass()).set("method", "setDate")
		    .set("arg0", "date=" + date);
	}
    }

    public void setTime(LocalTime time) throws SystemException {
	if (time != null) {
	    requestTab.setTime(time);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass()).set("method", "setTime")
		    .set("arg0", "time=" + time);
	}
    }

    public void setDateButtonState(boolean bool) {
	requestTab.setDateButtonState(bool);
    }

    public void setQuantity(int quantity) {
	requestTab.setQuantity(quantity);
    }

    private VBox createContentPane() throws SystemException, IOException {

	VBox mainVBox = new VBox();
	mainVBox.setPrefWidth(600);
	mainTabPane = new TabPane();

	requestTab = new RequestTab(model);
	debugTab = new DebugTab(model);
	statisticTab = new StatisticTab();
	evaluateTab = new EvaluateTab();
	requestListTab = new RequestListTab();
	releaseNotesTab = new InfoTab();

	if (model.newVersionAvailable()) {
	    highlightInfoTab();
	}

	mainTabPane.getTabs().addAll(requestTab, requestListTab, statisticTab, evaluateTab, debugTab, releaseNotesTab);
	mainVBox.getChildren().addAll(mainTabPane, createStatusBar());

	return mainVBox;
    }

    private HBox createStatusBar() {

	HBox statusBar = new HBox();
	statusBar.setPadding(new Insets(2, 2, 2, 2));
	statusBar.getStyleClass().addAll("statusbar", "lightgrey-background");
	statusBar.setAlignment(Pos.CENTER_RIGHT);
	statusBar.getChildren().add(progressBar);

	progressBar.setVisible(false);

	EventHandlerService.getInstance().setProgressIndicator(progressBar);

	return statusBar;
    }

    private void updateDebugTab() throws IOException, SystemException {
	debugTab.updateDebugTab();
    }

    public void updateRequestTableTab() {
	updateRequestTableService.restart();
    }

    public void switchToDebugTab() {
	mainTabPane.getSelectionModel().select(debugTab);
    }

    public void resetFields() {
	requestTab.resetFields();
    }

    private void highlightInfoTab() {

	// Updater hier ---------------

	VBox vb = new VBox(20);
	vb.setPadding(new Insets(10, 10, 10, 10));
	vb.setAlignment(Pos.CENTER);
	Button update = new Button("Update (Daumen drücken!)");
	vb.getChildren().add(update);
	releaseNotesTab.setContent(vb);

	update.setOnAction(actionEvent -> {
	    try {
		Runtime.getRuntime().exec("cmd /c start update.bat");
		Platform.exit();
	    } catch (IOException e) {
		ExceptionHandler.getInstance().print(new SystemException(e.getMessage(), e, ErrorCode.FILE_EXCEPTION)
			.set("class", this.getClass()).set("method", "highlightInfoTab"));
	    }
	});

	// ----------------------

	infoTabHighlight = true;
	releaseNotesTab.getStyleClass().add("releasetab");
	releaseNotesTab.setText(model.getLatestVersion());

	ImageView rockImage = new ImageView();
	rockImage.setImage(new Image(Paths.get("./config/hand.png").toAbsolutePath().toUri().toString()));
	rockImage.setFitWidth(20);
	rockImage.setFitHeight(20);
	rockImage.setPreserveRatio(true);
	rockImage.setSmooth(true);
	rockImage.setCache(true);

	releaseNotesTab.setGraphic(rockImage);

	TranslateTransition translateTransition = new TranslateTransition(Duration.millis(250), rockImage);
	translateTransition.setFromX(0);
	translateTransition.setToX(-10);
	translateTransition.setCycleCount(40);
	translateTransition.setAutoReverse(true);

	translateTransition.play();

    }

    private void initListenerAndEvents() {

	mainTabPane.getSelectionModel().selectedItemProperty()
		.addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
		    if (debugTab.equals(newValue)) {
			try {
			    updateDebugTab();
			} catch (Exception e) {
			    eh.print(new SystemException(e.getMessage(), e, ErrorCode.IO_EXCEPTION)
				    .set("class", this.getClass()).set("method", "initListenerAndEvents"));
			}
		    }
		    if (statisticTab.equals(newValue)) {
			updateStatisticService.restart();
		    }
		    if (requestListTab.equals(newValue)) {
			updateRequestTableService.restart();
		    }
		    if (infoTabHighlight && releaseNotesTab.equals(newValue)) {
			infoTabHighlight = false;
			releaseNotesTab.setText("Info");
			releaseNotesTab.setGraphic(null);
			releaseNotesTab.getStyleClass().remove(1);
		    }
		});

    }

    private void initBindings() {

	// TODO forget it
    }

    private void setFrameOptions() {
	this.setResizable(false);
	this.setTitle("Neelix " + VERSION);
	this.getIcons().add(new Image(Paths.get("./config/icon.png").toAbsolutePath().toUri().toString()));
    }

}
