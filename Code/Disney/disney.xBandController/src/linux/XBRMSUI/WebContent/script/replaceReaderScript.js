$(document).ready(function() 
{
	$('#parks').get(0).selectedIndex = 0;
	$('#parks').dropkick({
		width: '153',
		change: function (value, label){
			
			if (value === "-1")
			{
				clearVenues();
				focusOnParks();
				return;
			}
			
			$.ajax({
				url: "replaceReader-parkSelected.action",
                cache: false,
				data: {
					"parkId":value,
				},
				success: function(data){
					// refresh the venues select list
					$venues = $('#venues');
					// remove old options
					$venues[0].options.length = 0;	// works even with EI6, don't touch it!
					
					// add new options
					$venues.append('<option value="-1" selected="selected">-- Select Venue --</option>');
					var venues = data.venues;
					for (var i = 0; i < venues.length; i++)
					{
						$venues.append('<option value="' + venues[i].id + '">' + venues[i].name + '</option>');
					}
					
					// refresh the dropdown
					$('#venues').dropkick('reload');
					
					clearExistingReadersList();
					
					// move focus from parks selector to venues selector
					focusOnVenues();
				},
				error: function(data){
					if (data.status === 401 || data.responseText.indexOf("Authentication") != -1) {
						alert("Your session has expired. Please use the 'Logout' link to start a new session.");
					}
					else {
						alert("Failed to retrieve a list of venues! Please clear your selections and try again.");
					}
				},
				dataType: "json",
				type: "GET",
			});
		}
	});
	$('#parks').dropkick('focusOn');
	
	$('#venues').dropkick({
		width: '153',
		change: function (value, label){
			
			if (value === "-1")
			{
				clearExistingReadersList();
				focusOnVenues();
				return;
			}
			
			$.ajax({
				url: "replaceReader-venueSelected.action",
                cache: false,
				data: {
					"parkId": $('#parks').val(),
					"xbrcId": value,
				},
				success: function(data){
					// refresh the venues select list
					$readers = $('#readers');
					// remove old options
					$readers[0].options.length = 0;	// works even with EI6, don't touch it!
					// add new options
					$readers.append('<option value="-1" selected="selected">-- Select Reader --</option>');
					var readers = data.existingReaders;
					for (var i = 0; i < readers.length; i++)
					{
						$readers.append('<option value="' + readers[i].mac + '">' + readers[i].name + '</option>');
					}
					// refresh the dropdown
					$("#readers").dropkick('reload');
					
					clearReplacementReaders();
					
					focusOnReaders();
				},
				error: function(data){
					if (data.status === 401 || data.responseText.indexOf("Authentication") != -1) {
						alert("Your session has expired. Please use the 'Logout' link to start a new session.");
					}
					else {
						alert("Failed to retrieve a list of venues! Please clear your selections and try again.");
					}
				},
				dataType: "json",
				type: "GET",
			});
		}
	});
	
	$('#readers').dropkick({
		width: '153',
		change: function (value, label){	
			
			clearReplacementSummary();
			
			if (value === "-1")
			{
				clearReplacementReaders();
				focusOnReaders();
				return;
			}
			
			$.ajax({
				url: "replaceReader-existingReaderSelected.action",
                cache: false,
				data: {
					"parkId": $('#parks').val(),
					"xbrcId": $('#venues').val(),
					"existingReaderMac": value,
				},
				success: function(data){
					var reader = new Reader();
					reader.xbrcId = $.trim($("#venues").find(":selected").text());
					reader.name = data.existingReader.name;
					reader.typeDescription = data.existingReader.type;
					reader.ip = data.existingReader.ip;
					reader.mac = data.existingReader.mac;
						
					existingReaderSelected(reader);
					
					populateReplacementReadersTable(data.replacementUnlinkedReaders, reader.typeDescription);
				
					$('#venues').dropkick('focusOff');
					$('#parks').dropkick('focusOff');
					$('#readers').dropkick('focusOff');
				},
				error: function(data){
					if (data.status === 401 || data.responseText.indexOf("Authentication") != -1) {
						alert("Your session has expired. Please use the 'Logout' link to start a new session.");
					}
					else {
						alert("Failed to retrieve a list of venues! Please clear your selections and try again.");
					}
				},
				dataType: "json",
				type: "GET",
			});
		}
	});
	
	$("#confirmCancelDialog").dialog({
		title: "Confirm Cancel Operation",
		modal: true,
		autoOpen: false,
		buttons: [
		    {
				text: "Yes",
				click: function(){
					cancel();
					$(this).dialog("close");
				},
			},
			{
				text: "No",
				click: function(){
					$(this).dialog("close");
				},
			}
		],
	});
	
	$("#confirmReaplceDialog").dialog({
		title: "Confirm Replace Operation",
		modal: true,
		autoOpen: false,
		buttons: [
		    {
				text: "Yes",
				click: function(){
					$(this).dialog("close");
					replace();
				},
			},
			{
				text: "No",
				click: function(){
					$(this).dialog("close");
				},
			}
		],
	});
	
	$errorDialog = $("#errorDialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: [
		    {
				text: "OK",
				click: function(){
					$(this).dialog("close");
				},
			},
		],
	});
});

function populateReplacementReadersTable(replacementUnlinkedReaders, readerType)
{
	var replacementReaderData = new Array();
	if (replacementUnlinkedReaders.length > 0)
	{
		for (var i = 0; i < replacementUnlinkedReaders.length; i++)
		{
			var status = replacementUnlinkedReaders[i].status.toLowerCase();
			if (status === "green"){
				statusValue = 0;
			} else if (status === "yellow"){
				statusValue = 1;
			} else if (status === "red"){
				statusValue = 2;
			} else {
				statusValue = -1;
			}
				
			replacementReaderData[i] = new Array();
			replacementReaderData[i][0] = replacementUnlinkedReaders[i].mac;
			replacementReaderData[i][1] = replacementUnlinkedReaders[i].ip;
			replacementReaderData[i][2] = '<img src="images/' + status + '-light.png"><span style="display:none">' + statusValue + '</span></img>';
			if (readerType.indexOf("Tap") >= 0){
				replacementReaderData[i][3] = '<img class="clickable" src="images/light-16px.png" onclick="lightUp(\'' + replacementUnlinkedReaders[i].mac + '\')"/>';
				replacementReaderData[i][4] = '<input type="radio" name="replacementReader" value="' + replacementUnlinkedReaders[i].ip + '-' + replacementUnlinkedReaders[i].mac + '" onclick="replacementReaderSelected()"/>';
			} else {
				replacementReaderData[i][3] = '<input type="radio" name="replacementReader" value="' + replacementUnlinkedReaders[i].ip + '-' + replacementUnlinkedReaders[i].mac + '" onclick="replacementReaderSelected()"/>';
			}
		}
	}
		
		$replaceReadersTable = $("#replacement");
		
			var header = new Array();
			header.push({"sTitle":"MAC Address"});
			header.push({"sTitle":"Address"});
			header.push({
				"sTitle":"Health",
				"sClass":"center",
				"sWidth":"50px",
			});
			if ((readerType.indexOf("Tap") >= 0))
			{
				header.push({
					"sTitle":"Light Up",
					"sClass": "center",
					"sWidth":"50px",
					"bSortable":false,
					"bSearchable":false
				});
			}
			header.push({
				"sTitle":"Select",
				"sClass": "center",
				"sWidth":"50px",
				"bSortable":false,
				"bSearchable":false
			});
			
			// create the table with the new data
			if ($replaceReadersTable.length == 0)
			{
				$("#replacement-container h2").after('<table id="replacement" class="lightGreyBorder round" style="margin: 5px auto 0 auto; width: 657px;"></table>');
			}
			else
			{
				$replaceReadersTable.dataTable().fnDestroy(true);
				$("#replacement-container h2").after('<table id="replacement" class="lightGreyBorder round" style="margin: 5px auto 0 auto; width: 657px;"></table>');
			}
			
			$("#replacement").dataTable({
				"aaData": replacementReaderData,
				"aoColumns": header,
				"bPaginate": false,
				"bFilter": false,
				"oLanguage": {
					"sEmptyTable": "There are no unlinked readers available.",
				},
			});
			
			$("#replacement-container").show();
}

function existingReaderSelected(reader)
{
	$("#existingReader li").each(function(idx, li){
		$(this).children("span").each(function(i, span){
			if ($(this).attr("id") === "erVenue"){
				$(this).text(reader.xbrcId);
			}
			else if ($(this).attr("id") === "erVenue"){
				$(this).text(reader.xbrcId);
			}
			else if ($(this).attr("id") === "erName"){
				$(this).text(reader.name);
			}
			else if ($(this).attr("id") === "erType"){
				$(this).text(reader.typeDescription);
			}
			else if ($(this).attr("id") === "erMac"){
				$(this).text(reader.mac);
			}
			else if ($(this).attr("id") === "erIp"){
				$(this).text(reader.ip);
			}
		});
	});
}

function replacementReaderSelected()
{
	var value = $("#replacement").find("input[type='radio']:checked").val();
	
	var replacementReader = value.split('-');
	var replacementReaderIp = replacementReader[0];
	var replacementReaderMac = replacementReader[1];
	
	$("#replacementReader li").each(function(idx, li){
		$(this).children("span").each(function(i, span){
			if ($(this).attr("id") === "rrIp"){
				$(this).text(replacementReaderIp);
			}
			else if ($(this).attr("id") === "rrMac"){
				$(this).text(replacementReaderMac);
			}
		});
	});
}

function cancel()
{
	clearParkSelection();
	
	focusOnParks();
}

function replaceClicked()
{
	var parkSelected = $('#parks').val();
	var venueSelected = $('#venues').val();
	var existingReaderSelected = $('#readers').val();
	var replacementReaderSelected = $("#replacement").find("input[type='radio']:checked").length == 0;
	
	if (parkSelected === "-1" || venueSelected === "-1" || 
			existingReaderSelected === "-1" || replacementReaderSelected === true)
	{
		$errorDialog.dialog("option", "title", "Missing Selections");
		$errorDialog.text("Park, Venue, Reader to be Replaced, and Replacement Reader must be selected for the reader replacement action.");
		$errorDialog.dialog('open');
	}
	else
	{
		$('#confirmReaplceDialog').dialog('open');
	}
}

function replace()
{
	var run = true;
	
	var parkId, xbrcId, existingReaderMac, replacementReaderMac, replacementReader;
	
	try
	{
		parkId = $('#parks').val();
		xbrcId = $('#venues').val();
		existingReaderMac = $("#readers").val().split('-')[0];
		
		replacementReader = $("#replacement").find("input[type='radio']:checked").val().split('-');
		replacementReaderMac = replacementReader[1];
		
		if (parkId == "undefined" || xbrcId == "undefined" || existingReaderMac == "undefined" || replacementReaderMac == "undefined")
		{
			run = false;
		}
	} catch (exception){
		run = false;
	}
	
	if (run)
	{
		$.ajax({
			url: "replaceReader-runReplace.action",
            cache: false,
			data: {
				"parkId": parkId,
				"xbrcId": xbrcId,
				"existingReaderMac": existingReaderMac,
				"replacementReaderMac": replacementReaderMac,
			},
			success: function(data){
				clearParkSelection();
				$("#replacement-container").hide();
				focusOnParks();
				
				$errorDialog.dialog("option", "title", "Success!");
				$errorDialog.text("Reader " + data.existingReaderMac + " has been successfully replaced with reader " + replacementReaderMac + ".");
				$errorDialog.dialog('open');
			},
			error: function(data){
				$errorDialog.dialog("option", "title", "Error!");

                if(data.responseText.indexOf("readers with identical names")) {
                    $errorDialog.text("Replacement cannot be performed for readers with identical names. You must rename one of the readers first!");
                }
                else {
				    $errorDialog.text("Failed to replace reader " + existingReaderMac + " with reader " + replacementReaderMac + ".");
                }

				$errorDialog.dialog('open');
			},
			dataType: "json",
			type: "GET",
		});
	}
}

function clearParkSelection()
{
	$('#parks option[value="-1"]').prop('selected', true);
	$("#parks").dropkick('reload');
	
	clearVenues();
}

function clearVenues()
{
	// venues drop down
	$venues = $('#venues');
	// remove old options
	$venues[0].options.length = 0;	// works even with EI6, don't touch it!				
	// add new options
	$venues.append('<option value="-1" selected="selected">-- Select Venue --</option>');			
	// refresh the dropdown
	$('#venues').dropkick('reload');
	
	clearExistingReadersList();
}

function clearExistingReadersList()
{
	// readers drop down
	$readers = $('#readers');
	// remove old options
	$readers[0].options.length = 0;	// works even with IE6, don't touch it!
	// add new options
	$readers.append('<option value="-1" selected="selected">-- Select Reader --</option>');
	// refresh the dropdown
	$("#readers").dropkick('reload');
	
	clearReplacementReaders();
}

function clearReplacementReaders()
{
	$replaceReadersTable = $("#replacement");
	if ($replaceReadersTable.length > 0)
	{
		$replaceReadersTable.dataTable().fnClearTable(true);
	}
	
	clearReplacementSummary();
}

function clearReplacementSummary()
{
	$("#existingReader li").each(function(idx, li){
		$(this).children("span").each(function(i, span){
			$(this).text("");
		});
	});
	
	$("#replacementReader li").each(function(idx, li){
		$(this).children("span").each(function(i, span){
			$(this).text("");
		});
	});
}

function focusOnParks()
{
	$('#venues').dropkick('focusOff');
	$('#readers').dropkick('focusOff');
	$('#parks').dropkick('focusOn');
}

function focusOnVenues()
{
	$('#readers').dropkick('focusOff');
	$('#parks').dropkick('focusOff');
	$('#venues').dropkick('focusOn');
}

function focusOnReaders()
{
	$('#parks').dropkick('focusOff');
	$('#venues').dropkick('focusOff');
	$('#readers').dropkick('focusOn');
}

function lightUp(readerMac)
{
	if (readerMac === undefined || $.trim(readerMac) === ""){
		return;
	}
	
	$.ajax({
		url: "replaceReader-lightUp.action",
		data: {
			"parkId": $('#parks').val(),
			"xbrcId": $('#venues').val(),
			"replacementReaderMac": readerMac,
		},
		success: function(data){
			if (data.err == null){
				$errorDialog.dialog("option", "title", "Success!");
				$errorDialog.text("Light UP command to reader " + readerMac + " succeeded!");
			} else {
				$errorDialog.dialog("option", "title", "Failure!");
				$errorDialog.text("Light UP command to reader " + readerMac + " failed! Can't contact the reader.");
			}

			$errorDialog.dialog('open');
		},
		error: function(data){
			$errorDialog.dialog("option", "title", "Error!");
			$errorDialog.text("Light UP command to reader " + readerMac + " failed!");
			$errorDialog.dialog('open');
		},
		dataType: "json",
		type: "GET",
	});
}