package com.disney.model;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Links {
	@JsonProperty("self") private Map<String, String> self;
	@JsonProperty("profile") private Map<String, String> profile;
}
