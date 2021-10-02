package de.cmtjk.neelix.model.evaluator;

import de.cmtjk.neelix.logger.Logger;
import de.cmtjk.neelix.model.resources.Location;
import de.cmtjk.neelix.model.resources.Request;
import de.cmtjk.neelix.model.resources.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents the evaluator of Neelix. It's an very early but full
 * functional implementation from 2014. The evaluator evaluates the data of the
 * given requests and returns a human readable HTML string as table.
 *
 * @author Cornelius Matejka
 */

public class Evaluator {

    private final LocalTime beginAfternoon = LocalTime.of(12, 59, 59);

    private List<Request> requestListRZ;
    private List<Request> requestListTB4LOCAL;

    private StringBuilder htmlBuilder, commentsBuilder, tableRZBuilder, tableTB4Builder, summaryBuilder, tableBuilder,
            monthTable, tableOfMonthTablesBuilder;

    private int overallMorning, overallAfternoon, tb4Counter;
    private int noneMorning;
    private int noneAfternoon;
    private int windowsAfternoon;
    private int windowsMorning;
    private int macosMorning;
    private int macosAfternoon;
    private int linuxMorning;
    private int linuxAfternoon;
    private int androidMorning;
    private int androidAfternoon;
    private int otherMorning;
    private int otherAfternoon;
    private int overallCase;
    private int overallNone;
    private int overallWindows;
    private int overallMacos;
    private int overallLinux;
    private int overallAndroid;
    private int overallMisc;

    private int overallNoneCurrentMonth;
    private int overallWindowsCurrentMonth;
    private int overallMacosCurrentMonth;
    private int overallLinuxCurrentMonth;
    private int overallAndroidCurrentMonth;
    private int overallMiscCurrentMonth;
    private int overallCurrentMonth;
    private int tb4CounterCurrentMonth;
    private int overallMorningCurrentMonth;
    private int overallAfternoonCurrentMonth;

    /**
     * Evaluates the given requests and returns the result as human readable
     * html.
     *
     * @param requestList
     * @param requestTypes
     * @param beginDate
     * @param endDate
     * @return String human readable result as table
     */

    public String evaluate(List<Request> requestList, List<RequestType> requestTypes, LocalDate beginDate,
                           LocalDate endDate) {

        if (requestList == null || requestTypes == null || beginDate == null || endDate == null) {
            return "An error occured creating the content (null in arguments)";
        } else {

            prepareHTML(beginDate, endDate);
            prepareTableOfMonthTable(requestTypes);
            resetOverallCounters();
            evaluate(beginDate, endDate, sortRequestsByDate(requestList), requestTypes);

            summaryBuilder.append("<tr><th>&sum;</th><th>" + requestList.size() + "</th><th>" + tb4Counter + "<th>"
                    + overallMorning + "</th><th> " + overallAfternoon
                    + "</th></tr></table><p><a href='#comments'>zu den Kommentaren</a></p>");

            endHTML();

            return htmlBuilder.toString();

        }

    }

    private Map<Integer, Map<Month, List<Request>>> sortRequestsByDate(List<Request> requestList) {
        Map<Integer, Map<Month, List<Request>>> sortedRequests = new HashMap<>();

        requestList.forEach(request -> {

            if (!sortedRequests.containsKey((request.getDate().getYear()))) {
                sortedRequests.put((request.getDate().getYear()), new HashMap<Month, List<Request>>());
                Logger.getInstance().trace("Request(s) found for year: " + request.getDate().getYear());

            }
            if (!sortedRequests.get((request.getDate().getYear())).containsKey((request.getDate().getMonth()))) {
                sortedRequests.get((request.getDate().getYear())).put((request.getDate().getMonth()),
                        new LinkedList<Request>());
                Logger.getInstance().trace("Request(s) found for month: " + request.getDate().getMonth());
            }

            sortedRequests.get((request.getDate().getYear())).get((request.getDate().getMonth())).add(request);

        });

        Logger.getInstance().trace("Finished!");
        return sortedRequests;

    }

    private void resetOverallCounters() {
        overallMorning = 0;
        overallAfternoon = 0;
        tb4Counter = 0;
    }

    private void prepareTableOfMonthTable(List<RequestType> requestTypes) {
        tableOfMonthTablesBuilder.append("<td style='width:250px'>" + createRequestTypesTable(requestTypes) + "</td>");
    }

    private void evaluate(LocalDate beginDate, LocalDate endDate,
                          Map<Integer, Map<Month, List<Request>>> sortedRequests, List<RequestType> requestTypes) {

        LocalDate tempDate = beginDate;
        do {

            if (sortedRequests.containsKey(tempDate.getYear())
                    && sortedRequests.get(tempDate.getYear()).containsKey(tempDate.getMonth())) {

                monthTable = new StringBuilder(
                        "<table style='padding:0; width:150px; margin:0'><th colspan='2'>" + tempDate.getMonth() + " "
                                + tempDate.getYear() + "</th><tr id='location'><td>RZ</td><td>TB</td></tr>");

                overallNoneCurrentMonth = 0;
                overallWindowsCurrentMonth = 0;
                overallMacosCurrentMonth = 0;
                overallLinuxCurrentMonth = 0;
                overallAndroidCurrentMonth = 0;
                overallMiscCurrentMonth = 0;
                overallCurrentMonth = 0;
                tb4CounterCurrentMonth = 0;
                overallAfternoonCurrentMonth = 0;
                overallMorningCurrentMonth = 0;

                List<Request> currentRequestsList = sortedRequests.get(tempDate.getYear()).get(tempDate.getMonth());
                Logger.getInstance().trace("Starting to evaluate " + currentRequestsList.size() + " requests for "
                        + tempDate.getMonth() + " " + tempDate.getYear());

                sortRequestByLocationAndGetComments(currentRequestsList);

                createTableAndRowBuilder(tempDate);

                requestTypes.forEach(requestType -> {
                    monthTable.append("<tr class='data'>");
                    tableRZBuilder.append(createTableRow(requestListRZ, requestType));
                    tableTB4Builder.append(createTableRow(requestListTB4LOCAL, requestType));
                    monthTable.append("</tr>");
                });

                tb4CounterCurrentMonth = requestListTB4LOCAL.size();
                tb4Counter += tb4CounterCurrentMonth;
                overallCurrentMonth = currentRequestsList.size();
                endTable();

                tableOfMonthTablesBuilder.append("<td style='paddding:0; margin:0'>" + monthTable + "</table></td>");

                summaryBuilder.append("<tr><td><a href='#" + tempDate.hashCode() + "'>" + tempDate.getYear() + " "
                        + tempDate.getMonth() + "</a></td><td>" + overallCurrentMonth + "</td><td>"
                        + tb4CounterCurrentMonth + "</td><td>" + overallMorningCurrentMonth + "</td><td>"
                        + overallAfternoonCurrentMonth + "</td></tr>");

            }

            tempDate = tempDate.plusMonths(1);
        } while (!tempDate.isAfter(endDate));
    }

    private void prepareHTML(LocalDate beginDate, LocalDate endDate) {

        /* Create HTML + Head */
        htmlBuilder = new StringBuilder("<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset='utf-8'>"
                + "<style type='text/css'>" + "html {overflow-y: scroll;}"
                + "body,div,h1,h2,h3,h4,h5,h6,p,blockquote,pre,code,ul,ol,li,table,th,td,form,fieldset,legend,input,textarea {padding: 0;margin: 0;}"
                + "h2,h3,h4,h5,h6,p,blockquote,pre,section {margin-bottom: 1em;}"
                + "body {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 75%; background-color: whitesmoke; }"
                + "h1 { font-size: 150%; padding-bottom: 1em}" + "h2 { font-size: 75%; font-weight: lighter }"
                + "header {padding: .5em; padding-bottom: .1em; margin-bottom: 1em; background-color: rgb(51,106,151); color:white}"
                + "div {margin-bottom: 2em; margin-top: 2em;}"
                + "div#wrapper { width: 1280px; margin:auto; background:white; padding: 1em; }"
                + ".summary { background-color: whitesmoke; font-size: 10pt;}" + "div#comments {padding: 1em}"
                + "div#comments p#wholeComment {background-color: rgb(255,246,204); padding: .5em}"
                + "div#comments span#theComment {font-style: italic;}"
                + "a#top {position:fixed; padding:1em; background-color: orange; bottom:0px; margin:auto; box-shadow: 3px 3px 3px grey; color:white;}"
                + "div.scrollable {overflow:hidden; overflow-y:scroll; height:1250px}"
                + "ul.scrollable {overflow:hidden; overflow-y:scroll; height:500px;}"
                + "table {width:100%; margin-bottom: 3em; margin-top: 3em;}"
                + "th {padding: .5em; background-color: rgb(102,144,177); color:white}"
                + "tr#daytime { font-size: 85%; background-color: rgb(234,242,207); text-align: center}"
                + "tr#location { background-color: rgb(234,242,207); text-align:center}"
                + "td.month {background-color: yellow; font-weight:bold; text-align:center; font-size:14pt}"
                + "tr.data:hover td {background-color: rgb(255,246,204);}"
                + "td {vertical-align:top; overflow:hidden; padding: .1em}"
                + "tr.data:nth-child(odd) { background-color: rgb(204,218,229)}"
                + "tr.data:nth-child(even) { background-color: #ffffff}" + ".center { text-align: center}"
                + ".blue {background-color: rgb(102,144,177); color:white;}" + ".center {text-align: center;}"
                + ".no-margin { margin: 0}" + "</style>" + "</head>" + "<body>" + "<div id=wrapper>" + "<header>"
                + "<h1>Statistik von " + beginDate.getDayOfMonth() + ". " + beginDate.getMonth().toString() + " "
                + beginDate.getYear() + " bis " + endDate.getDayOfMonth() + ". " + endDate.getMonth().toString() + " "
                + endDate.getYear() + "</h1>" + "<h2>" + LocalDateTime.now()
                + "</h2></header><div class=''><a id='top' href='#'>Back To Top</a>");

        tableBuilder = new StringBuilder();
        summaryBuilder = new StringBuilder(
                "<div class='summary'>Übersicht - insgesamt<table><tr><th>Übersicht</th><th>insgesamt</th><th>davon TB4</th><th>vormittags</th><th>nachmittags</th></tr>");
        tableOfMonthTablesBuilder = new StringBuilder(
                "<div class='summary' style='overflow-x:scroll'>Übersicht - monatlich<table><tr>");
        commentsBuilder = new StringBuilder("<div id=comments><h1>Kommentare:</h1><ul class='scrollable'>");
    }

    private String createRequestTypesTable(List<RequestType> requestTypes) {
        StringBuilder tmp = new StringBuilder(
                "<table style='width:350px; margin:0;'><th>Anfragen</th><tr id='location'><td>Ort</td></tr>");
        requestTypes.forEach(requestType -> {
            tmp.append("<tr class='data'><td>" + requestType.getName() + "</td></tr>");
        });
        tmp.append("</table>");
        return tmp.toString();
    }

    private void createTableAndRowBuilder(LocalDate currentDate) {

        Logger.getInstance().trace("Creating table for " + currentDate.getMonth() + " " + currentDate.getYear());

        final String table = "<table id='" + currentDate.hashCode() + "'><tr><td class='month'>" + currentDate.getYear()
                + " - " + currentDate.getMonth().toString() + "</td></tr>";

        /* Create Table + Head */
        final String tablehead = "<tr>" + "<th rowspan='2'>Anfragetyp</th>" + "<th colspan='2'>BS unabhängig</th>"
                + "<th colspan='2'>Windows</th>" + "<th colspan='2'>Mac OS / iOS</th>" + "<th colspan='2'>Linux</th>"
                + "<th colspan='2'>Android</th>" + "<th colspan='2'>sonstige</th>" + "<th colspan='2'>&sum;</th>"
                + "</tr>" + "<tr id=daytime>" + "<td>vormittags</td>" + "<td>nachmittags</td>" + "<td>vormittags</td>"
                + "<td>nachmittags</td>" + "<td>vormittags</td>" + "<td>nachmittags</td>" + "<td>vormittags</td>"
                + "<td>nachmittags</td>" + "<td>vormittags</td>" + "<td>nachmittags</td>" + "<td>vormittags</td>"
                + "<td>nachmittags</td>" + "<td colspan=2></td>" + "</tr>";

        tableRZBuilder = new StringBuilder(table + "<tr><th colspan='14'>RZ und TB4(tel/OTRS)</th></tr>" + tablehead);
        tableTB4Builder = new StringBuilder("<tr><th colspan='14'>TB4(lokal)</th></tr>" + tablehead);

        Logger.getInstance().trace("Success");
    }

    private void endHTML() {
        summaryBuilder.append("</div>");
        tableOfMonthTablesBuilder.append("</td></tr></table></div>");
        commentsBuilder.append("</ul></div>");
        htmlBuilder.append(summaryBuilder).append(tableOfMonthTablesBuilder).append(tableBuilder).append("</div>")
                .append(commentsBuilder).append("</div></body></html>");
    }

    private void endTable() {
        tableTB4Builder.append("<tr>" + "<th rowspan='2'>&sum; aller Anfragen: ").append(overallCurrentMonth)
                .append("<br/> davon TB4: ").append(tb4CounterCurrentMonth).append("</th>").append("<th colspan='2'>")
                .append(overallNoneCurrentMonth).append("</th>").append("<th colspan='2'>")
                .append(overallWindowsCurrentMonth).append("</th>").append("<th colspan='2'>")
                .append(overallMacosCurrentMonth).append("</th>").append("<th colspan='2'>")
                .append(overallLinuxCurrentMonth).append("</th>").append("<th colspan='2'>")
                .append(overallAndroidCurrentMonth).append("</th>").append("<th colspan='2'>")
                .append(overallMiscCurrentMonth).append("</th>").append("<th colspan='2'></th>").append("</tr><tr>")
                .append("<td class='center blue' colspan='6'>Vormittags(").append(overallMorningCurrentMonth)
                .append(")</td><td class='center blue' colspan='6'>Nachmittags(").append(overallAfternoonCurrentMonth)
                .append(")</td><td class='center blue'></td></tr>").append("</table>");
        tableBuilder.append(tableRZBuilder).append(tableTB4Builder);
    }

    private void sortRequestByLocationAndGetComments(List<Request> requestList) {

        Logger.getInstance().trace("Sorting by location...");

        requestListRZ = new LinkedList<>();
        requestListTB4LOCAL = new LinkedList<>();

        requestList.forEach(request -> {

            createComment(request);

            if (Location.TB4LOCAL.equals(request.getLocation())) {
                requestListTB4LOCAL.add(request);
            } else {
                requestListRZ.add(request);

            }
        });

        Logger.getInstance()
                .trace("Success (" + requestListRZ.size() + " in RZ and " + requestListTB4LOCAL.size() + " in TB4)");

    }

    private void createComment(Request request) {

        if (request.getComment() != null && !"".equals(request.getComment())) {
            commentsBuilder.append("<li><p id=wholeComment>").append(request.getDate()).append(" ")
                    .append(request.getTime()).append(" | ").append(request.getRequestType().getName()).append(" | ")
                    .append(request.getOperatingSystem()).append(" | ").append(request.getLocation()).append(" |  &gt;")
                    .append("<span id=theComment>").append(request.getComment()).append("</span></p></li>");
        }
    }

    private String createTableRow(List<Request> requestList, RequestType requestType) {
        noneMorning = 0;
        noneAfternoon = 0;
        windowsMorning = 0;
        windowsAfternoon = 0;
        macosMorning = 0;
        macosAfternoon = 0;
        linuxMorning = 0;
        linuxAfternoon = 0;
        androidMorning = 0;
        androidAfternoon = 0;
        otherMorning = 0;
        otherAfternoon = 0;
        overallCase = 0;

        StringBuilder tr = new StringBuilder();

        requestList.forEach(request -> {
            if (requestType.getName().equals(request.getRequestType().getName())) {
                switch (request.getOperatingSystem()) {
                    case NONE:
                        if (request.getTime().isAfter(beginAfternoon)) {
                            noneAfternoon++;
                            overallAfternoon++;
                            overallAfternoonCurrentMonth++;
                        } else {
                            noneMorning++;
                            overallMorning++;
                            overallMorningCurrentMonth++;
                        }
                        overallNoneCurrentMonth++;
                        overallNone++;
                        break;
                    case WINDOWS:
                        if (request.getTime().isAfter(beginAfternoon)) {
                            windowsAfternoon++;
                            overallAfternoon++;
                            overallAfternoonCurrentMonth++;
                        } else {
                            windowsMorning++;
                            overallMorning++;
                            overallMorningCurrentMonth++;
                        }
                        overallWindowsCurrentMonth++;
                        overallWindows++;
                        break;
                    case MACOS:
                        if (request.getTime().isAfter(beginAfternoon)) {
                            macosAfternoon++;
                            overallAfternoon++;
                            overallAfternoonCurrentMonth++;
                        } else {
                            macosMorning++;
                            overallMorning++;
                            overallMorningCurrentMonth++;
                        }
                        overallMacosCurrentMonth++;
                        overallMacos++;
                        break;
                    case LINUX:
                        if (request.getTime().isAfter(beginAfternoon)) {
                            linuxAfternoon++;
                            overallAfternoon++;
                            overallAfternoonCurrentMonth++;
                        } else {
                            linuxMorning++;
                            overallMorning++;
                            overallMorningCurrentMonth++;
                        }
                        overallLinuxCurrentMonth++;
                        overallLinux++;
                        break;
                    case ANDROID:
                        if (request.getTime().isAfter(beginAfternoon)) {
                            androidAfternoon++;
                            overallAfternoon++;
                            overallAfternoonCurrentMonth++;
                        } else {
                            androidMorning++;
                            overallMorning++;
                            overallMorningCurrentMonth++;
                        }
                        overallAndroidCurrentMonth++;
                        overallAndroid++;
                        break;
                    case OTHER:
                        if (request.getTime().isAfter(beginAfternoon)) {
                            otherAfternoon++;
                            overallAfternoon++;
                            overallAfternoonCurrentMonth++;
                        } else {
                            otherMorning++;
                            overallMorning++;
                            overallMorningCurrentMonth++;
                        }
                        overallMiscCurrentMonth++;
                        overallMisc++;
                        break;
                }
                overallCase++;
            }
        });

        monthTable.append("<td class='center'>" + overallCase + "</td>");

        tr.append("<tr class=data>" + "<td>").append(requestType.getName()).append("<td>").append(noneMorning)
                .append("</td>").append("<td>").append(noneAfternoon).append("</td>").append("<td>")
                .append(windowsMorning).append("</td>").append("<td>").append(windowsAfternoon).append("</td>")
                .append("<td>").append(macosMorning).append("</td>").append("<td>").append(macosAfternoon)
                .append("</td>").append("<td>").append(linuxMorning).append("</td>").append("<td>")
                .append(linuxAfternoon).append("</td>").append("<td>").append(androidMorning).append("</td>")
                .append("<td>").append(androidAfternoon).append("</td>").append("<td>").append(otherMorning)
                .append("</td>").append("<td>").append(otherAfternoon).append("</td>").append("<td>")
                .append(overallCase).append("</td>").append("</tr>");

        return tr.toString();
    }

}
