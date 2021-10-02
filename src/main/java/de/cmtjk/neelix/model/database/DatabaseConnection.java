package de.cmtjk.neelix.model.database;

import de.cmtjk.neelix.model.resources.Request;
import de.cmtjk.neelix.model.resources.RequestType;
import de.cmtjk.neelix.model.resources.exception.SystemException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface DatabaseConnection {

    /**
     * Returns all requests from the last seven days.
     *
     * @return ResultSet contains the data
     * @throws SQLException
     */

    List<Request> getRequestsFromLastSevenDays() throws SystemException;

    /**
     * Adds one or more new requests to the database.
     *
     * @param request
     * @param quantity
     * @return bool
     * @throws SQLException
     */
    boolean addRequest(Request request, int quantity) throws SystemException;

    /**
     * Returns all requests falling into a given time frame.
     *
     * @param beginDate
     * @param endDate
     * @return ResultSet contains the data
     * @throws SQLException
     */

    List<Request> getRequestsByDate(LocalDate beginDate, LocalDate endDate) throws SystemException;

    /**
     * Removes the given request from the database.
     *
     * @param request
     * @return bool
     * @throws SQLException
     */

    boolean removeRequest(Request request) throws SystemException;

    /**
     * Returns all request types from the database.
     *
     * @return ResultSet contains the data
     * @throws SQLException
     */

    List<RequestType> getRequestTypes() throws SystemException;

    String getVersion() throws SystemException;

}