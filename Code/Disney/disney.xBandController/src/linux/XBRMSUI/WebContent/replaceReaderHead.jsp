<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="script/new/DropDown/jquery.dropkick-1.0.0.js"></script>
<script type="text/javascript" src="script/Reader.js"></script>

<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />
<link rel="stylesheet" href="css/new/dropdown.css" type="text/css">
<link type="text/css" href="script/new/legacy-jquery-custom-blue/css/legacy-custom-blue-theme/jquery-ui-1.9.2.custom.min.css" rel="stylesheet" />

<style type="text/css">
#idBody { position: relative; min-height: 345px; }

ul.horisontal-list {
	list-style: none;
	margin: 1em 0 1em 0; padding: 0;
	position: relative;
}
ul li { list-style: none; padding: 0; margin: 0; }
ul.horisontal-list li.first-layer {
	margin: 1em 0 0 0; padding: 0;
	display: inline;
}
ul.horisontal-list li.first-layer:FIRST-CHILD {
	margin: 0; padding: 0;
}

ul#toBeReplaced { width: 70%; }
ul#toBeReplaced li select { width: 23.3%; min-width: 23.3%; max-width: 23.3%; }

div#replacement_wrapper { width: 70% !important; margin-top: 1em; } 
div#dk_container_parks { margin-left: 0; } 

div#summary {
	position: absolute; top: 60px; right: 12px;
	margin: 0; padding: 0 1em 1em 1em;
	width: 250px;
}
div#buttons {
	padding: 0; margin: 1em 0 0 0;
	border: none;
	max-width: 200px;
}
div#buttons input#cancel {
	float: left; clear: right;
}
div#buttons input#replace {
	float: right; clear: right;
}
.button {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #14325d), color-stop(1, #205395) );
	background:-moz-linear-gradient( center top, #14325d 5%, #205395 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#14325d', endColorstr='#205395');
	background-color:#14325d;
	-moz-border-radius:4px;
	-webkit-border-radius:4px;
	border-radius:4px;
	border:1px solid #6e9de0;
	display:inline-block;
	color:#ffffff;
	font-family:arial;
	font-size:13px;
	font-weight:bold;
	padding:6px 22px;
	text-decoration:none;
}.button:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #205395), color-stop(1, #14325d) );
	background:-moz-linear-gradient( center top, #205395 5%, #14325d 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#205395', endColorstr='#14325d');
	background-color:#205395;
}.button:active {
	position:relative;
	top:1px;
}

label {
	text-decoration: none;
	font-style: normal;
}
label:AFTER {content: "";}

.dk_options_inner { padding: 0; margin: 0;}
.dk_options_inner li { padding: 0; margin: 0; }
.dataTables_info { display: none; }
</style>

<script type="text/javascript">
var authenticationErrorDialogContent = "<p>Your session has expired. Please <a style='text-decoration:underline;color:blue;border:none;' href='logout.action'>logout</a> to start a new session.</p>"
var venuesByPark;
initParksAndVenues();

$(document).ready(function() 
{
	ErrorDialog.init();
	
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
			
			// refresh the venues select list
			$venues = $('#venues');
			// remove old options
			$venues[0].options.length = 0;	// works even with EI6, don't get tempted to make it better!
			
			// add new options
			$venues.append('<option value="-1" selected="selected">-- Select Venue --</option>');
			var venues = venuesByPark[value];

			for (var xbrc in venues)
			{
				$venues.append('<option value="' + venues[xbrc].id + '">' + venues[xbrc].name + '</option>');
			}
			
			// refresh the dropdown
			$('#venues').dropkick('reload');
			
			clearExistingReadersList();
			
			// move focus from parks selector to venues selector
			focusOnVenues();
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
				url: "replaceReader-venueSelected-ajax.action",
                cache: false,
				data: {
					"parkId": $('#parks').val(),
					"xbrcId": value,
					"filter": $("#filter").prop("checked"),
					"NoAcRedirect":"NoAcRedirect"
				},
				success: function(response){
					// refresh the venues select list
					$readers = $('#readers');
					// remove old options
					$readers[0].options.length = 0;	// works even with EI6, don't touch it!
					// add new options
					$readers.append('<option value="-1" selected="selected">-- Select Reader --</option>');
					var readers = response.existingReaders;
					for (var i = 0; i < readers.length; i++)
					{
						$readers.append('<option value="' + readers[i].mac + '">' + readers[i].name + '</option>');
					}
					// refresh the dropdown
					$("#readers").dropkick('reload');
					
					clearReplacementReaders();
					
					focusOnReaders();
				},
				error: function(response){
					handleError(response, "Failed to retrieve a list of readers eligible for replacement.");
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
				url: "replaceReader-existingReaderSelected-ajax.action",
                cache: false,
				data: {
					"parkId": $('#parks').val(),
					"xbrcId": $('#venues').val(),
					"existingReaderMac": value,
					"filter": $("#filter").prop("checked"),
					"NoAcRedirect":"NoAcRedirect"
				},
				success: function(response){
					var reader = new Reader();
					reader.xbrcId = $.trim($("#venues").find(":selected").text());
					reader.name = response.existingReader.name;
					reader.typeDescription = response.existingReader.type;
					reader.ip = response.existingReader.ip;
					reader.mac = response.existingReader.mac;
						
					existingReaderSelected(reader);
					
					populateReplacementReadersTable(
							response.replacementUnlinkedReaders, 
							reader.typeDescription,
							response.replacementUnassignedReaders
					);
				
					$('#venues').dropkick('focusOff');
					$('#parks').dropkick('focusOff');
					$('#readers').dropkick('focusOff');
				},
				error: function(response){
					handleError(response, "Failed to retrieve a list of replacement readers.");
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
	
	$confirmDialog = $("#confirmDialog").dialog({
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
	
	initParksAndVenues();
});

function populateReplacementReadersTable(replacementUnlinkedReaders, readerType, replacementUnassignedReaders)
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
			replacementReaderData[i][0] = "Assigned to an xBRC but not yet linked to a location";
			replacementReaderData[i][1] = replacementUnlinkedReaders[i].mac;
			replacementReaderData[i][2] = replacementUnlinkedReaders[i].ip;
			replacementReaderData[i][3] = '<img src="images/' + status + '-light.png"><span style="display:none">' + statusValue + '</span></img>';
			if (readerType.indexOf("Tap") >= 0){
				replacementReaderData[i][4] = '<img class="clickable" src="images/light-16px.png" onclick="lightUp(\'' + replacementUnlinkedReaders[i].mac + '\',false)"/>';
				replacementReaderData[i][5] = '<input class="unlinked" type="radio" name="replacementReader" value="' + replacementUnlinkedReaders[i].ip + '-' + replacementUnlinkedReaders[i].mac + '" onclick="replacementReaderSelected()"/>';
			} else {
				replacementReaderData[i][4] = '<input class="unlinked" type="radio" name="replacementReader" value="' + replacementUnlinkedReaders[i].ip + '-' + replacementUnlinkedReaders[i].mac + '" onclick="replacementReaderSelected()"/>';
			}
		}
	}
	if (replacementUnassignedReaders.length > 0)
	{
		for (var i = 0, tableIndex = replacementUnlinkedReaders.length; i < replacementUnassignedReaders.length; i++, tableIndex++)
		{
			var status = replacementUnassignedReaders[i].status.toLowerCase();
			if (status === "green"){
				statusValue = 0;
			} else if (status === "yellow"){
				statusValue = 1;
			} else if (status === "red"){
				statusValue = 2;
			} else {
				statusValue = -1;
			}
				
			replacementReaderData[tableIndex] = new Array();
			replacementReaderData[tableIndex][0] = "Not yet assigned to any xBRC";
			replacementReaderData[tableIndex][1] = replacementUnassignedReaders[i].mac;
			replacementReaderData[tableIndex][2] = replacementUnassignedReaders[i].ip;
			replacementReaderData[tableIndex][3] = '<img src="images/' + status + '-light.png"><span style="display:none">' + statusValue + '</span></img>';
			if (readerType.indexOf("Tap") >= 0){
				replacementReaderData[tableIndex][4] = '<img class="clickable" src="images/light-16px.png" onclick="lightUp(\'' + replacementUnassignedReaders[i].mac + '\',true)"/>';
				replacementReaderData[tableIndex][5] = '<input class="unassigned" type="radio" name="replacementReader" value="' + replacementUnassignedReaders[i].ip + '-' + replacementUnassignedReaders[i].mac + '" onclick="replacementReaderSelected()"/>';
			} else {
				replacementReaderData[tableIndex][4] = '<input class="unassigned" type="radio" name="replacementReader" value="' + replacementUnassignedReaders[i].ip + '-' + replacementUnassignedReaders[i].mac + '" onclick="replacementReaderSelected()"/>';
			}
		}
	}
		
		$replaceReadersTable = $("#replacement");
		
			var header = new Array();
			header.push({"sTitle":"Group"});
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
				$("#replacement-container .instruction").after('<table id="replacement" class="lightGreyBorder round" style="margin: 5px auto 0 auto; width: 657px;"></table>');
			}
			else
			{
				$replaceReadersTable.dataTable().fnDestroy(true);
				$("#replacement-container .instruction").after('<table id="replacement" class="lightGreyBorder round" style="margin: 5px auto 0 auto; width: 657px;"></table>');
			}
			
			$("#replacement").dataTable({
				"fnDrawCallback": function(oSettings){
					if (oSettings.aiDisplay.length == 0)
						return;
					
					var nTrs = $("#replacement tbody tr");
					var iColspan = nTrs[0].getElementsByTagName('td').length;
					var sLastGroup = "";
					for (var i = 0; i < nTrs.length; i++){
						var iDisplayIndex = oSettings._iDisplayStart + i;
						var sGroup = oSettings.aoData[oSettings.aiDisplay[iDisplayIndex]]._aData[0];
						if (sGroup != sLastGroup)
						{
							var nGroup = document.createElement('tr');
							var nCell = document.createElement('td');
							nCell.colSpan = iColspan;
							nCell.className = "group lightBlueBackground darkBlueText centered";
							nCell.innerHTML = sGroup;
							nGroup.appendChild(nCell);
							nTrs[0].parentNode.insertBefore(nGroup,nTrs[i]);
							sLastGroup = sGroup;
						}
					}
				},
				"aaData": replacementReaderData,
				"aoColumns": header,
				"aoColumnDefs": [
					{"bVisible":false,"aTargets":[0]}
				],
				"bPaginate": false,
				"bFilter": false,
				"oLanguage": {
					"sEmptyTable": "There are no readers available for replacement.",
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
		ErrorDialog.open("Park, Venue, Reader to be Replaced, and Replacement Reader must be selected for the reader replacement action.");
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
		
		$selectedReplacementReader = $("#replacement").find("input[type='radio']:checked");
		replacementReader = $selectedReplacementReader.val().split('-');
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
			url: "replaceReader-runReplace-ajax.action",
            cache: false,
			data: {
				"parkId": parkId,
				"xbrcId": xbrcId,
				"existingReaderMac": existingReaderMac,
				"replacementReaderMac": replacementReaderMac,
				"unassignedReplacement": $selectedReplacementReader.hasClass('unassigned'),
				"NoAcRedirect":"NoAcRedirect"
			},
			success: function(response){
				refresh();
				
				$confirmDialog.dialog("option", "title", "Success!");
				$confirmDialog.text("Reader " + response.existingReaderMac + " has been successfully replaced with reader " + replacementReaderMac + ".");
				$confirmDialog.dialog('open');
			},
			error: function(response){
				handleError(response, "Failed to replace reader " + existingReaderMac + " with reader " + replacementReaderMac + ".");
			},
			dataType: "json",
			type: "GET",
		});
	}
}

function refresh()
{
		$.ajax({
			url: "replaceReader-refresh-ajax.action",
            cache: false,
            data: {
            	"NoAcRedirect":"NoAcRedirect"
            },
			success: function(response){
				clearParks(response);
				$("#replacement-container").hide();
				
				focusOnParks();
			},
			error: function(response){
				handleError(response, "Failed to retreive the requested list of readers.");
			},
			dataType: "json",
			type: "GET",
		});
}

function clearParkSelection()
{
	$('#parks option[value="-1"]').prop('selected', true);
	$("#parks").dropkick('reload');
	
	clearVenues();
}

function clearParks(data)
{
	if (data == undefined)
		return;
	
	// parks drop down
	$parks = $("#parks");
	// remove old options
	$parks[0].options.length = 0; // works even with IE6, don't change it!
	// add new options
	$parks.append('<option value="-1" selected="selected">-- Select Park --</option>');
	var parks = data.parks;
	for (var key in parks) 
	{
		if (!parks.hasOwnProperty(key)){
			continue;
		}
	    $parks.append('<option value="' + key + '">' + parks[key] + '</option>');
	}
	// refresh the dropdown
	$('#parks').dropkick('reload');
	
	clearVenues();
}

function clearVenues()
{
	// venues drop down
	$venues = $('#venues');
	// remove old options
	$venues[0].options.length = 0;	// works even with IE6, don't touch it!				
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

function lightUp(readerMac, unassigned)
{
	if (readerMac === undefined || $.trim(readerMac) === ""){
		return;
	}
	
	if (unassigned === undefined){
		unassigned = false;
	}
	
	$.ajax({
		url: "replaceReader-lightUp-ajax.action",
		data: {
			"parkId": $('#parks').val(),
			"xbrcId": $('#venues').val(),
			"replacementReaderMac": readerMac,
			"unassignedReplacement": unassigned,
			"NoAcRedirect":"NoAcRedirect"
		},
		success: function(response){
			$confirmDialog.dialog("option", "title", "Success!");
			$confirmDialog.text("Light UP command to reader " + readerMac + " succeeded!");
			$confirmDialog.dialog('open');
		},
		error: function(response){
			handleError(response, "Light UP command to reader " + readerMac + " failed!");
		},
		dataType: "json",
		type: "GET",
	});
}

function initParksAndVenues()
{
	venuesByPark = new Array();
	var parkId;
	<s:iterator value="venues" status="parkStat">
		parkId = "${key}";
		venuesByPark[parkId] = new Array();
		<s:iterator var="xbrc" value="value" status="xbrcStat">
			<s:if test="name != null && name != ''">
				venuesByPark[parkId].push({"id":"${xbrc.id}", "name":"${xbrc.name}"});
			</s:if>
			<s:else>
				venuesByPark[parkId].push({"id":"${xbrc.id}", "name":"${xbrc.hostname}"});
			</s:else>
		</s:iterator>
	</s:iterator>
}

function handleError(response, errorMessage)
{
	var status = 500;
	var responseText = "";
	var reason = "unknown";
	
	if (response !== undefined)
	{
		status = response.status;
		responseText = response.responseText;
		reason = response.getResponseHeader("errMsg");
		if ($.trim(reason).length === 0)
			reason = "unknown";
	}
	
	if (status === 401 || responseText.indexOf("Authentication") != -1) {
		location.href = "replaceReader.action";
	}
	else {
		if (errorMessage === undefined || $.trim(errorMessage).length === 0)
			errorMessage = "Unexpected server error.";
			
		ErrorDialog.open("<p>" + errorMessage + "</p><p><b>Reason:</b> " + reason);
	}		
}
</script>