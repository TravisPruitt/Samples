package com.disney;

import org.codehaus.jackson.annotate.JsonProperty;

public class GuestIdentifiersEntry {
	@JsonProperty("value") private String value;
	@JsonProperty("type") private String type;
}
