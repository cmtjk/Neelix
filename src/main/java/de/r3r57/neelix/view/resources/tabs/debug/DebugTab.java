package de.r3r57.neelix.view.resources.tabs.debug;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class DebugTab extends Tab {

    private final List<String> logMessageStyle = Arrays.asList("info", "debug", "warning", "error", "report", "fatal",
	    "trace");
    private final Model model;
    private ListView<String> debugListView;

    public DebugTab(Model model) throws SystemException, IOException {
	super("Debug");

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

    public void updateDebugTab() throws IOException, SystemException {
	debugListView.setItems(FXCollections.observableArrayList(model.getLogContent()));
	debugListView.scrollTo(debugListView.getItems().size());

    }

    private void createContent() throws IOException, SystemException {

	VBox debugVBox = new VBox();
	debugVBox.getStyleClass().addAll("vbox", "center", "grey-background");

	debugListView = new ListView<>();
	debugListView.setEditable(false);

	updateDebugTab();

	debugVBox.getChildren().add(debugListView);

	this.setContent(debugVBox);

    }

    private void initListenerAndEvents() {

	debugListView.setCellFactory(listCell -> new ListCell<String>() {
	    @Override
	    public void updateItem(String content, boolean isEmpty) {
		super.updateItem(content, isEmpty);
		getStyleClass().removeAll(logMessageStyle);
		if (isEmpty || content == null) {
		    setText("");
		    getStyleClass().add("debug");
		} else {
		    setText(content);
		    if (content.contains("INFO")) {
			getStyleClass().addAll("info", "debug");
		    } else if (content.contains("TRACE")) {
			getStyleClass().addAll("trace", "debug");
		    } else if (content.contains("WARN")) {
			getStyleClass().addAll("warning", "debug");
		    } else if (content.contains("ERROR")) {
			getStyleClass().addAll("error", "debug");
		    } else if (content.contains("DEBUG")) {
			getStyleClass().addAll("report", "debug");
		    } else if (content.contains("FATAL")) {
			getStyleClass().addAll("fatal", "debug");
		    } else {
			getStyleClass().addAll("trace", "debug");
		    }
		}
	    }
	});

    }

}
