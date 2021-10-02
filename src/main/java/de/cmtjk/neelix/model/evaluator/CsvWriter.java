package de.cmtjk.neelix.model.evaluator;

import de.cmtjk.neelix.logger.Logger;
import de.cmtjk.neelix.model.evaluator.utils.EvaluationUtilities;
import de.cmtjk.neelix.model.resources.Request;
import de.cmtjk.neelix.model.resources.RequestType;
import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.SystemException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CsvWriter {

    private final EvaluationUtilities evalUtils = new EvaluationUtilities();

    public boolean writeCsvAll(Path csvFilePath, List<Request> requestList) throws IOException, SystemException {

        if (csvFilePath == null || requestList == null) {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "writeCsvAll").set("arg0", "csvFilePath=" + csvFilePath)
                    .set("arg1", "requestList=" + requestList);
        }

        if (!Files.exists(csvFilePath)) {
            Files.createDirectories(csvFilePath);
        }

        Logger.getInstance().trace("Writing file...");
        return writeFileAll(csvFilePath, requestList);

    }

    private boolean writeFileAll(Path csvFilePath, List<Request> requestList) throws IOException {

        Path filePath = Paths.get(csvFilePath.toString() + "/csv" + LocalDate.now() + ".csv");

        int count = 1;
        while (Files.exists(filePath)) {
            filePath = Paths.get(csvFilePath + "/csv" + LocalDate.now() + "(" + count + ").csv");
            count++;
        }

        Logger.getInstance().trace("CSV: Writing to file " + filePath.toAbsolutePath());

        try (BufferedWriter br = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {

            br.write("'ID,DATE,TIME,REQUEST TYPE,OPERATING SYSTEM,COMMENT,LOCATION");
            br.newLine();

            for (Request request : requestList) {
                br.write(request.toCsvString());
                br.newLine();
            }

            return true;

        }

    }

    public boolean createCSVFile(Path csvFilePath, List<Request> requestList, List<RequestType> requestTypeList,
                                 LocalDate beginDate, LocalDate endDate) throws IOException, SystemException {
        if (csvFilePath == null || requestList == null) {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "createCSVFile").set("arg0", "csvFilePath=" + csvFilePath)
                    .set("arg1", "requestList=" + requestList);
        }

        if (!Files.exists(csvFilePath)) {
            Files.createDirectories(csvFilePath);
        }

        StringBuilder csvBuilder = createContent(requestList, requestTypeList, beginDate, endDate);

        return writeToFile(csvFilePath, csvBuilder);
    }

    private StringBuilder createContent(List<Request> requestList, List<RequestType> requestTypeList,
                                        LocalDate beginDate, LocalDate endDate) throws IOException {

        Logger.getInstance().trace("Sorting requests...");

        Map<Integer, Map<Month, Map<String, AtomicInteger>>> sumOfRequestsByDateAndType = evalUtils
                .sumOfRequestsSortedByTypeAndDate(requestList);

        StringBuilder columnLine = new StringBuilder("REQUESTTYPE, ");

        LocalDate tempDate = beginDate;
        do {
            columnLine.append(tempDate.getMonth().toString() + " " + tempDate.getYear() + ", ");
            tempDate = tempDate.plusMonths(1);
        } while (!tempDate.isAfter(endDate));

        StringBuilder requestTypeLineBuilder = new StringBuilder();
        requestTypeList.forEach(requestType -> {

            requestTypeLineBuilder.append(requestType.getName() + ", ");

            LocalDate tempDate2 = beginDate;
            do {
                if (sumOfRequestsByDateAndType.containsKey(tempDate2.getYear())) {
                    if (sumOfRequestsByDateAndType.get(tempDate2.getYear()).containsKey(tempDate2.getMonth())) {
                        if (sumOfRequestsByDateAndType.get(tempDate2.getYear()).get(tempDate2.getMonth())
                                .containsKey(requestType.getName())) {
                            requestTypeLineBuilder.append(sumOfRequestsByDateAndType.get(tempDate2.getYear())
                                    .get(tempDate2.getMonth()).get(requestType.getName()) + ", ");
                        } else {
                            requestTypeLineBuilder.append("0, ");
                        }
                    } else {
                        requestTypeLineBuilder.append("0, ");
                    }
                } else {
                    requestTypeLineBuilder.append("0, ");
                }

                tempDate2 = tempDate2.plusMonths(1);
            } while (!tempDate2.isAfter(endDate));

            requestTypeLineBuilder.append(System.lineSeparator());

        });

        return columnLine.append(System.lineSeparator()).append(requestTypeLineBuilder);

    }

    private boolean writeToFile(Path csvFilePath, StringBuilder csvBuilder) throws IOException {

        Path filePath = Paths.get(csvFilePath.toString() + "/csv" + LocalDate.now() + ".csv");

        int count = 1;
        while (Files.exists(filePath)) {
            filePath = Paths.get(csvFilePath + "/csv" + LocalDate.now() + "(" + count + ").csv");
            count++;
        }

        Logger.getInstance().trace("CSV: Writing to file " + filePath.toAbsolutePath());

        try (BufferedWriter br = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {

            br.write(csvBuilder.toString());

        }

        return true;

    }

}
