package com.disney;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Avatar {
	@JsonProperty("name") private String name;
	@JsonProperty("links") private Links links;
}
