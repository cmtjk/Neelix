package de.cmtjk.neelix.view.resources.tabs.request;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.model.Model;
import de.cmtjk.neelix.model.resources.RequestType;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class RequestTypeChooser extends ComboBox<RequestType> {

    private Controller controller;
    private final Model model;

    public RequestTypeChooser(Model model) throws SystemException {
        super();
        if (model != null) {

            this.model = model;

            this.setPrefWidth(Double.MAX_VALUE);
            this.setVisibleRowCount(Integer.MAX_VALUE);

            populate();
            initListenerAndEvents();
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "constructor").set("arg0", "model=" + model);
        }
    }

    public void setController(Controller controller) throws SystemException {
        if (controller != null) {
            this.controller = controller;

            controller.setRequestType(this.getSelectionModel().getSelectedItem());

            if (this.getSelectionModel().getSelectedItem().isIndependent()) {
                controller.disableOSButtons();
            }
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "setController").set("arg0", "controller=" + controller);
        }
    }

    public void reset() {
        this.getSelectionModel().selectFirst();
    }

    private void populate() {

        this.setItems(FXCollections.observableArrayList(model.getRequestTypes()));
        addTooltips();
        this.getSelectionModel().selectFirst();

    }

    private void addTooltips() {

        this.setCellFactory(cell -> {
            return new ListCell<RequestType>() {

                @Override
                public void updateItem(RequestType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.getName());
                        Tooltip tt = new Tooltip();
                        tt.setWrapText(true);
                        tt.setPrefWidth(500);
                        tt.setText(item.getDescription());
                        tt.setShowDelay(new Duration(250));
                        setTooltip(tt);
                    }
                }

            };
        });
    }

    private void initListenerAndEvents() {

        this.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends RequestType> observable, RequestType oldValue, RequestType newValue) -> {
                    controller.setRequestType(newValue);
                    if (newValue.isIndependent()) {
                        controller.disableOSButtons();
                    } else {
                        controller.enableOSButtons();
                    }
                });

    }

}
