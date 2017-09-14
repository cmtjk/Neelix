package de.r3r57.neelix.model.resources.exception;

public enum ErrorCode {

    ILLEGAL_ARGUMENT(101, "Invalid argument"),

    SQL_EXCEPTION(201, "Database error"),

    PARSE_EXCEPTION(301, "Unable to parse data"),

    IO_EXCEPTION(401, "Unable to read/write file"),

    FILE_EXCEPTION(501, "Unable to access file"),
    
    ILLEGAL_STATE(601, "Illegal state"),
    
    CRITICAL(901, "Critical error");

    private final int code;
    private final String description;

    private ErrorCode(int code, String description) {
	this.code = code;
	this.description = description;
    }

    public int getCode() {
	return code;
    }

    public String getDescription() {
	return description;
    }

}
