function showGuests(locationId, state, xPassOnly) {
	showProgress();
	document.forms.showGuestsForm.locationId.value = locationId;
	document.forms.showGuestsForm.state.value = state;
	document.forms.showGuestsForm.xpass.value = xPassOnly;
	dojo.event.topic.publish('handleShowGuests');
}