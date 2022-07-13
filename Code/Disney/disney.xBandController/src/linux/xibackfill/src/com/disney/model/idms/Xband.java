
package com.disney.model.idms;

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
    "state",
    "publicId",
    "productId",
    "xbandId",
    "xbandRequestId",
    "bandId",
    "guests",
    "shortRangeTag",
    "longRangeTag",
    "secondaryState"
})
public class Xband {

    @JsonProperty("state")
    private String state;
    @JsonProperty("publicId")
    private String publicId;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("xbandId")
    private Integer xbandId;
    @JsonProperty("xbandRequestId")
    private String xbandRequestId;
    @JsonProperty("bandId")
    private String bandId;
    @JsonProperty("guests")
    private List<Object> guests = new ArrayList<Object>();
    @JsonProperty("shortRangeTag")
    private String shortRangeTag;
    @JsonProperty("longRangeTag")
    private String longRangeTag;
    @JsonProperty("secondaryState")
    private String secondaryState;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("publicId")
    public String getPublicId() {
        return publicId;
    }

    @JsonProperty("publicId")
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @JsonProperty("productId")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("xbandId")
    public Integer getXbandId() {
        return xbandId;
    }

    @JsonProperty("xbandId")
    public void setXbandId(Integer xbandId) {
        this.xbandId = xbandId;
    }

    @JsonProperty("xbandRequestId")
    public String getXbandRequestId() {
        return xbandRequestId;
    }

    @JsonProperty("xbandRequestId")
    public void setXbandRequestId(String xbandRequestId) {
        this.xbandRequestId = xbandRequestId;
    }

    @JsonProperty("bandId")
    public String getBandId() {
        return bandId;
    }

    @JsonProperty("bandId")
    public void setBandId(String bandId) {
        this.bandId = bandId;
    }

    @JsonProperty("guests")
    public List<Object> getGuests() {
        return guests;
    }

    @JsonProperty("guests")
    public void setGuests(List<Object> guests) {
        this.guests = guests;
    }

    @JsonProperty("shortRangeTag")
    public String getShortRangeTag() {
        return shortRangeTag;
    }

    @JsonProperty("shortRangeTag")
    public void setShortRangeTag(String shortRangeTag) {
        this.shortRangeTag = shortRangeTag;
    }

    @JsonProperty("longRangeTag")
    public String getLongRangeTag() {
        return longRangeTag;
    }

    @JsonProperty("longRangeTag")
    public void setLongRangeTag(String longRangeTag) {
        this.longRangeTag = longRangeTag;
    }

    @JsonProperty("secondaryState")
    public String getSecondaryState() {
        return secondaryState;
    }

    @JsonProperty("secondaryState")
    public void setSecondaryState(String secondaryState) {
        this.secondaryState = secondaryState;
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
