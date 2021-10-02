package de.cmtjk.neelix.model.resources.exception;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExceptionAlert extends Alert {

    public ExceptionAlert() {
        super(AlertType.ERROR);
    }

    public void showAlert(SystemException ex, String exceptionText) {

        this.setTitle("Error");
        this.setHeaderText(ex.getErrorCode().getDescription() + " (" + ex.getErrorCode().getCode() + ")");

        Label label = new Label("The exception stacktrace is:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setMinHeight(300);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(label, 0, 0);
        content.add(textArea, 0, 1);

        this.getDialogPane().setContent(content);
        this.setResizable(true);

        this.showAndWait();
    }
}
