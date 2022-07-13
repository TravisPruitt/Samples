
package com.disney.model.gxp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "xpassId",
    "gxpLinkId",
    "bookingGxpLinkId",
    "reason",
    "xpassType",
    "entertainments",
    "activities",
    "returnWindow",
    "status",
    "totalRedemptionsAllowed",
    "totalRedemptionsRemaining"
})
public class Gxp {

    @JsonProperty("xpassId")
    private Integer xpassId;
    @JsonProperty("gxpLinkId")
    private Integer gxpLinkId;
    @JsonProperty("bookingGxpLinkId")
    private Integer bookingGxpLinkId;
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("xpassType")
    private String xpassType;
    @JsonProperty("entertainments")
    private List<Entertainmants> entertainments = new ArrayList<Entertainmants>();
    @JsonProperty("activities")
    private List<Activity> activities = new ArrayList<Activity>();
    @JsonProperty("returnWindow")
    private ReturnWindow returnWindow;
    @JsonProperty("status")
    private String status;
    @JsonProperty("totalRedemptionsAllowed")
    private Integer totalRedemptionsAllowed;
    @JsonProperty("totalRedemptionsRemaining")
    private Integer totalRedemptionsRemaining;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("xpassId")
    public Integer getXpassId() {
        return xpassId;
    }

    @JsonProperty("xpassId")
    public void setXpassId(Integer xpassId) {
        this.xpassId = xpassId;
    }

    @JsonProperty("gxpLinkId")
    public Integer getGxpLinkId() {
        return gxpLinkId;
    }

    @JsonProperty("gxpLinkId")
    public void setGxpLinkId(Integer gxpLinkId) {
        this.gxpLinkId = gxpLinkId;
    }

    @JsonProperty("bookingGxpLinkId")
    public Integer getBookingGxpLinkId() {
        return bookingGxpLinkId;
    }

    @JsonProperty("bookingGxpLinkId")
    public void setBookingGxpLinkId(Integer bookingGxpLinkId) {
        this.bookingGxpLinkId = bookingGxpLinkId;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("xpassType")
    public String getXpassType() {
        return xpassType;
    }

    @JsonProperty("xpassType")
    public void setXpassType(String xpassType) {
        this.xpassType = xpassType;
    }

    @JsonProperty("entertainments")
    public List<Entertainmants> getEntertainments() {
        return entertainments;
    }

    @JsonProperty("entertainments")
    public void setEntertainments(List<Entertainmants> entertainments) {
        this.entertainments = entertainments;
    }

    @JsonProperty("activities")
    public List<Activity> getActivities() {
        return activities;
    }

    @JsonProperty("activities")
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    @JsonProperty("returnWindow")
    public ReturnWindow getReturnWindow() {
        return returnWindow;
    }

    @JsonProperty("returnWindow")
    public void setReturnWindow(ReturnWindow returnWindow) {
        this.returnWindow = returnWindow;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("totalRedemptionsAllowed")
    public Integer getTotalRedemptionsAllowed() {
        return totalRedemptionsAllowed;
    }

    @JsonProperty("totalRedemptionsAllowed")
    public void setTotalRedemptionsAllowed(Integer totalRedemptionsAllowed) {
        this.totalRedemptionsAllowed = totalRedemptionsAllowed;
    }

    @JsonProperty("totalRedemptionsRemaining")
    public Integer getTotalRedemptionsRemaining() {
        return totalRedemptionsRemaining;
    }

    @JsonProperty("totalRedemptionsRemaining")
    public void setTotalRedemptionsRemaining(Integer totalRedemptionsRemaining) {
        this.totalRedemptionsRemaining = totalRedemptionsRemaining;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
