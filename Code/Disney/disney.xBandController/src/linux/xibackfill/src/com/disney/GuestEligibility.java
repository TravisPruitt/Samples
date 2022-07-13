package com.disney;

import org.codehaus.jackson.annotate.JsonProperty;

public class GuestEligibility {
	@JsonProperty("magicPlusParticipantStatusEffectiveDate") private String magicPlusParticipantStatusEffectiveDate;
	@JsonProperty("links") private Links links;
	@JsonProperty("magicPlusParticipantStatus") private String magicPlusParticipantStatus;
}
