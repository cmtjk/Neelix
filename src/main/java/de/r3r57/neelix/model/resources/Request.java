package de.r3r57.neelix.model.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;

public class Request {

    private int id;
    private LocalDate date;
    private LocalTime time;
    private RequestType requestType;
    private OperatingSystem operatingSystem;
    private String comment;
    private Location location;

    public Request() {
	this.id = -1;
	this.date = LocalDate.now();
	this.time = LocalTime.now();
	this.requestType = new RequestType();
	this.operatingSystem = OperatingSystem.NONE;
	this.comment = "";
	this.location = Location.RZ;

    }

    public Request(int id, LocalDate date, LocalTime time, RequestType requestType, OperatingSystem os,
	    String description, Location location) throws SystemException {
	if (date != null && time != null && requestType != null && os != null && description != null
		&& location != null) {
	    this.id = id;
	    this.date = date;
	    this.time = time;
	    this.requestType = requestType;
	    this.operatingSystem = os;
	    this.comment = description;
	    this.location = location;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "Request")
		    .set("method", "parseFromSQLResult").set("arg0", "id=" + id).set("arg1", "date=" + date)
		    .set("arg2", "time=" + time).set("arg3", "requestType=" + requestType).set("arg4", "os=" + os)
		    .set("arg5", "description=" + description).set("arg6", "location=" + location);
	}
    }

    public static List<Request> parseFromSQLResult(ResultSet resultSet) throws SystemException, SQLException {
	if (resultSet != null) {
	    List<Request> resultList = new ArrayList<>();
	    while (resultSet.next()) {
		int id = resultSet.getInt("ID");
		LocalDate date = LocalDate.parse(resultSet.getString("DATE"));
		LocalTime time = LocalTime.parse(resultSet.getString("TIME"));
		RequestType requestType = RequestType.parse(resultSet.getString("TYPE"));
		OperatingSystem os = OperatingSystem.parse(resultSet.getString("OPERATING_SYSTEM"));
		String description = resultSet.getString("COMMENT");
		Location location = Location.parse(resultSet.getString("LOCATION"));

		resultList.add(new Request(id, date, time, requestType, os, description, location));
	    }
	    return resultList;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "Request")
		    .set("method", "parseFromSQLResult").set("arg0", "resultSet=" + resultSet);
	}
    }

    public LocalDate getDate() {
	return date;
    }

    public void setDate(LocalDate date) throws SystemException {
	if (date != null) {
	    this.date = date;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "parseFromSQLResult").set("arg0", "date=" + date);
	}
    }

    public LocalTime getTime() {
	return time;
    }

    public void setTime(LocalTime time) throws SystemException {
	if (time != null) {
	    this.time = time;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass()).set("method", "setTime")
		    .set("arg0", "time=" + time);
	}
    }

    public RequestType getRequestType() {
	return requestType;
    }

    public void setRequestType(RequestType requestType) throws SystemException {
	if (requestType != null) {
	    this.requestType = requestType;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setRequestType").set("arg0", "requestType=" + requestType);
	}
    }

    public OperatingSystem getOperatingSystem() {
	return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem os) throws SystemException {
	if (os != null) {
	    this.operatingSystem = os;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setOperatingSystem").set("arg0", "os=" + os);
	}
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) throws SystemException {
	if (comment != null) {
	    comment = comment.replace(";", ":");
	    comment = comment.replace("\r\n", " ");
	    comment = comment.replace("\n", " ");
	    this.comment = comment;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setComment").set("arg0", "comment=" + comment);
	}
    }

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) throws SystemException {
	if (location != null) {
	    this.location = location;
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setLocation").set("arg0", "location=" + location);
	}
    }

    public int getId() {
	return id;
    }

    @Override
    public String toString() {
	return id + ";" + date + ";" + time + ";" + requestType.getName() + ";" + operatingSystem + ";" + comment + ";"
		+ location;
    }

    public String toCsvString() {
	return id + "," + date + "," + time + "," + requestType.getName() + "," + operatingSystem + ","
		+ comment.replace(',', ' ') + "," + location;
    }

}
