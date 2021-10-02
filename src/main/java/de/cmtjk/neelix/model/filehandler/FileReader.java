package de.cmtjk.neelix.model.filehandler;

import de.cmtjk.neelix.model.resources.exception.ErrorCode;
import de.cmtjk.neelix.model.resources.exception.SystemException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * This class is an universal file reader an reads the config and the log file.
 *
 * @author Cornelius Matejka
 */

public class FileReader {

    public Properties getProperties(Path filePath) throws SystemException, IOException {

        if (filePath == null) {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "getProperties").set("arg0", "filePath=" + filePath);
        }

        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(filePath.toString())) {
            props.load(in);
        }
        return props;
    }

    /**
     * Returns the whole content of the current log file.
     * <p>
     * This method removes the BOM.
     *
     * @param filePath
     * @return List<String> containing each line of the log file
     * @throws IOException
     * @throws SystemException
     */

    public List<String> getLogFile(Path filePath) throws IOException, SystemException {

        final List<String> logList = new LinkedList<>();

        if (filePath != null) {

            try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {

                String line;
                while ((line = br.readLine()) != null) {
                    line = removeBOM(line);
                    logList.add(line.trim());
                }

                return logList;
            }
        } else {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "getLogFile").set("arg0", "filePath=" + filePath);
        }
    }

    private String removeBOM(String line) {
        return line.replace("\uFEFF", "");
    }

}
