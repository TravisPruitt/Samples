package com.disney;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class OneViewGuest {
	@JsonProperty("dateOfBirth") private String dateOfBirth;
	@JsonProperty("lastName") private String lastName;
	@JsonProperty("favoriteCharacter") private FavoriteCharacter favoriteCharacter;
	@JsonProperty("guestIdentifiers") private List<GuestIdentifiersEntry> guestIdentifiers;
	@JsonProperty("links") public Links links;
	@JsonProperty("avatar") private Avatar avatar;
	@JsonProperty("transactionalGuestProfileList") public List<FavoriteCharacter> transactionalGuestProfileList;
	@JsonProperty("ATSGuestList") private List<FavoriteCharacter> ATSGuestList;
	@JsonProperty("title") private String title;
	@JsonProperty("DMEGuestList") private List<FavoriteCharacter> DMEGuestList;
	@JsonProperty("guestEligibility") private GuestEligibility guestEligibility;
	@JsonProperty("firstName") private String firstName;
	@JsonProperty("guestType") private String guestType;
}
