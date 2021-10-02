package de.cmtjk.neelix.view.resources.tabs.request;

import de.cmtjk.neelix.controller.Controller;
import de.cmtjk.neelix.model.resources.OperatingSystem;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class OperatingSystemChooser extends HBox {

    Controller controller;
    private ToggleGroup osToggleGroup;
    private ToggleButton none;
    private ToggleButton windows;
    private ToggleButton macos;
    private ToggleButton linux;
    private ToggleButton android;
    private ToggleButton other;

    public OperatingSystemChooser() {
        super();
        this.getStyleClass().add("center");

        this.getChildren().addAll(createToggleButtons());

        initListenerAndEvents();
    }

    public void setController(Controller controller) throws SystemException {
        if (controller != null) {
            this.controller = controller;
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "setController").set("arg0", "controller=" + controller);
        }
    }

    public void disableOSButtons() {
        osToggleGroup.selectToggle(none);
        this.setDisable(true);
    }

    public void enableOSButtons() {
        this.setDisable(false);
    }

    private List<ToggleButton> createToggleButtons() {

        List<ToggleButton> toggleButtonList = new ArrayList<>();

        none = new ToggleButton("Unabhängig");
        windows = new ToggleButton("Windows");
        macos = new ToggleButton("Mac OS/iOS");
        linux = new ToggleButton("Linux");
        android = new ToggleButton("Android");
        other = new ToggleButton("Andere");

        toggleButtonList.add(none);
        toggleButtonList.add(windows);
        toggleButtonList.add(macos);
        toggleButtonList.add(linux);
        toggleButtonList.add(android);
        toggleButtonList.add(other);

        none.getStyleClass().add("left-pill");
        windows.getStyleClass().add("center-pill");
        macos.getStyleClass().add("center-pill");
        linux.getStyleClass().add("center-pill");
        android.getStyleClass().add("center-pill");
        other.getStyleClass().add("right-pill");

        none.setSelected(true);

        osToggleGroup = new ToggleGroup();

        toggleButtonList.forEach(toggleButton -> toggleButton.setToggleGroup(osToggleGroup));

        return toggleButtonList;

    }

    public void reset() {
        osToggleGroup.selectToggle(none);
    }

    private OperatingSystem getSelectedOS() {
        if (none.isSelected()) {
            return OperatingSystem.NONE;
        }
        if (windows.isSelected()) {
            return OperatingSystem.WINDOWS;
        }
        if (macos.isSelected()) {
            return OperatingSystem.MACOS;
        }
        if (linux.isSelected()) {
            return OperatingSystem.LINUX;
        }
        if (android.isSelected()) {
            return OperatingSystem.ANDROID;
        }
        if (other.isSelected()) {
            return OperatingSystem.OTHER;
        }
        return OperatingSystem.NONE;
    }

    private void initListenerAndEvents() {

        osToggleGroup.selectedToggleProperty()
                .addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
                    if (newValue == null) {
                        osToggleGroup.selectToggle(oldValue);
                    }
                    controller.setOperatingSystem(getSelectedOS());
                });

    }
}
