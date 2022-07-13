
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
    "activityReason",
    "activityReasonTime",
    "relatedXpassIds"
})
public class Reason {

    @JsonProperty("activityReason")
    private String activityReason;
    @JsonProperty("activityReasonTime")
    private String activityReasonTime;
    @JsonProperty("relatedXpassIds")
    private List<Integer> relatedXpassIds = new ArrayList<Integer>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("activityReason")
    public String getActivityReason() {
        return activityReason;
    }

    @JsonProperty("activityReason")
    public void setActivityReason(String activityReason) {
        this.activityReason = activityReason;
    }

    @JsonProperty("activityReasonTime")
    public String getActivityReasonTime() {
        return activityReasonTime;
    }

    @JsonProperty("activityReasonTime")
    public void setActivityReasonTime(String activityReasonTime) {
        this.activityReasonTime = activityReasonTime;
    }

    @JsonProperty("relatedXpassIds")
    public List<Integer> getRelatedXpassIds() {
        return relatedXpassIds;
    }

    @JsonProperty("relatedXpassIds")
    public void setRelatedXpassIds(List<Integer> relatedXpassIds) {
        this.relatedXpassIds = relatedXpassIds;
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
