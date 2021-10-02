package de.cmtjk.neelix.model.evaluator;

import de.cmtjk.neelix.logger.Logger;
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

public class HTMLFileWriter {

    public boolean writeHTMLFile(Path htmlFilePath, String html) throws IOException, SystemException {

        if (htmlFilePath != null && !Files.exists(htmlFilePath)) {
            Files.createDirectory(htmlFilePath);
        }

        if (html == null || "".equals(html)) {
            throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
                    .set("method", "writeHTMLFile").set("arg0", "html");
        } else {
            Logger.getInstance().trace("Writing file...");
            return writeFile(htmlFilePath, html);
        }

    }

    public boolean writeFile(Path htmlFilePath, String html) throws IOException {
        int count = 1;

        Path filePath = Paths.get(htmlFilePath.toString() + "/statistic" + LocalDate.now() + ".html");

        while (Files.exists(filePath)) {
            filePath = Paths.get(htmlFilePath + "/statistic" + LocalDate.now() + "(" + count + ").html");
            count++;
        }

        Logger.getInstance().trace("EVALUATE: Writing to file " + filePath.toAbsolutePath());

        try (BufferedWriter br = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {

            br.write(html);

            Logger.getInstance().trace("EVALUATE: Success!");

        } catch (IOException e) {
            throw new IOException("Unable to write to file: " + e);
        }
        return true;
    }
}
