package com.disney.xband.xbrms.common.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "problem")
public class ProblemDto implements IDataTransferObject {
    private static final String NO_STACK_TRACE_MESSAGE = "There is no exception associated with this problem.";

    private String message;
    private Date timestamp;

    public ProblemDto(String message, Date timestamp, Throwable throwable) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public ProblemDto(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public ProblemDto(Date timestamp, Throwable throwable) {
        this.message = throwable != null ? throwable.getLocalizedMessage() : null;
        this.timestamp = timestamp;
    }

    @XmlValue
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlAttribute
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp(String format) {
        if (timestamp == null) {
            return "";
        }

        if (format == null || format.trim().isEmpty()) {
            format = "MM-dd-yyyy'T'HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(timestamp);
    }

    private ProblemDto() {
    }
}
