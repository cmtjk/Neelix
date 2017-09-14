package de.r3r57.neelix.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import de.r3r57.neelix.logger.Logger;
import de.r3r57.neelix.model.resources.Request;
import de.r3r57.neelix.model.resources.RequestType;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;

public class MysqlHandler implements DatabaseConnection {

    private final MysqlDataSource mysqlDS;

    private final String queryRequestsFromLastSevenDays = "SELECT * FROM (SELECT * FROM Requests ORDER BY ID DESC LIMIT 1000) AS t WHERE DATE >= CURDATE() - INTERVAL 7 DAY";

    public MysqlHandler(Properties props) throws SystemException {

	Logger.getInstance().trace("Setting up database connection...");

	mysqlDS = new MysqlDataSource();
	mysqlDS.setURL("jdbc:mysql://" + props.getProperty("DB_SERVER") + ":" + props.getProperty("DB_PORT") + "/"
		+ props.getProperty("DB_NAME") + "?connectTimeout=" + props.getProperty("DB_TIMEOUT", "5000"));
	mysqlDS.setUser(props.getProperty("DB_USERNAME"));
	mysqlDS.setPassword(props.getProperty("DB_PASSWORD"));

	mysqlDS.setUseUnicode(true);
	mysqlDS.setCharacterEncoding("UTF-8");

	boolean ssl_enabled = Boolean.valueOf(props.getProperty("SSL", "false"));
	if (ssl_enabled) {
	    System.setProperty("javax.net.ssl.trustStore", props.getProperty("TRUSTSTORE"));
	    System.setProperty("javax.net.ssl.trustStorePassword", props.getProperty("TRUSTSTORE_PASSWORD"));
	    mysqlDS.setUseSSL(ssl_enabled);
	}

	Logger.getInstance().trace("URL: " + mysqlDS.getUrl());
	Logger.getInstance().trace("USER: " + mysqlDS.getUser());
	checkConnection();

    }

    public List<Request> getRequestsFromLastSevenDays() throws SystemException {
	try (Connection con = mysqlDS.getConnection();
		PreparedStatement statement = con.prepareStatement(queryRequestsFromLastSevenDays)) {
	    return Request.parseFromSQLResult(statement.executeQuery());
	} catch (SQLException e) {
	    throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
		    .set("method", "getRequestsFromLastSevenDays()");
	}
    }

    public boolean addRequest(Request request, int quantity) throws SystemException {
	if (request != null && quantity > 0) {
	    final String insertNewRequest = "INSERT INTO Requests (DATE, TIME, TYPE, OPERATING_SYSTEM, COMMENT, LOCATION) VALUES (?,?,?,?,?,?);";
	    try (Connection con = mysqlDS.getConnection();
		    PreparedStatement statement = con.prepareStatement(insertNewRequest)) {
		statement.setString(1, request.getDate().toString());
		statement.setString(2, request.getTime().toString());
		statement.setString(3, request.getRequestType().toString());
		statement.setString(4, request.getOperatingSystem().toString());
		statement.setString(5, request.getComment());
		statement.setString(6, request.getLocation().toString());

		while (quantity > 0) {
		    statement.addBatch();
		    quantity--;
		}
		statement.executeBatch();
		return true;
	    } catch (SQLException e) {
		throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
			.set("method", "addRequest()");
	    }
	}
	return false;
    }

    public List<Request> getRequestsByDate(LocalDate beginDate, LocalDate endDate) throws SystemException {
	if (beginDate != null && endDate != null) {
	    try (Connection con = mysqlDS.getConnection();
		    PreparedStatement statement = con.prepareStatement("SELECT ALL * FROM Requests WHERE DATE BETWEEN '"
			    + beginDate + "' AND '" + endDate + "'")) {
		return Request.parseFromSQLResult(statement.executeQuery());
	    } catch (SQLException e) {
		throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
			.set("method", "getRequestsByDate()");
	    }
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "getRequestsByDate()").set("arg0", beginDate).set("arg1", endDate);
	}

    }

    public boolean removeRequest(Request request) throws SystemException {
	if (request != null) {
	    try (Connection con = mysqlDS.getConnection();
		    PreparedStatement statement = con
			    .prepareStatement("DELETE FROM Requests WHERE ID = '" + request.getId() + "';")) {
		return (statement.executeUpdate() == 1);
	    } catch (SQLException e) {
		throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
			.set("method", "removeRequest()");
	    }
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "removeRequest()").set("arg0", request);
	}
    }

    public List<RequestType> getRequestTypes() throws SystemException {
	try (Connection con = mysqlDS.getConnection();
		PreparedStatement statement = con
			.prepareStatement("SELECT ALL * FROM RequestTypes ORDER BY Name ASC")) {
	    ResultSet rs = statement.executeQuery();
	    List<RequestType> result = new LinkedList<>();
	    while (rs.next()) {
		result.add(new RequestType(rs.getString("NAME"), rs.getString("DESCRIPTION"),
			rs.getString("OS_INDEPENDENCY")));
	    }
	    return result;
	} catch (SQLException e) {
	    throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
		    .set("method", "getRequestTypes()");
	}
    }

    private void checkConnection() throws SystemException {
	try (Connection con = mysqlDS.getConnection()) {
	    Logger.getInstance().trace("Connection test successful...");
	} catch (SQLException e) {
	    throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
		    .set("method", "checkConnection()").set("cause", "There is a problem connecting to the database.");
	}
    }

    @Override
    public String getVersion() throws SystemException {
	try (Connection con = mysqlDS.getConnection();
		PreparedStatement statement = con
			.prepareStatement("SELECT `VALUE` FROM Properties WHERE `KEY` = 'Version'")) {
	    ResultSet rs = statement.executeQuery();
	    rs.next();
	    return rs.getString("VALUE");
	} catch (SQLException e) {
	    throw new SystemException(e.getMessage(), e, ErrorCode.SQL_EXCEPTION).set("class", this.getClass())
		    .set("method", "getRequestTypes()");
	}
    }

}