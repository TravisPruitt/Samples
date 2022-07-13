<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/form.css" />

<link type="text/css" href="script/jquery-ui-1.8.16.custom.darkblue/css/custom-theme/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />
<script type="text/javascript" src="script/jquery-ui-1.8.16.custom.darkblue/js/jquery-ui-1.8.16.custom.min.js"></script>

<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />


<script type="text/javascript" src="script/validation_utils.js"></script>
<script type="text/javascript" src="script/fg.menu.js"></script>

<link rel="stylesheet" type="text/css" href="css/locationListEdit.css" />

<script type="text/javascript" src="script/OmniServer.js"></script>
<script type="text/javascript" src="script/OmniServerCollection.js"></script>
<script type="text/javascript" src="script/NetworkAddress.js"></script>
<script type="text/javascript" src="script/TextInputSanitizer.js"></script>
<script type="text/javascript" src="script/Reader.js"></script>
<script type="text/javascript" src="script/transmitEdit.js"></script>

<script type="text/javascript">
var minThreshold = ${readerConfig.minimumThreshold};
var maxThreshold = ${readerConfig.maximumThreshold};
var minGain = ${readerConfig.minimumGain};
var maxGain = ${readerConfig.maximumGain};
var selectedLocations = Array();
dojo.event.topic.subscribe("/accessDenied", refreshPage);

var associtedOmniServerCollection = new OmniServerCollection();
var omniServerCollection = new OmniServerCollection();
var isParkEntry = false;

function refreshPage() {
	location.href = "locationlistedit.action";
}

function editLocation(locationId, showTransmitTab) {
	if (locationId !== undefined){
		document.forms.idEditLocationForm.locationId.value = locationId;
		document.forms.idEditLocationForm.readerId.value = "";
		document.forms.idEditLocationForm.showTransmitTab.value = showTransmitTab;
		dojo.event.topic.publish('handleEditLocation');
	}
	return false;
}
function editReader(element, readerId) {
	if (readerId !== undefined){
		document.forms.idEditLocationForm.readerId.value = readerId;
		document.forms.idEditLocationForm.locationId.value = "";
		document.forms.idEditLocationForm.showTransmitTab.value = false;
		dojo.event.topic.publish('handleEditLocation');
	}
}
function transmitReader(element, readerId, locationId) {
	if (readerId !== undefined){
		document.forms.idEditLocationForm.readerId.value = readerId;
		document.forms.idEditLocationForm.locationId.value = locationId;
		document.forms.idEditLocationForm.showTransmitTab.value = false;
		dojo.event.topic.publish('handleEditLocation');
	}
}
function identifyReader(element, readerId, locationId) {
	document.forms.idReaderIdentifyForm.locationId.value = locationId;
	document.forms.idReaderIdentifyForm.readerId.value = readerId;
	dojo.event.topic.publish('handleIdentifyReader');
}
function restartReader(element, readerId, locationId) {
	document.forms.idReaderRestartForm.locationId.value = locationId;
	document.forms.idReaderRestartForm.readerId.value = readerId;
	dojo.event.topic.publish('handleRestartReader');
}
function rebootReader(element, readerId, locationId) {
	document.forms.idReaderRebootForm.locationId.value = locationId;
	document.forms.idReaderRebootForm.readerId.value = readerId;
	dojo.event.topic.publish('handleRebootReader');
}
function saveLocation(form) {
	
	var valid = true;
	if (form){
		var locationNameSpan = $("span#location_name");
		if (form.idLocationSaveForm_location_name.value == ""){
			locationNameSpan.show();
			valid = false;
		} else {
			locationNameSpan.hide();
		}

        // Do some checking on the input characters of the location name.
        // Only check if we're still valid.
        if ( valid && form.idLocationSaveForm_location_name.value !== null )
        {
            if ( form.idLocationSaveForm_location_name.value.indexOf('<') >= 0 ||
                    form.idLocationSaveForm_location_name.value.indexOf('>') >= 0 )
            {
                locationNameSpan.show();
                valid = false;
            }
            
            if (valid && form.idLocationSaveForm_location_name.value == "UNKNOWN")
            {
                locationNameSpan.show();
                valid = false;
            }
        }

        // Verify that the locationId is restricted to a long.
        var locationlocationIdSpan = $("span#location_locationId");

        // It is not a required field, so only ensure that it's an
        // integer if there's SOME value.
        var testedValue = form.idLocationSaveForm_location_locationId.value;
        if ( trim(testedValue) !== null )
        {        	
        	 if ( testedValue.indexOf('<') >= 0 || testedValue.indexOf('>') >= 0 )
             {
        		 locationlocationIdSpan.show();
                 valid = false;
             }
        	 else
        	 {
        		 locationlocationIdSpan.hide();
        	 }        	
        }
        else
        {
        	locationlocationIdSpan.hide();
        }

        valid &= validateTimeout(form.idLocationSaveForm_location_errorTimeout, $("#location_errorTimeout") );
        valid &= validateTimeout(form.idLocationSaveForm_location_failureTimeout, $("#location_failureTimeout") );
        
        if ($("#maxAnalyzeGuestEventsError").length)
        {
	        // valideate vehicle location configuration
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_maxAnalyzeGuestEvents, $("#maxAnalyzeGuestEventsError"), 500, 100000);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_maxAnalyzeGuestEventsPerVehicle, $("#maxAnalyzeGuestEventsPerVehicleError"), 2, 100);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_minReadsToAssociate, $("#minReadsToAssociateError"), 2, 1000);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_onrideTimeoutSec, $("#onrideTimeoutSecError"), 1, 3600);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_vehicleTimeOffsetMs, $("#vehicleTimeOffsetMsError"), -30000, 30000);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_vehicleHoldTimeMs, $("#vehicleHoldTimeMsError"), 0, 60000);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_trainTimeoutSec, $("#trainTimeoutSecError"), 1, 3600);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_laserBreaksBeforeVehicle, $("#laserBreaksBeforeVehicleError"), 0, 1000);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_laserBreaksAfterVehicle, $("#laserBreaksAfterVehicleError"), 0, 1000);
	        valid &= validateNumeric(form.idLocationSaveForm_vaLocationConfig_carsPerTrain, $("#carsPerTrainError"), 0, 1000);
        }

        // Store the sequences.
        $form = $("form#idLocationSaveForm");

        // clear first, so they don't accumulate between requests
        $form.find("input[name=successSequences]").remove();

        // Ensure that there's timeouts and ratios that add up
        if ( $("#selectCustomSequenceList").val() == "true" )
        {
            var ratioTotal = 0;
            $form.find(".subSequenceItemContainer").each( function(index, elem) 
            {
                var name = $(elem).find(".subSequenceSelect").val();
                var timeoutElem = $(elem).find(".timeout")
                var ratio = $(elem).find(".ratio").val();

                var subTotal;

                if (validateTimeout(timeoutElem.get(0), $("#location_successsequences_invalid")) == false)
                    valid = false;


                if (ratio == null)
                    valid = false;
                else
                {
                    ratio = trim(ratio);
                    if (ratio == "" ||
                        !numericOnlyCheck(ratio) ||
                        !isPositive(parseInt(ratio)))
                    {
                        valid = false;
                    }
                }

                if (valid) {
                    subTotal = parseInt(ratio);
                    ratioTotal += subTotal;
                }
            });
            if (ratioTotal != 100) {
                valid = false;
                $("span#location_successsequences_invalid").show();
            }
            else {
                $("span#location_successsequences_invalid").hide();
            }

            if (valid)
            {
                var sequenceIndex = 0;
                $form.find(".subSequenceItemContainer").each(function(index, elem)
                {
                    var name = $(elem).find(".subSequenceSelect").val();
                    var timeout = $(elem).find(".timeout").val();
                    var ratio = $(elem).find(".ratio").val();

                    var value = name + "," + timeout + "," + ratio;
                    $form.append('<input type="hidden" id="successSequences_' + sequenceIndex + '" name="successSequences" value="' + value + '"/>');

                    sequenceIndex++;
                });
            }
        }
    }
	
	if (valid){		
		dojo.event.topic.publish('handleLocationSave');
		$("#locationErrorsWarning").hide();
	}
	else{
		$("#locationErrorsWarning").show();
	}
}

function validateTimeout(textfield, errorspan) {
    var valid = true;

    if ( trim(textfield.value) == null ||
            trim(textfield.value) == "" ||
            !numericOnlyCheck(textfield.value) ||
            !isPositive(parseInt(textfield.value)))
    {
        errorspan.show();
        valid = false;
    }
    else
    {
        errorspan.hide();
    }
    
    return valid;
}

function validateNumeric(textfield, errorspan, min, max) {
    var valid = true;

    if ( trim(textfield.value) == null ||
         trim(textfield.value) == "" ||
         !numericOnlyCheck(textfield.value) ||
          trim(textfield.value) < min ||
          trim(textfield.value) > max)
    {
        errorspan.show();
        valid = false;
    }
    else
    {
        errorspan.hide();
    }
    
    return valid;
}

function clearReaderTest(formObj) {
	
	$element = $("#idReaderTestStatus");
	if ($element.length != 0) {
		$element.hide();
	}
	
	$form = $("form#idReaderSaveForm");
	if ($form.length == 0){
		return;
	}
	
	$form.find("#idReaderSaveForm_reader_lastReaderTestTime").val("");
	$form.find("#idReaderSaveForm_reader_lastReaderTestSuccess").val("");
	$form.find("#idReaderSaveForm_reader_lastReaderTestUser").val("");
	
	saveReader(formObj);
}

function saveReader(form) {
	
	var valid = true;
	
	try
	{
		$form = $("form#idReaderSaveForm");
		if ($form.length == 0){
			return;
		}
		
		$errorSpans = $form.find("span.fieldError");
		
		// hide all old error messages
		$errorSpans.each(function(){
			$(this).hide();
		});
		
		var reader = new Reader(minThreshold,maxThreshold,minGain,maxGain);
		reader.setName($form.find("#idReaderSaveForm_reader_readerId").val());
		reader.setIpAddress($form.find("#idReaderSaveForm_reader_ipAddress").val());
		reader.setPort($form.find("#idReaderSaveForm_reader_port").val());
		reader.setMacAddress($form.find("#idReaderSaveForm_reader_macAddress").val());
		reader.setSsThreshold($form.find("#idReaderSaveForm_reader_signalStrengthThreshold").val());
		reader.setGain($form.find("#idReaderSaveForm_reader_gain").val());
		reader.setLane($form.find("#idReaderSaveForm_reader_lane").val());
		reader.setDeviceId($form.find("#idReaderSaveForm_reader_deviceId").val());
		reader.setType($form.find("#idReaderSaveForm_readerType option:selected").val());
		reader.setEnabled($form.find("#idReaderSaveForm_reader_enabled").val());
		reader.setModelData($form.find("#idReaderSaveForm_reader_modelData").val());
		reader.setBioDeviceType($form.find("#idReaderSaveForm_reader_bioDeviceType").val());
		
		valid = reader.isValid(true);
		
		if (!valid)
		{
			$errorSpans.each(function(){
				$this = $(this);
				if ($this.attr("id").search("reader_readerId") == 0){
					if (reader.invalid["name"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_deviceId") == 0){
					if (reader.invalid["deviceId"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_lane") == 0){
					if (reader.invalid["lane"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_ipAddress") == 0){
					if (reader.invalid["ipAddress"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_port") == 0){
					if (reader.invalid["port"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_signalStrengthThreshold") == 0){
					if (reader.invalid["ssThreshold"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_gain") == 0){
					if (reader.invalid["gain"]){
						$this.show();
					}
				} else if ($this.attr("id").search("reader_bioDeviceType") == 0){
					if (reader.invalid["bioDeviceType"]){
						$this.show();
					}
				}
			});
		};
	} catch (exception){
		valid = false;
	} finally {
		reader = null;
		$form = null;
	}
	
	if (valid){
		if (isParkEntry)
		{
			try {
				if (associtedOmniServerCollection != null)
				{
					$form = $("form#idReaderSaveForm");
					
					// clear first, so they don't accumulate between requests
					$form.find("input[name=addedOmniServers]").remove();
					
					for (var i = 0; i < associtedOmniServerCollection.getCollection().length; i++)
					{
						var os = associtedOmniServerCollection.getOmniAtIndex(i);
						
						if (os === undefined){
							continue;
						}
						
						var value = os.getId() + ":" + i;
						$form.append('<input type="hidden" id="addedOmniServers_' + os.getId() + '" name="addedOmniServers" value="' + value + '"/>');
					}
				}
			} catch (exception) {
				Debugger.log(exception);
			} finally {
				os = null;
				value = null;
			};
		}
		
		dojo.event.topic.publish('handleReaderSave');
	};
}

function addReader(element, locationId) {
	if (element !== undefined){
		
		$("div#idProgress").toggle();
		
		if (element.type === "select-one"){
			document.forms.idAddReaderForm.locationId.value = locationId;
			document.forms.idAddReaderForm.readerId.value = element.value;
			document.forms.idAddReaderForm.submit();
		}
	}
}
function deleteReader(readerId, locationId, readerName) {
	var message = "You are about to permanently delete a reader " + readerName;
	
	var userResponse = confirm(message);
	if (userResponse === true){
		$("div#idProgress").toggle();
		
		document.forms.idReaderDeleteForm.locationId.value = locationId;
		document.forms.idReaderDeleteForm.readerId.value = readerId;
		document.forms.idReaderDeleteForm.submit();
	}
}

function unlinkReader(readerId, locationId, readerName) {

    var message = "Unlinking a reader is an asynchronous operation. You will need to refresh your browser (click 'Edit Locations') to see an accurate representation of the xBRC state.\n\nPress OK to unlink selected reader (" + readerName + ").";

    var userResponse = confirm(message);
    if (userResponse === true) {
        $("div#idProgress").toggle();

        document.forms.idReaderUnlinkForm.locationId.value = locationId;
        document.forms.idReaderUnlinkForm.readerId.value = readerId;
        document.forms.idReaderUnlinkForm.submit();
    }
}

function replaceReader(readerId, locationId, readerName) {
	$("#replaceReaderDialog").dialog('option', 'title', 'Replace Reader ');
	$("#replaceReaderDialog").dialog('open');
	$("#readerToReplace").html(readerName);
	
	document.forms.idReplaceReaderForm.readerId.value = readerId;
	document.forms.idReplaceReaderForm.locationId.value = locationId;
	document.forms.idReplaceReaderForm.readerName.value = readerName;
}

function applyReplaceReader(newReaderId, newReaderName) {
	
	if (document.forms.idReplaceReaderForm.readerId.value == newReaderId) {
		alert("Cannot replace a reader with itself.");
		return;
	}
	
	document.forms.idReplaceReaderForm.newReaderId.value = newReaderId;
	
	var readerName = document.forms.idReplaceReaderForm.readerName.value;
	var message = "Press OK to replace selected reader (" + readerName + ") with new reader (" + newReaderName + ").";
	
	var userResponse = confirm(message);
	if (userResponse === true) {
		document.forms.idReplaceReaderForm.submit();
	}
}

function deleteLocation(locationId, locationName){
	var message = "Press OK if you want to permanently delete location " + locationName;
	
	var userResponse = confirm(message);
	if (userResponse === true){
		$("div#idProgress").toggle();
		
		var form = $("form#idLocationDeleteForm");
		if (form){
			var hidden = $("form#idLocationDeleteForm input#idLocationDeleteForm_locationId");
			if (hidden){
				hidden.attr("value", locationId);
				form.submit();
			}
		}
	}
}

function deleteCommand(commandId, locationId) {
	var message = "You are about to permanently delete command id " + commandId;
	
	var userResponse = confirm(message);
	if (userResponse === true){
		$("div#idProgress").toggle();
		
		document.forms.idCommandDeleteForm.locationId.value = locationId;
		document.forms.idCommandDeleteForm.commandId.value = commandId;
		document.forms.idCommandDeleteForm.submit();
	}
}

function addTransmitCommand(locationId, locationName)
{
	$("#addTransmitCommand").dialog({
		height: 300,
		width: 700,
		autoOpen: false,
		hide: "slide",
		modal: true,
		resizable: true,
		title: "Add Transmit Command",
	});
	
	$("#locationToAddCommandTo").html(locationName);
	
	$("#addTransmitCommand").dialog('open');
	
	enableThresholdSelected();
	
	transmitCommandSelected();
}



function transmitReaderSave()
{
	dojo.event.topic.publish('handleTransmitReaderSave');
}

function byLocationSelected() 
{
}

function openAddOmniServerDilog()
{	
	$("#add-omni-dialog").dialog('open');
}

function showAssociatedOmnisList()
{
	if (associtedOmniServerCollection === undefined ||
			associtedOmniServerCollection.getLength() === 0){
		return;
	}
	
	// add a visual confirmation for the user
	$omniList = $("ul#omniServers");
	
	// remove all li elements
	$omniListLis = $omniList.children("li").remove();
	
	// enter them all back in order of priority
	var collection = associtedOmniServerCollection.getCollection();
	for (var i = 0; i < collection.length; i++){
		var os = collection[i];
		if (os === undefined){
			continue;
		}
		
		$omniList.append(
			$('<li id="omniServer_' + os.getId() + '" class="lightBlueBackground paddingSmall lightGreyBorder round greyText">').append('<span class="left">' 
					+ '<em style="">' + associtedOmniServerCollection.getPriority(os.getId()) + '.&nbsp;&nbsp;&nbsp;&nbsp;</em>' + os.getHostname()
					+ '</span><a onclick="removeOmniServer(' + os.getId() + ')" class="right" style="text-decoration:none;font-weight:bold;cursor:pointer;">x</a>')
			);
	}
	$omniList.show();
}

function addOmniServer(selected, priority)
{
	try {
		// keep track for selected omnis for when the reader is saved
		var selected = $("select#selectOmni option:selected").val();
		var priority = $("select#omniPriority option:selected").val();
		
		var omniServer = omniServerCollection.getOmni(selected);
		
		if (omniServer === null){
			return;
		}
		
		associtedOmniServerCollection.addOmni(
				omniServer.getId(),
				omniServer.getHostname(),
				omniServer.getDescription(),
				omniServer.getActive(),
				omniServer.getPort(),
				omniServer.getNotActiveReason(),
				priority);
		
		// add a visual confirmation for the user
		showAssociatedOmnisList();
		
		// remove from the available omnies collection
		omniServerCollection.removeOmni(omniServer.getId());
		
		// make this priority unavailable
		$("select#omniPriority option#omniPriority_" + priority).remove();
		
		// remove from tha available omnis drop down
		$omniSelectOption = $("select#selectOmni option#selectOmni_" + omniServer.getId()).remove();
		
		$("#add-omni-dialog").dialog('close');
		
	} catch (exception) {
		Debugger.log(exception);
	} finally {
		omniServer = null;
		selected = null;
		priority = null;
		collection = null;
		os = null;
	}
}

function removeOmniServer(omniId) {
	if (omniId === undefined){
		return;
	}
	
	try {
		var omniServer = associtedOmniServerCollection.getOmni(omniId);
		
		if (omniServer === null){
			return;
		}
		
		var index = omniServerCollection.addOmni(
				omniServer.getId(),
				omniServer.getHostname(),
				omniServer.getDescription(),
				omniServer.getActive(),
				omniServer.getPort(),
				omniServer.getNotActiveReason());
		
		var priority = index + 1;
		
		// remove from the available omnies collection
		associtedOmniServerCollection.removeOmni(omniServer.getId());
		
		$omniListItem = $("ul#omniServers li#omniServer_" + omniServer.getId()).remove();
		
		// add it to the omnis still for graps drop down
		$omniSelect = $("select#selectOmni");
		$omniSelect.append(
			$('<option value="' + omniServer.getId() + '" id="selectOmni_' + omniServer.getId() + '">' + omniServer.getHostname() + '</option>')
		);
		
		// add back the freedup up priority
		$omniPrioritySelect = $("select#omniPriority");
		$omniPrioritySelect.append(
			$('<option value="' + index + '" id="omniPriority_' + index + '">' + priority + '</option>')
		);
		
	} catch (exception) {
		Debugger.log(exception);
	} finally {
		omniServer = null;
		priority = null;
	}
}

function initOmniServerElements(omnisForGrabsCollection,omnisAssociated) {
	
	$("#add-omni-dialog").dialog({
		autoOpen: false,
		closeOnEscape: true,
		draggable: true,
		resizable: true,
		position: 'center',
		title: 'Add an Omni Server',
		show: 'slide',
		width: 500,
	});
	
	isParkEntry = true;
	
	associtedOmniServerCollection = omnisAssociated;
	
	// clear the old collections
	omniServerCollection = new OmniServerCollection();
	
	$omniPrioritySelect = $("select#omniPriority");
	$omniPrioritySelect.children("option").remove();
	
	$omniSelect = $("select#selectOmni");
	$omniSelect.children("option").remove();
	
	try {
		if (omnisForGrabsCollection.getLength() > 0){
			
			var index = associtedOmniServerCollection.getNextAvailableIndex(associtedOmniServerCollection.getLength());
			var priority = index + 1;
			
			for (var i = 0; i < omnisForGrabsCollection.getCollection().length; i++){
				var omniForGrabs = omnisForGrabsCollection.getOmniAtIndex(i);
				if (omniForGrabs == undefined){
					continue;
				}
				
				index = omniServerCollection.addOmni(
						omniForGrabs.getId(),
						omniForGrabs.getHostname(),
						omniForGrabs.getDescription(),
						omniForGrabs.getActive(),
						omniForGrabs.getPort(),
						omniForGrabs.getNotActiveReason(),
						index);
				
				priority = index + 1;
				
				$omniPrioritySelect.append(
					$('<option value="' + index + '" id="omniPriority_' + index + '">' + priority + '</option>')
				);
				
				$omniSelect.append(
					$('<option value="' + omniForGrabs.getId() + '" id="selectOmni_' + omniForGrabs.getId() + '">' + omniForGrabs.getHostname() + '</option>')
				);
				
				index += 1;
			};
		};
	} catch (exception) {
		Debugger.log(exception);	
	} finally {
		index = null;
		priority = null;
	}
	
	showAssociatedOmnisList();
}

function typeChanged()
{
	try {
		$select = $("select#idReaderSaveForm_readerType");
		var selectedHtml = $select.find("option:selected").html();
		
		if (selectedHtml === "Long Range") {
			$("#readerXBR").show();
			$("input#idReaderSaveForm_reader_deviceId").hide();
			$('label[for="idReaderSaveForm_reader_deviceId"]').hide();

		} else {
			$("#readerXBR").hide();
			$("input#idReaderSaveForm_reader_deviceId").show();
			$('label[for="idReaderSaveForm_reader_deviceId"]').show();
		};
		
		if (selectedHtml === "Tap + xBio"){
			$("#configureOmnis").show();
   			$("#readerXBIO").show();
		} else {
			$("#configureOmnis").hide();
			$("#readerXBIO").hide();
		};
	
	} catch (exception) {
		Debugger.log(exception);
	} finally {
		$select = null;
		selectedHtml = null;
	};
}

function onChangeCustomSequenceList() {
    var useSequenceList = $("#selectCustomSequenceList").val();
    if ( useSequenceList == "true" ) {
        $("#locationSubSequencesContainer").show();
    }
    else {
        $("#locationSubSequencesContainer").hide();
    }
}

function updateCustomSequenceListRemove()
{
    var sequences = $(".subSequenceItemContainer");
    if (sequences.length > 1)
        sequences.find(".remove").show();
    else
        sequences.find(".remove").hide();
}

function onRemoveSequence( elem ) {
    // Confirm that the sequence should be deleted.
    var message = "You are about to permanently delete a sequence.";

    var userResponse = confirm(message);
    if (userResponse === true){
        // Remove the sequence.
        $(elem).parent().parent().remove();

        updateCustomSequenceListRemove();
    }
}

function onAddSequence() {
    // Enable the delete button.
    var newSequence = $(".subSequenceItemContainer").first().clone();
    $(".subSequenceItemContainer").first().find(".remove").show();
    $(newSequence).find(".remove").show();
    $(".subSequenceItemContainer").last().after(newSequence);
}

function enableVehicleAssociation( checkbox ) {
	if (checkbox.checked === true) {
		$("#vehicleAssociationDiv").show();
	}
	else {
		var userResponse = confirm("Are you sure you want to delete all vehicle association settings for this location?");
		if (userResponse === false){
			checkbox.checked = true;
			return false;
		}
		$("#vehicleAssociationDiv").hide();
	}
}

function enableLocationEvents( checkbox ) {
    if (checkbox.checked === true) {
        $("#locationEventsDiv").show();
    }
    else {       
        $("#locationEventsDiv").hide();
    }
}

function updateLocationName(locationId,locationName){
	if (locationId === undefined || locationName === undefined)
		return;
	
	var locationAccordionTitle = $("#locationName_" + locationId);
	if (locationAccordionTitle.length == 0)
		return;
	
	if (locationAccordionTitle.html() === locationName)
		return;
	
	locationAccordionTitle.html(locationName);
}

function updateReaderName(readerId,readerName){
	if (readerId === undefined || readerName === undefined)
		return;
	
	var readerAccordionTitle = $("#menu-" + readerId);
	if (readerAccordionTitle.length == 0)
		return;
	
	var readerAccordionTitleElements = readerAccordionTitle.html().split("</span>");
	if ($.trim(readerAccordionTitleElements[1]) === readerName)
		return;
	
	readerAccordionTitle.html(readerAccordionTitleElements[0] + " </span>" + readerName);
}

$(document).ready(function()
{
	$("#replaceReaderDialog").dialog({height:400,width:400,autoOpen:false});
	
	$("#transmitCommandDialog").dialog({height:360,width:480,autoOpen:false});
	
	$("#locationAccordion").accordion({ 
		header: "h3",
		autoHeight: false,
		navigation: true,
		navigationFilter: function(){
			return this.href == location.href + "#" + ${locationId};
		}
	});	

	<s:iterator var="location" value="locMap" status="statLoc">
		<s:iterator var="reader" value="value"> 
		$('#menu-${reader.id}').menu({
			content: $('#content-${reader.id}').html(),		
			maxHeight: 180
		});
		</s:iterator>
	</s:iterator>	
});

</script>
