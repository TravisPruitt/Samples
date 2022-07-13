
package com.disney.model.gxp;

import java.util.HashMap;
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
    "activityId",
    "activityStatus",
    "reason"
})
public class Activity {

    @JsonProperty("activityId")
    private Integer activityId;
    @JsonProperty("activityStatus")
    private String activityStatus;
    @JsonProperty("reason")
    private Reason reason;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("activityId")
    public Integer getActivityId() {
        return activityId;
    }

    @JsonProperty("activityId")
    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    @JsonProperty("activityStatus")
    public String getActivityStatus() {
        return activityStatus;
    }

    @JsonProperty("activityStatus")
    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    @JsonProperty("reason")
    public Reason getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(Reason reason) {
        this.reason = reason;
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
