package com.disney.model;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class GuestIdentifier {
	@JsonProperty("identifiers") public List<IdentifiersEntry> identifiers;
	@JsonProperty("links") private Links links;
}
