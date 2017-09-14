package de.r3r57.neelix.model.resources;

import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;

public class RequestType {

    private String name;
    private String description;
    private boolean osIndependency;

    public RequestType() {
	this.name = "";
	this.description = "";
	this.osIndependency = false;
    }

    public RequestType(String name, String description, String osIndependency) throws SystemException {
	if (name != null && description != null && osIndependency != null) {
	    this.name = name;
	    this.description = description;

	    this.osIndependency = "true".equals(osIndependency.toLowerCase());
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "name=" + name).set("arg1", "description=" + description)
		    .set("arg2", "osIndependency=" + osIndependency);
	}
    }

    public static RequestType parse(String string) throws SystemException {
	if (string != null) {
	    return new RequestType(string, "", "false");
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", "RequestType").set("method", "parse")
		    .set("arg0", "string=" + string);
	}
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public boolean isIndependent() {
	return osIndependency;
    }

    @Override
    public String toString() {
	return name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((description == null) ? 0 : description.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + (osIndependency ? 1231 : 1237);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RequestType other = (RequestType) obj;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (osIndependency != other.osIndependency)
	    return false;
	return true;
    }

}
