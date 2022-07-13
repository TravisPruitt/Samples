package com.disney.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class IdentifiersEntry {
	@JsonProperty("value") public String value;
	@JsonProperty("type") public String type;
}
