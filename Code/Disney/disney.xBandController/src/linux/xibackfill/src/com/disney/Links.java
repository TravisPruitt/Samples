package com.disney;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Links {
	@JsonProperty("ownedGuestsProfile") private Map<String, String> ownedGuestsProfile;
	@JsonProperty("magicPlusParticipantStatusEvaluationRecords") private Map<String, String> magicPlusParticipantStatusEvaluationRecords;
	@JsonProperty("registeredProfile") private Map<String, String> registeredProfile;
	@JsonProperty("guestIdentifiers") public Map<String, String> guestIdentifiers;
	@JsonProperty("self") public Map<String, String> self;
	@JsonProperty("guestAffiliations") public Map<String, String> guestAffiliations;
	@JsonProperty("wdproFavoriteCharacter") private Map<String, String> wdproFavoriteCharacter;
	@JsonProperty("wdproAvatar") private Map<String, String> wdproAvatar;
}
