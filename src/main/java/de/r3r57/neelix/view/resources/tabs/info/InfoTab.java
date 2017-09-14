package de.r3r57.neelix.view.resources.tabs.info;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class InfoTab extends Tab {

    private final Path docPath = Paths.get("./doc/release-notes.html");

    public InfoTab() {
	super("Info");

	setClosable(false);

	createContent();
    }

    private void createContent() {

	VBox debugVBox = new VBox();
	debugVBox.getStyleClass().addAll("vbox", "center", "grey-background");

	WebView browser = new WebView();
	WebEngine webEngine = browser.getEngine();
	webEngine.load(docPath.toAbsolutePath().toUri().toString());

	debugVBox.getChildren().add(browser);

	this.setContent(debugVBox);
    }

}
