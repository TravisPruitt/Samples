package com.disney;

import org.codehaus.jackson.annotate.JsonProperty;

public class FavoriteCharacter {
	@JsonProperty("name") private String name;
	@JsonProperty("links") private Links links;
}
