package de.r3r57.neelix.model.resources;

import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;

public enum OperatingSystem {

    NONE, WINDOWS, MACOS, LINUX, ANDROID, OTHER;

    public static OperatingSystem parse(String string) throws SystemException {
	if (string != null) {
	    switch (string.toLowerCase()) {
	    case "none":
		return OperatingSystem.NONE;
	    case "windows":
		return OperatingSystem.WINDOWS;
	    case "macos":
		return OperatingSystem.MACOS;
	    case "android":
		return OperatingSystem.ANDROID;
	    case "linux":
		return OperatingSystem.LINUX;
	    case "other":
		return OperatingSystem.OTHER;
	    }
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "OperatingSystem").set("method", "parse")
		    .set("arg0", "string=" + string);

	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "OperatingSystem").set("method", "parse")
		    .set("arg0", "string=" + string);
	}
    }
}
