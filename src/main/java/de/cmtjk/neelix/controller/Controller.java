package de.cmtjk.neelix.controller;

import de.cmtjk.neelix.model.Model;
import de.cmtjk.neelix.model.resources.Location;
import de.cmtjk.neelix.model.resources.OperatingSystem;
import de.cmtjk.neelix.model.resources.Request;
import de.cmtjk.neelix.model.resources.RequestType;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.ExceptionHandler;
import de.cmtjk.neelix.model.resources.exception.SystemException;
import de.cmtjk.neelix.view.DateDialog;
import de.cmtjk.neelix.view.QuantityDialog;
import de.cmtjk.neelix.view.View;
import javafx.application.Platform;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This class connects the {@link View} and the
 * {@link Model} and provides appropriate methods
 *
 * @author Cornelius Matejka
 */

public class Controller {

    private final Model model;
    private final View view;
    private final ExceptionHandler eh = ExceptionHandler.getInstance();

    /**
     * Creates the {@code Controller} object.
     *
     * @param model {@link Model}
     * @param view  {@link View}
     * @throws SystemException
     */

    public Controller(Model model, View view) throws SystemException {
        if (model != null && view != null) {
            this.model = model;
            this.view = view;
            view.show();
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "constructor").set("arg0", "model=" + model).set("arg1", "view=" + view);
        }
    }

    /**
     * @see View#disableOSButtons()
     */

    public void disableOSButtons() {
        view.disableOSButtons();
    }

    /**
     * @see View#enableOSButtons()
     */

    public void enableOSButtons() {
        view.enableOSButtons();
    }

    /**
     * Opens the date dialog.
     *
     * @throws SystemException
     * @see DateDialog
     */

    public void showDateDialog() {
        try {
            new DateDialog(view, model, this).show();
        } catch (SystemException e) {
            eh.print(e);
        }
    }

    /**
     * Opens the quantity dialog.
     *
     * @see QuantityDialog
     */

    public void showQuantityDialog() {
        try {
            new QuantityDialog(view, this).show();
        } catch (SystemException e) {
            eh.print(e);
        }
    }

    /**
     * @param date
     * @throws SystemException
     * @see Model#setDate(LocalDate)
     */

    public void setDate(LocalDate date) {
        try {
            view.setDate(date);
            model.setDate(date);
        } catch (SystemException e) {
            eh.print(e);
        }

    }

    /**
     * @param quantity
     * @see Model#setQuantity(int)
     */

    public void setQuantity(int quantity) {
        if (quantity >= 1 && quantity <= 20) {
            view.setQuantity(quantity);
            model.setQuantity(quantity);
        } else {
            eh.print(new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "setQuantity").set("arg0", quantity));
        }
    }

    /**
     * @param requestType
     * @throws SystemException
     * @see Model#setRequestType(RequestType)
     */

    public void setRequestType(RequestType requestType) {
        try {
            model.setRequestType(requestType);
            setDescripition(requestType.getDescription());
        } catch (SystemException e) {
            eh.print(e);
        }

    }

    /**
     * @param description
     * @see Model#setComment(String)
     */

    public void setComment(String description) {
        try {
            model.setComment(description);
        } catch (SystemException e) {
            eh.print(e);
        }
    }

    /**
     * @param os
     * @see Model#setOperatingSystem(OperatingSystem)
     */

    public void setOperatingSystem(OperatingSystem os) {
        try {
            model.setOperatingSystem(os);
        } catch (SystemException e) {
            eh.print(e);
        }
    }

    /**
     * @param time
     * @see Model#setTime(LocalTime)
     */

    public void setTime(LocalTime time) {
        try {
            view.setTime(time);
            model.setTime(time);
        } catch (SystemException e) {
            eh.print(e);
        }

    }

    /**
     * @param location
     * @see Model#setLocation(Location)
     */
    public void setLocation(Location location) {
        try {
            model.setLocation(location);
        } catch (SystemException e) {
            eh.print(e);
        }
    }

    /**
     * @return
     * @throws @see Model#saveRequest()
     */

    public boolean saveRequest() {
        try {
            if (model.saveRequest()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.resetFields();
                    }
                });
                return true;
            }
        } catch (SystemException e) {
            eh.print(e);
        }
        return false;

    }

    /**
     * @param bool
     * @see Model#setUseCurrentDateTime(boolean)
     */

    public void setUseCurrentDateTime(boolean bool) {
        view.setDateButtonState(bool);
        model.setUseCurrentDateTime(bool);
    }

    public boolean evaluateRequests(LocalDate beginDate, LocalDate endDate) {
        try {
            return model.evaluateRequest(beginDate, endDate);
        } catch (IOException e) {
            eh.print(new SystemException(e.getMessage(), e, ErrorCode.IO_EXCEPTION).set("class", this.getClass())
                    .set("method", "evaluateRequests"));
        } catch (SystemException e) {
            eh.print(e);
        }
        return false;
    }

    public boolean createCsvFileAll(LocalDate beginDate, LocalDate endDate) {
        try {
            return model.createCsvFileAll(beginDate, endDate);
        } catch (IOException e) {
            eh.print(new SystemException(e.getMessage(), e, ErrorCode.IO_EXCEPTION).set("class", this.getClass())
                    .set("method", "createCsvFileAll"));
        } catch (SystemException e) {
            eh.print(e);
        }
        return false;
    }

    public boolean createCsvFileOverview(LocalDate beginDate, LocalDate endDate) {
        try {
            return model.createCsvFileOverview(beginDate, endDate);
        } catch (IOException e) {
            eh.print(new SystemException(e.getMessage(), e, ErrorCode.IO_EXCEPTION).set("class", this.getClass())
                    .set("method", "createCsvFileOverview"));
        } catch (SystemException e) {
            eh.print(e);
        }
        return false;
    }

    /**
     * @see View#switchToDebugTab()
     */

    public void switchToDebugTab() {
        view.switchToDebugTab();
    }

    /**
     * @param description
     * @throws SystemException
     * @see View#setDescription(String)
     */

    private void setDescripition(String description) {
        try {
            view.setDescription(description);
        } catch (SystemException e) {
            eh.print(e);
        }
    }

    /**
     * @param requestToDelete
     * @return bool
     * @see Model#deleteRequest(Request)
     */

    public boolean deleteRequest(Request requestToDelete) {
        try {
            if (model.deleteRequest(requestToDelete)) {
                view.updateRequestTableTab();
                return true;
            }
        } catch (SystemException e) {
            eh.print(e);
        }
        return false;
    }

}
