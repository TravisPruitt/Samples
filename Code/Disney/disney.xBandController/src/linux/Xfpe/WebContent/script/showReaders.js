
function tapReader(readerId, action, bandId, bioData) {
	bandId = prompt("Enter the band id");
	document.forms.readerEventForm.readerId.value = readerId;
	document.forms.readerEventForm.action.value = action;
	document.forms.readerEventForm.bandId.value = bandId;
	document.forms.readerEventForm.bioData.value = bioData;
	dojo.event.topic.publish('handleReaderEvent');
}

function scanReader(readerId, action, bandId, bioData) {
	bioData = prompt("Enter the fingerprint template data");
	document.forms.readerEventForm.readerId.value = readerId;
	document.forms.readerEventForm.action.value = action;
	document.forms.readerEventForm.bandId.value = bandId;
	document.forms.readerEventForm.bioData.value = bioData;
	dojo.event.topic.publish('handleReaderEvent');
}

function logoutCastMember(readerId) {
	document.forms.readerEventForm.readerId.value = readerId;
	document.forms.readerEventForm.action.value = "logout";
	dojo.event.topic.publish('handleReaderEvent');
}

function setReaderLight(readerId, xfpeLight, fpLightOn)  {	
	
	blue = document.getElementById('id_' + readerId + '_blue');
	green = document.getElementById('id_' + readerId + '_green');
	fpwhite = document.getElementById('id_' + readerId + '_fpwhite');
	
	toggle--;
	if (toggle < 0) {
		toggle = 4;
	}
	
	if (fpLightOn === true && toggle > 1) {
		fpwhite.style.display = 'block';
	}
	else {
		fpwhite.style.display = 'none';
	}
	
	if (xfpeLight === 'blue') {
		blue.style.display = 'block';
	}
	else {
		blue.style.display = 'none';
	}
	
	if (xfpeLight === 'green') {
		green.style.display = 'block';
	}
	else {
		green.style.display = 'none';
	}
}

function stopTest(locationId) {
	window.location.href = "showreaders?locationId=" + locationId + "&action=stopTest";
}

function startTest(locationId, suiteId, maxReaders) {	
	window.location.href = "showreaders?locationId=" + locationId + 
		"&suiteId=" + suiteId +
		"&maxReaders=" + maxReaders +
		"&action=startTest";
}

function setCurrentTestDesc(readerId, testDesc, actionDesc) {
	e = document.getElementById("idCurrentAction_" + readerId);
	e.innerHTML = actionDesc;
	
	e = document.getElementById("idCurrentTest_" + readerId);
	e.innerHTML = testDesc;
}

function setOmniStatus(readerId, connected, loggedIn) {
	e = document.getElementById("idOmniStatus_" + readerId);
	b = document.getElementById("idLogoutButton_" + readerId);
	
	if (loggedIn) {
		e.innerHTML = "Logged in";
		if (b.style.display != 'block')
			b.style.display = 'block';
	}
	else {
		b.style.display = 'none';
		if (connected) {
			e.innerHTML = "Connected";
		} else {
			e.innerHTML = "Not connected";
		}
	}
}

function allTestHaveFinished(locationId) {
	// If the stop test button is showing then we need to refresh the showReaders page beacuse
	// all tests have stopped.
	var stopTestButton = document.getElementById("stopTestButton");
	if (stopTestButton) {
		window.location.href = "showreaders?locationId=" + locationId;
	}
}