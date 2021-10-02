package de.cmtjk.neelix.view.resources.tabs.requestlist;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.model.resources.Request;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import de.cmtjk.neelix.view.resources.SuccessImage;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class RequestListTab extends Tab {

    private Controller controller;
    private RequestTableView<Request> requestTableView;
    private Button deleteButton;
    private SuccessImage successImage;

    public RequestListTab() throws SystemException {
        super("Anfragen-Log");

        setClosable(false);

        this.setContent(createContent());
        initListenerAndEvents();
    }

    public void populate(List<Request> requestsFromLastSevenDays) {
        requestTableView.setItems(FXCollections.observableArrayList(requestsFromLastSevenDays));
        requestTableView.scrollTo(0);
        requestTableView.getSelectionModel().selectFirst();
    }

    public void setController(Controller controller) throws SystemException {
        if (controller != null) {
            this.controller = controller;
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "setController").set("arg0", "controller=" + controller);
        }
    }

    private VBox createContent() throws SystemException {

        VBox tableVBox = new VBox();
        tableVBox.getStyleClass().addAll("vbox", "center", "grey-background");

        requestTableView = new RequestTableView<>();

        tableVBox.getChildren().addAll(requestTableView, createFooter());

        return tableVBox;
    }

    private HBox createFooter() throws SystemException {

        HBox footerHBox = new HBox();
        footerHBox.getStyleClass().addAll("hbox", "center");

        deleteButton = new Button("Löschen");
        deleteButton.setDisable(true);

        successImage = new SuccessImage();
        successImage.setTransition(2.0);

        footerHBox.getChildren().addAll(deleteButton, successImage);

        return footerHBox;
    }

    private void initListenerAndEvents() {

        requestTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteButton.setDisable(newSelection == null);
        });

        deleteButton.setOnAction(actionEvent -> {
            if (confirmationDialog(requestTableView.getSelectionModel().getSelectedItem())
                    && controller.deleteRequest(requestTableView.getSelectionModel().getSelectedItem())) {
                successImage.play();
            }
            actionEvent.consume();
        });

    }

    private boolean confirmationDialog(Request requestToDelete) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Löschen");
        confirmationDialog.setHeaderText("Folgende Anfrage wird gelöscht: ID " + requestToDelete.getId());
        confirmationDialog.setContentText("Datum: " + requestToDelete.getDate() + " " + requestToDelete.getTime()
                + "\nTyp: " + requestToDelete.getRequestType() + "\nOS: " + requestToDelete.getOperatingSystem()
                + "\nKommentar: " + requestToDelete.getComment() + "\nOrt: " + requestToDelete.getLocation());

        confirmationDialog.setResizable(true);
        confirmationDialog.getDialogPane().setPrefWidth(500);
        confirmationDialog.getDialogPane().setPrefHeight(250);

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        return result.get() == ButtonType.OK;
    }
}
