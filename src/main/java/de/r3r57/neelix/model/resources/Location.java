package de.r3r57.neelix.model.resources;

import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;

public enum Location {

    TB4LOCAL, TB4TELOTRS, RZ;

    public static Location parse(String string) throws SystemException {
	if (string != null) {
	    switch (string.toLowerCase()) {
	    case "rz":
		return Location.RZ;
	    case "tb4local":
		return Location.TB4LOCAL;
	    case "tb4telotrs":
		return Location.TB4TELOTRS;
	    }
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "Location").set("method", "parse")
		    .set("arg0", "string=" + string);

	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "Location").set("method", "parse")
		    .set("arg0", "string=" + string);
	}
    }

}
