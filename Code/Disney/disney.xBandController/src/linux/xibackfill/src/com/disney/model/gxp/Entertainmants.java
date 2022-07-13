
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
    "entertainmentId",
    "experienceType"
})
public class Entertainmants {

    @JsonProperty("entertainmentId")
    private Integer entertainmentId;
    @JsonProperty("experienceType")
    private String experienceType;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("entertainmentId")
    public Integer getEntertainmentId() {
        return entertainmentId;
    }

    @JsonProperty("entertainmentId")
    public void setEntertainmentId(Integer entertainmentId) {
        this.entertainmentId = entertainmentId;
    }

    @JsonProperty("experienceType")
    public String getExperienceType() {
        return experienceType;
    }

    @JsonProperty("experienceType")
    public void setExperienceType(String experienceType) {
        this.experienceType = experienceType;
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
