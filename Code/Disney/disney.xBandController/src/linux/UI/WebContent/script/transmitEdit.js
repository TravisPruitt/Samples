
function showTransmitCommandDialog(locationId, commandId) {
	$("#transmitCommandDialog").dialog('option', 'title', 'Transmit Command ');
	$("#transmitCommandDialog").dialog('open');
	document.forms.transmitCommandForm.locationId.value = locationId;
	document.forms.transmitCommandForm.commandId.value = commandId;
	dojo.event.topic.publish('handleShowTransmitCommandForm');
}

function saveTransmitCommand(form) {
	// showProgress();
	document.forms.transmitCommandForm.action.value = "save";
	dojo.event.topic.publish('handleShowTransmitCommandForm');
}

function transmitCommandSelected()
{
	$('select#commandTypes > option:selected').each(function() {
		
		//disable/enable frequency and timeout appropriately
	   	var summary = $(this).context.id;
	   	var state = summary.split(',');
	    if (state.length == 6) {
	   		var frequencyMin = state[0].trim();
	   		var frequencyMax = state[1].trim();
	   		var frequencyProgrammable = state[2].trim();
	   		var timeoutMin = state[3].trim() / 1000;
	   		var timeoutMax = state[4].trim() / 1000;
	   		var timeoutProgrammable = state[5].trim();
	   		
	   		$freq = $('input#frequency');
	   		$freq.val("");
	   		$freq_range = $('span#frequency_range');
	   		if (frequencyProgrammable == "false"){
	   			$freq.val(frequencyMin);
	   			$freq.attr("disabled", "disabled");
	   			
	   			$freq_range.text("(msec)");
	   		} else {
	   			$freq.removeAttr("disabled");
	   			$freq_range.text("(" + frequencyMin + " - " + frequencyMax + " msec)");
	   		}
	   		
	   		$timeout = $('input#timeout');
	   		$timeout.val("");
	   		$timeout_range = $('span#timeout_range');
	   		if (timeoutProgrammable == "false"){
	   			$timeout.val(timeoutMax);
	   			$timeout.attr("disabled", "disabled");
	   			
	   			$timeout_range.text("(sec)");
	   		} else {
	   			$timeout.removeAttr("disabled");
	   			$timeout_range.text("(" + timeoutMin + " - " + timeoutMax + " sec)");
	   		}
	   	}
	    
	    //disable REPLY mode and frequency input for fast receive command
	    var command = $(this).context.value;
	    if (command === "FAST_RX_ONLY"){
	    	$select = $('select#transmitMode');
	    	$select.find('option[value="BROADCAST"]').attr("selected","selected");
	    	$select.attr("disabled","disabled");
	    } else {
	    	$select.removeAttr("disabled");
	    }
	    transmitModeSelected();
	});
}

function transmitModeSelected()
{
	$recipientCheckboxes = $("ul#locations").find(":input");
	$sstt = $("div#recipientContainer").find("input#recipient_sstt");
	var value = $('select#transmitMode').val();
	
	if (value === "BROADCAST") {
		$("div#recipientContainer").hide();
		$("#recipientContainerLabel").hide();
	} else {
		$("div#recipientContainer").show();
		$("#recipientContainerLabel").show();
	};
}

function enableThresholdSelected() 
{	
	$sstt = $("div#recipientContainer").find("input#recipient_sstt");
	
	var enableThreshold = $sstt.attr("checked");
	
	if (enableThreshold){
		$("#threshold").removeAttr("disabled");		
	} else {
		$("#threshold").attr("disabled", "disabled");
	}
}

function recalculateHaPriority()
{
	$("#transmitReadersTable tr").each(function(i, tr) {
	    // this represents the row
		$(this).children("td.haPriority").each(function(j, td) {
	       $(this).children("input:first").val(i);
	    });
	});
}

function moveReaderUpDown(readerId, obj, up)
{
	var row = obj.parents("tr:first");
    if (up) {
        row.insertBefore(row.prev());
    } else {
        row.insertAfter(row.next());
    }
    
    recalculateHaPriority();
}