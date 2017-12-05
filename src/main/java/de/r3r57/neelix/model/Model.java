package de.r3r57.neelix.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Properties;

import de.r3r57.neelix.logger.Logger;
import de.r3r57.neelix.model.database.DatabaseConnection;
import de.r3r57.neelix.model.database.MysqlHandler;
import de.r3r57.neelix.model.evaluator.CsvWriter;
import de.r3r57.neelix.model.evaluator.Evaluator;
import de.r3r57.neelix.model.evaluator.HTMLFileWriter;
import de.r3r57.neelix.model.filehandler.FileReader;
import de.r3r57.neelix.model.resources.Location;
import de.r3r57.neelix.model.resources.OperatingSystem;
import de.r3r57.neelix.model.resources.Request;
import de.r3r57.neelix.model.resources.RequestType;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;

public class Model {

    private final String VERSION = "2.2.1";

    private Properties props;

    private final Path configFilePath = Paths.get("./config/neelix.config").toAbsolutePath();
    private final Path userhome = Paths.get(System.getProperty("user.home")).toAbsolutePath();
    private final Path logFilePath = Paths.get("./log/neelix.log").toAbsolutePath();

    private int quantity;
    private boolean useCurrentTime;

    private String latestVersion;

    private DatabaseConnection databaseHandler;

    private final Request request;
    private final FileReader fileReader;

    private List<RequestType> requestTypeList;

    public Model() {

	request = new Request();
	quantity = 1;
	useCurrentTime = true;

	fileReader = new FileReader();

    }

    public void init() throws IOException, SystemException {

	props = fileReader.getProperties(configFilePath);

	try {

	    switch (props.getProperty("DB_TYPE").toLowerCase()) {
	    case "mysql":
		databaseHandler = new MysqlHandler(props);
		break;
	    default:
		throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
			.set("method", "init()").set("cause", "Invalid database type in config file.");
	    }

	    latestVersion = databaseHandler.getVersion();

	} catch (NullPointerException e) {
	    throw new SystemException(e.getMessage(), e, ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "initFilePaths()");
	}

	requestTypeList = initRequestTypes();

    }

    public String getVersion() {
	return VERSION;
    }

    public String getLatestVersion() {
	return latestVersion;
    }

    public boolean newVersionAvailable() {
	return !VERSION.equals(latestVersion);
    }

    public List<RequestType> getRequestTypes() {
	return requestTypeList;
    }

    public void setRequestType(RequestType requestType) throws SystemException {
	request.setRequestType(requestType);
    }

    public void setComment(String comment) throws SystemException {
	request.setComment(comment);
    }

    public void setOperatingSystem(OperatingSystem os) throws SystemException {
	request.setOperatingSystem(os);
    }

    public void setDate(LocalDate date) throws SystemException {
	useCurrentTime = false;
	request.setDate(date);
    }

    public void setTime(LocalTime time) throws SystemException {
	request.setTime(time);
	useCurrentTime = false;
    }

    public void setQuantity(int quantity) {
	this.quantity = quantity;
    }

    public void setLocation(Location location) throws SystemException {
	request.setLocation(location);
    }

    public void setUseCurrentDateTime(boolean bool) {
	useCurrentTime = bool;
    }

    public boolean saveRequest() throws SystemException {

	if (useCurrentTime) {
	    request.setDate(LocalDate.now());
	    request.setTime(LocalTime.now());
	}

	databaseHandler.addRequest(request, quantity);
	Logger.getInstance().info("REQUEST SAVED (" + quantity + "): " + request);
	return true;

    }

    public List<String> getLogContent() throws IOException, SystemException {
	return fileReader.getLogFile(logFilePath);
    }

    public List<Request> getRequestsFromLastSevenDays() throws SystemException {
	return databaseHandler.getRequestsFromLastSevenDays();
    }

    public int[] getRequestStatistic() throws SystemException {
	int[] statistic = new int[] { 0, 0, 0, 0, 0, 0, 0 };

	List<Request> requestList;
	requestList = databaseHandler.getRequestsFromLastSevenDays();
	LocalDate now = LocalDate.now();
	requestList.forEach(request -> {
	    LocalDate requestDate = request.getDate();
	    if (now.isEqual(requestDate)) {
		statistic[0]++;
	    } else if (now.minusDays(1).equals(requestDate)) {
		statistic[1]++;
	    } else if (now.minusDays(2).equals(requestDate)) {
		statistic[2]++;
	    } else if (now.minusDays(3).equals(requestDate)) {
		statistic[3]++;
	    } else if (now.minusDays(4).equals(requestDate)) {
		statistic[4]++;
	    } else if (now.minusDays(5).equals(requestDate)) {
		statistic[5]++;
	    } else if (now.minusDays(6).equals(requestDate)) {
		statistic[6]++;
	    }
	});

	return statistic;
    }

    public boolean evaluateRequest(LocalDate beginDate, LocalDate endDate) throws IOException, SystemException {

	if (beginDate != null && endDate != null) {
	    LocalDate beginMonth = beginDate.withDayOfMonth(1);
	    LocalDate endMonth = endDate.withDayOfMonth(endDate.lengthOfMonth());

	    List<Request> requestList = databaseHandler.getRequestsByDate(beginMonth, endMonth);

	    Logger.getInstance().trace("EVALUATE: " + requestList.size() + " request parsed from file.");
	    return new HTMLFileWriter().writeHTMLFile(userhome,
		    new Evaluator().evaluate(requestList, requestTypeList, beginMonth, endMonth));

	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "evaluateRequest").set("arg0", "beginDate=" + beginDate)
		    .set("arg1", "endDate=" + endDate);
	}

    }

    public boolean createCsvFileAll(LocalDate beginDate, LocalDate endDate) throws SystemException, IOException {

	if (beginDate != null && endDate != null) {
	    LocalDate beginMonth = beginDate.withDayOfMonth(1);
	    LocalDate endMonth = endDate.withDayOfMonth(endDate.lengthOfMonth());

	    List<Request> requestList = databaseHandler.getRequestsByDate(beginMonth, endMonth);

	    Logger.getInstance().trace("CSV: " + requestList.size() + " request parsed from file.");

	    return new CsvWriter().writeCsvAll(userhome, requestList);

	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "createCsvFileAll").set("arg0", "beginDate=" + beginDate)
		    .set("arg1", "endDate=" + endDate);
	}

    }

    public boolean createCsvFileOverview(LocalDate beginDate, LocalDate endDate) throws IOException, SystemException {
	if (beginDate != null && endDate != null) {
	    LocalDate beginMonth = beginDate.withDayOfMonth(1);
	    LocalDate endMonth = endDate.withDayOfMonth(endDate.lengthOfMonth());

	    List<Request> requestList = databaseHandler.getRequestsByDate(beginMonth, endMonth);

	    Logger.getInstance().trace("CSV: " + requestList.size() + " request parsed from file.");

	    return new CsvWriter().createCSVFile(userhome, requestList, requestTypeList, beginDate, endDate);

	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "createCsvFileOverview").set("arg0", "beginDate=" + beginDate)
		    .set("arg1", "endDate=" + endDate);
	}

    }

    public boolean deleteRequest(Request requestToDelete) throws SystemException {
	if (requestToDelete != null) {
	    Logger.getInstance().trace("DELETING: Deleting " + requestToDelete.toString());
	    return databaseHandler.removeRequest(requestToDelete);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "deleteRequest()").set("variable", "requestToDelete");
	}
    }

    private List<RequestType> initRequestTypes() throws SystemException {
	return databaseHandler.getRequestTypes();
    }

}
