
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
    "startItem",
    "itemLimit",
    "itemCount",
    "xbands"
})
public class Xbands {

    @JsonProperty("startItem")
    private Integer startItem;
    @JsonProperty("itemLimit")
    private Integer itemLimit;
    @JsonProperty("itemCount")
    private Integer itemCount;
    @JsonProperty("xbands")
    private List<Xband> xbands = new ArrayList<Xband>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("startItem")
    public Integer getStartItem() {
        return startItem;
    }

    @JsonProperty("startItem")
    public void setStartItem(Integer startItem) {
        this.startItem = startItem;
    }

    @JsonProperty("itemLimit")
    public Integer getItemLimit() {
        return itemLimit;
    }

    @JsonProperty("itemLimit")
    public void setItemLimit(Integer itemLimit) {
        this.itemLimit = itemLimit;
    }

    @JsonProperty("itemCount")
    public Integer getItemCount() {
        return itemCount;
    }

    @JsonProperty("itemCount")
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    @JsonProperty("xbands")
    public List<Xband> getXbands() {
        return xbands;
    }

    @JsonProperty("xbands")
    public void setXbands(List<Xband> xbands) {
        this.xbands = xbands;
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
