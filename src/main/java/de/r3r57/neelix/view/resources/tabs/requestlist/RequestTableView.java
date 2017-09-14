package de.r3r57.neelix.view.resources.tabs.requestlist;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;

import de.r3r57.neelix.model.resources.Location;
import de.r3r57.neelix.model.resources.OperatingSystem;
import de.r3r57.neelix.model.resources.RequestType;

public class RequestTableView<Request> extends TableView<Request> {

    private TableColumn<Request, RequestType> requestTypeColumn;

    public RequestTableView() {
	super();
	setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	createIssueTable();
    }

    @SuppressWarnings("unchecked")
    private void createIssueTable() {

	TableColumn<Request, Integer> idColumn = new TableColumn<>("Id");
	TableColumn<Request, LocalDate> dateColumn = new TableColumn<>("Datum");
	TableColumn<Request, LocalTime> timeColumn = new TableColumn<>("Zeit");
	requestTypeColumn = new TableColumn<>("Anfragetyp");
	TableColumn<Request, OperatingSystem> operatingSystemColumn = new TableColumn<>("Betriebsystem");
	TableColumn<Request, String> commentColumn = new TableColumn<>("Kommentar");
	TableColumn<Request, Location> locationColumn = new TableColumn<>("Ort");

	PropertyValueFactory<Request, Integer> idColumnFactory = new PropertyValueFactory<>("id");
	PropertyValueFactory<Request, LocalDate> dateColumnFactory = new PropertyValueFactory<>("date");
	PropertyValueFactory<Request, LocalTime> timeColumnFactory = new PropertyValueFactory<>("time");
	PropertyValueFactory<Request, RequestType> requestTypeFactory = new PropertyValueFactory<>("requestType");
	PropertyValueFactory<Request, OperatingSystem> operatingSystemColumnFactory = new PropertyValueFactory<>(
		"operatingSystem");
	PropertyValueFactory<Request, String> commentColumnFactory = new PropertyValueFactory<>("comment");
	PropertyValueFactory<Request, Location> locationColumnFactory = new PropertyValueFactory<>("location");

	idColumn.setCellValueFactory(idColumnFactory);
	dateColumn.setCellValueFactory(dateColumnFactory);
	timeColumn.setCellValueFactory(timeColumnFactory);
	requestTypeColumn.setCellValueFactory(requestTypeFactory);
	operatingSystemColumn.setCellValueFactory(operatingSystemColumnFactory);
	commentColumn.setCellValueFactory(commentColumnFactory);
	locationColumn.setCellValueFactory(locationColumnFactory);

	initCellStyles();

	this.getColumns().addAll(idColumn, dateColumn, timeColumn, requestTypeColumn, operatingSystemColumn,
		commentColumn, locationColumn);
    }

    private void initCellStyles() {

	requestTypeColumn.setCellFactory(tableColumn -> new TableCell<Request, RequestType>() {
	    @Override
	    public void updateItem(RequestType requestType, boolean isEmpty) {
		super.updateItem(requestType, isEmpty);
		if (isEmpty || requestType == null) {
		    setText("");
		    setStyle("");
		} else {
		    setText(requestType.toString());
		}
	    }
	});

    }

}
