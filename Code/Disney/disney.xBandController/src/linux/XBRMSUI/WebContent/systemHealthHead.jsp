<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />

<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<script type="text/javascript" src="script/NetworkAddress.js"></script>

<style type="text/css">
#idBody { position: relative; }

ul#monitoredSystems { list-style: none; padding: 0; margin: 0; }

label { text-decoration: none; font-style: normal; }
label:AFTER { content: ""; }

/*.healthSystemType { width: 50%; position: relative; top: 30px; left: 0px;}*/

td div img.openSlave { 
	position: absolute; right: 3px; bottom: -2px; 
	width: 16px; height: 16px;
	cursor: pointer;
}

td.slave-row { padding: 0; margin: 0; }
ul.slave-info { 
	list-style: none; background: #f0f0f0;
	padding: 0; margin: 0; position: relative; 
}
ul.slave-info li { float: left; width: 19%; margin: 0 5px 0 0; }
ul.slave-info li span.label { font-style: italic; font-weight: normal; }
ul.slave-info li span.value { font-style: normal; font-weight: normal; }
ul.slave-info li.statusMessage { width: 20%; }

div.dataTables_info { display: none; }
div.dataTables_filter { display: none; }

</style>

<script type="text/javascript">

var slaves = new Array();
var PreviousSystemHealth;
var refreshing = false;

function submitHealthItemForm(id, type){
	
	if (id === "undefined"){
		return;
	}	
	
	$form = $("form#" + type + "Form");
	$hidden = $form.children("input#id");
	$hidden.attr("value", id);
	
	if ($form.length > 0)
	{
		$form.submit();
	}
}

function removeHealthItem(id, ip, type){
	
	refreshing = true;
	
	if (type === "XBRMS"){
		var message = "You are about to permanently delete item " + ip + 
			" from a list of watched systems. This action will delete all historical data " +
			"associated with that item, ex: performance metric data";
	} else {
		var message = "You are about to permanently delete item " + ip + 
			" from a list of watched systems.";
	}

	var userResponse = confirm(message);
	
	if (userResponse === false){
		refreshing = false;
		return;
	}
	
	try
	{
		var healthTable = $("#table_" + type).dataTable();
		
		// delete the row
		healthTable.fnDeleteRow(
				healthTable.fnGetPosition(
						$("#table_" + type + " tbody #inv_tr_" + id).get(0)), null, true);
		
	} catch (exception ) {
		Debugger.log(exception);
	} finally {
		refreshing = false;
	}
	
	$.ajax({
		url: "removehealthitem.action",
		data: {
			"healthItemId":id,
			"healthItemType":type
		},
		success: function(data){
			if (data.err != null){
				alert("Failed to remove a health item! Error: " + data.err + ". Your health item will be added back to the table on the next refresh.");
			}
			
			refreshing = false; //force another refresh
			refresh();
		},
		error: function(data){
			alert("Failed to remove a health item! Error: " + data.err + ". Your health item will be added back to the table on the next refresh.");
		},
		dataType: "json",
		type: "GET",
	});
}

function deactivateHealthItem(id, ip, type){
	
	refreshing = true;
	
	var message = "You are about to deactivate item " + ip + 
		" from a list of watched systems. This action will remove it from" +
		" this page, but will NOT delete historical data associated with it.";
	
	var userResponse = confirm(message);
	
	if (userResponse === false){
		refreshing = false;
		return;
	}

	try
	{
		var healthTable = $("#table_" + type).dataTable();
				
		healthTable.fnDeleteRow(
				healthTable.fnGetPosition(
						$("#table_" + type + " tbody #inv_tr_" + id).get(0)), null, true);
		
	} catch (exception ) {
		Debugger.log(exception);
	} finally {
		refreshing = false;
	}
	
	$.ajax({
		url: "inactivatehealthitem.action",
		data: {
			"healthItemId":id,
			"healthItemType":type
		},
		success: function(data){
			if (data.err != null){
				alert("Failed to deactivate a health item! Error: " + data.err + ". Your health item will be added back to the table on the next refresh.");
			}
			
			refreshing = false; //force another refresh
			refresh();
		},
		error: function(data){
			alert("Failed to deactivate a health item! Error: " + data.err + ". Your health item will be added back to the table on the next refresh.");
		},
		dataType: "json",
		type: "GET",
	});
}

function showAddXbrcDialog() {
	
    dojo.event.topic.publish('checkAccess');
    
	$("#addXbrcDialog").dialog('option', 'title', 'Add Health Item ');
	$("#addXbrcDialog").dialog('open');
	$("span#addXbrcStatus").html("");

}

function refreshStatus() {
    //dojo.event.topic.publish('checkAccess');
    //dojo.event.topic.publish('refreshStatus');
	refresh();
}

function addHealthItem() {
	
	var validPort = true;
	var validIp = true;
	var status = "Invalid input: ";
	
	try {
		$portField = $("input#addXbrcPort");
		if ($portField.length > 0){
			validPort = NetworkAddress.isValidPort($portField.val());
			if (!validPort){
				status += "port";
				$("span#addXbrcStatus").html(status);
			}
		}
		
		$ipField = $("input#xbrcIp");
		if ($ipField.length > 0){
			validIp = NetworkAddress.isValidHostname($ipField.val());
			if (!validIp){
				if (!validPort){
					status += ", ";
				}
				status += "ip";
				$("span#addXbrcStatus").html(status);
			}
		}
		
		if (validPort && validIp){
			dojo.event.topic.publish('checkAccess');
			
			if ($.trim($ipField.val()) == "") {
				refreshAddXbrcDialog("Please enter the IP address", false);
				return;
			}
			
			var status =  "Connecting to " + $ipField.val() + " ...";
			$("span#addXbrcStatus").html(status);
			
			$.ajax({
				url: "addhealthitem.action",
				data: {
					"healthItemIp":$ipField.val(),
					"healthItemPort":$portField.val(),
					"healthItemType":$("select#idSelectType").val()
				},
				success: function(data){
					if (data.err != null){
						alert("Failed to add a health item! Error: " + data.err);
						return;
					}
					
					if (data.newHealthItem != null){
						refresh();
					}
	
					try
					{	
						$("#addXbrcDialog").dialog('close');
					} catch (exception ) {
						Debugger.log(exception);
					}
				},
				error: function(data){
					$("span#addXbrcStatus").html("Failed to add a health item! Error: " + data.err);
				},
				dataType: "json",
				type: "GET",
			});
		}
	} catch (exception) {
		Debugger.log(exception);
	} finally {
		$field = null;
		status = null;
		validPort = null;
		validIp == null;
	}
}

function refreshAddXbrcDialog(message, close) {
	
	$("span#addXbrcStatus").html(message);
	if (close) {
		$("#addXbrcDialog").dialog('close');
		window.location.href="systemhealth";
	}
}

function toggleRow(table, link)
{
	try
	{	
		var row = link.parentNode.parentNode.parentNode;
		if (table.fnIsOpen(row)){
			table.fnClose(row);
		} else {
			table.fnOpen(row, formatSlaveDetails(table, row), "slave-row");
		}
	} catch (exception ) {
		Debugger.log(exception);
	}
}

function formatSlaveDetails2(table, masterRow)
{
	// keep this method for now, somebody might want a table
	var rowId = $(masterRow).prop("id");
	var masterId = rowId.substring("inv_tr_".length);
	var slaveInfo = slaves[masterId];
	
	if (slaveInfo == undefined){
		return;
	}
	
	var array = slaveInfo.split(',');
	if (array.length == 0)
	{
		return;
	}
	
	var slaveRow = '<table class="slave" id="' + array[0] + '">';
	slaveRow = '<thead><tr>';
	slaveRow = '<th>Address</th><th>Port</th><th>Vip</th><th>Version</th><th>Ping</th><th>Facility Id</th><th>Fodel</th><th>HA</th><th>Health</th><th>Status</th>';
	slaveRow = '</tr></thead>';
	slaveRow = '<tbody>';
	slaveRow = '<tbody>';
	slaveRow = '<tr>';
	for (var i = 1; i < array.length; i++)
	{
		if (i == array.length - 1)
		{
			slaveRow += '<td>' + (array[i].length == 0 ? "Message not available" : array[i]) + '</td>';
		}
		else
		{
			var content = array[i];
			if (content.indexOf("://") >= 0){
				continue;
			}
			if (content.indexOf("png") >= 0){
				content = '<img src="images/' + array[i] + '">';
			}

			slaveRow += '<td>' + content + '</td>';
		}
	}
	slaveRow += '</tr>';
	slaveRow += '</tbody>';
	slaveRow += '</table>';
	
	return slaveRow;
}

function formatSlaveDetails(table, masterRow)
{
	var rowId = $(masterRow).prop("id");
	var masterId = rowId.substring("inv_tr_".length);
	var slaveInfo = slaves[masterId];
	
	if (slaveInfo == undefined){
		return;
	}
	
	var array = slaveInfo.split(',');
	if (array.length == 0)
	{
		return;
	}
	
	var slaveRow = '<ul class="slave-info" id="' + array[0] + '">';
	slaveRow += '<li id="address_' + array[0] + '"><span class="label blueText">Direct Address: </span></br><span class="value">' + array[1] + ':' + array[2] + '</span></li>';
	slaveRow += '<li id="vip_' + array[0] + '"><span class="label blueText">VIP Address: </span></br><span class="value">' + array[3] + ':' + array[2] + '</span></li>';
	slaveRow += '<li id="version_' + array[0] + '"><span class="label blueText">Version: </span></br><span class="value">' + array[4] + '</span></li>';
	slaveRow += '<li id="discovery_' + array[0] + '"><span class="label blueText">Last Discovery: </span></br><span class="value">' + array[5] + '</span></li>';
	slaveRow += '<li id="message_' + array[0] + '" class="statusMessage"><span class="label blueText">Status: </span></br><span class="value">' + (array.length == 6 ? 'Message not available' : array[7] + '</span></li>');
	slaveRow += '</ul>';
	
	return slaveRow;
}

function refreshRow(xbrcTable, oItem, oHi, rowPos, aCells, type)
{
	if (xbrcTable.length == 0){
		return;
	}
	if (oItem === undefined){
		return;
	}
	if (rowPos === undefined){
		return;
	}
	if (aCells === undefined){
		return;
	}
	
	// name and vip columns are only displayed for xBRCs
	var isXbrc = type === "XBRC";
	
	var mandatoryFields = new Array();
	for (var i = 0; i < oHi.fields.length; i++){
		if (oHi.fields[i].mandatory === false){
			continue;
		}
		mandatoryFields.push(oHi.fields[i]);
	}
	
	if (isXbrc)
		refreshXbrcRow(xbrcTable, oItem, oHi, rowPos, aCells, mandatoryFields);
	else
		refreshNonXbrcRow(xbrcTable, oItem, oHi, rowPos, aCells, mandatoryFields);
}

function refreshXbrcRow(xbrcTable, oItem, oHi, rowPos, aCells, mandatoryFields)
{
	var cellIndex = 0;
	aCells.each(function(){
		$cell = $(this);
		
		var numOfDynamicFields = 0;
		if (mandatoryFields != "undefined")
		{
			numOfDynamicFields = mandatoryFields.length;
		}

		// do the main fileds
		if (cellIndex == 0 && $.trim($cell.html()) !== oItem.name){
			xbrcTable.fnUpdate( oItem.name, rowPos, 0, false, false);
		} else if (cellIndex == 1 && $.trim($cell.html()) !== oHi.hostname){
			xbrcTable.fnUpdate( oHi.hostname, rowPos, 1, false, false);
		} else if (cellIndex == 2 && $.trim($cell.html()) !== oHi.address){
			xbrcTable.fnUpdate( oHi.address, rowPos, 2, false, false);
		} else if (cellIndex == 3 && $.trim($cell.html()) !== oItem.vip){
			xbrcTable.fnUpdate( oItem.vip, rowPos, 3, false, false);
		} else if (cellIndex == 4 && $.trim($cell.html()) !== oItem.version){
			xbrcTable.fnUpdate( oItem.version, rowPos, 4, false, false);
		} else if (cellIndex == 5 && $.trim($cell.html()) !== oHi.lastDiscoveryAgo){
			xbrcTable.fnUpdate( oHi.lastDiscoveryAgo, rowPos, 5, false, false);
		} else if (cellIndex == (6 + numOfDynamicFields)){
			$masterImage = $cell.find("div a.openMaster img");
			$slaveImage = $cell.find("div img.openSlave");

			if ($masterImage.length != 0)
			{
				$masterImage.prop("src", "images/" + oHi.statusIcon);
			}
			if (oHi.slave != null)
			{
				// slave icon not yet there, or needs updating
				if ($slaveImage.length == 0){
					$cell.find("div a.openMaster").after('<img class="openSlave" src="images/' + oHi.slaveStatusIcon + '" title="Click for detail slave status.">');
				} else {
					$slaveImage.prop("src", "images/" + oHi.slaveStatusIcon);
				}
			}
			else
			{
				// slave icon shouldn't be there
				if ($slaveImage.length != 0)
				{
					$slaveImage.remove();
				}
			}
				
			xbrcTable.fnUpdate($cell.html(), rowPos, 6 + numOfDynamicFields, false, false);
		} else if ( cellIndex == (7 + numOfDynamicFields) && $.trim($cell.html()) !== oHi.statusMessage){
			xbrcTable.fnUpdate( oHi.statusMessage, rowPos, 7 + numOfDynamicFields, false, false);
		}
		
		// do dynamic fields
		for (var k = 0; k < mandatoryFields.length; k++)
		{
			var field = mandatoryFields[k];
			
			if (cellIndex == (6 + k))
			{
				if (field.type !== "Url" && $.trim($cell.html()) !== field.value)
				{
					var newValue = field.value;
					if (newValue == null){
						newValue = "";
					}
					var modelIndex = newValue.indexOf("model");
					if (modelIndex > 0){
						newValue = field.value.substring(0, modelIndex);
					}
					xbrcTable.fnUpdate( newValue, rowPos, 6 + k, false, false);
				}
				else if (field.type === "Url" && $cell.html().trim().indexOf(field.value) == -1)
				{
					xbrcTable.fnUpdate('<a href="' + field.value + '" target="_blank">UI</a>', rowPos, 6 + k, false, false);
				}
			}
		}
		
		cellIndex++;
	});
}

function refreshNonXbrcRow(xbrcTable, oItem, oHi, rowPos, aCells, mandatoryFields)
{
	var cellIndex = 0;
	aCells.each(function(){
		$cell = $(this);
		
		var numOfDynamicFields = 0;
		if (mandatoryFields != "undefined")
		{
			numOfDynamicFields = mandatoryFields.length;
		}

		// do the main fileds
		if (cellIndex == 0 && $.trim($cell.html()) !== oHi.hostname){
			xbrcTable.fnUpdate( oHi.hostname, rowPos, 0, false, false);
		} else if (cellIndex == 1 && $.trim($cell.html()) !== oHi.address){
			xbrcTable.fnUpdate( oHi.address, rowPos, 1, false, false);
		} else if (cellIndex == 2 && $.trim($cell.html()) !== oItem.version){
			xbrcTable.fnUpdate( oItem.version, rowPos,2, false, false);
		} else if (cellIndex == 3 && $.trim($cell.html()) !== oHi.lastDiscoveryAgo){
			xbrcTable.fnUpdate( oHi.lastDiscoveryAgo, rowPos, 3, false, false);
		} else if (cellIndex == (4 + numOfDynamicFields)){
			$masterImage = $cell.find("div a.openMaster img");
			$slaveImage = $cell.find("div img.openSlave");

			if ($masterImage.length != 0)
			{
				$masterImage.prop("src", "images/" + oHi.statusIcon);
			}
			if (oHi.slave != null)
			{
				// slave icon not yet there, or needs updating
				if ($slaveImage.length == 0){
					$cell.find("div a.openMaster").after('<img class="openSlave" src="images/' + oHi.slaveStatusIcon + '" title="Click for detail slave status.">');
				} else {
					$slaveImage.prop("src", "images/" + oHi.slaveStatusIcon);
				}
			}
			else
			{
				// slave icon shouldn't be there
				if ($slaveImage.length != 0)
				{
					$slaveImage.remove();
				}
			}
				
			xbrcTable.fnUpdate($cell.html(), rowPos, 4 + numOfDynamicFields, false, false);
		} else if ( cellIndex == (5 + numOfDynamicFields) && $.trim($cell.html()) !== oHi.statusMessage){
			xbrcTable.fnUpdate( oHi.statusMessage, rowPos, 5 + numOfDynamicFields, false, false);
		}
		
		// do dynamic fields
		for (var k = 0; k < numOfDynamicFields; k++)
		{
			var field = mandatoryFields[k];
			
			if (cellIndex == (4 + k))
			{
				if (field.type !== "Url" && $.trim($cell.html()) !== field.value)
				{
					var newValue = field.value;
					if (newValue == null){
						newValue = "";
					}
					var modelIndex = newValue.indexOf("model");
					if (modelIndex > 0){
						newValue = field.value.substring(0, modelIndex);
					}
					xbrcTable.fnUpdate( newValue, rowPos, 4 + k, false, false);
				}
				else if (field.type === "Url" && $cell.html().trim().indexOf(field.value) == -1)
				{
					xbrcTable.fnUpdate('<a href="' + field.value + '" target="_blank">UI</a>', rowPos, 4 + k, false, false);
				}
			}
		}
		
		cellIndex++;
	});
}

function createRowData(oItem, oHi, type)
{
	// name and vip columns are only displayed for xBRC
	var isXbrc = false;
	if (type === "XBRC")
		isXbrc = true;
	
	var aRowData = new Array();
	if (isXbrc)
		aRowData.push(oItem.name);
	aRowData.push(oHi.hostname);
	aRowData.push(oHi.address);
	if (isXbrc)
		aRowData.push(oItem.vip);
	aRowData.push(oItem.version);
	aRowData.push(oHi.lastDiscoveryAgo);
	for (var i = 0; i < oHi.fields.length; i++)
	{
		if (oHi.fields[i].mandatory === false){
			continue;
		}
		
		if (oHi.fields[i].type === "Url"){
			aRowData.push('<a href="' + oHi.fields[i].value + '" target="_blank">UI</a>');
		} else {
			var newValue = oHi.fields[i].value;
			if (newValue == null){
				newValue = "";
			}
			var modelIndex = newValue.indexOf("model");
			if (modelIndex > 0){
				newValue = oHi.fields[i].value.substring(0, modelIndex);
			}
			aRowData.push(newValue);
		}
	}
	var healthStatus = '<div style="position:relative;">'
		+ '<a class="openMaster" onclick="submitHealthItemForm(' + oItem.id + ', \'' + type + '\')" href="#" target="_blank">'
		+ '<img src="images/' + oHi.statusIcon + '"/>'
		+ '<span style="display:none">' + oHi.statusWeight + '</span>'
		+ '</a>';
	if (oHi.slave != null)
	{
		healthStatus += '<img class="openSlave" src="images/' + oHi.slaveStatusIcon + '" title="Click for detail slave status.">';
	}	
	healthStatus += '</div>';
	aRowData.push(healthStatus);
	aRowData.push(oHi.statusMessage);

	var actions = '<a id="inv_menu_' + oItem.id + '" style="background: none; border: none;padding: 0 0 0 12px;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#">'
		+ '<span class="ui-icon ui-icon-triangle-1-s"></span></a>'
		+ '<div id="inv_content_' + oItem.id + '" class="hidden"><ul>'
		+ '<li><a href="#inv_tr_' + oItem.id + '" onclick=\'submitHealthItemForm(' + oItem.id + ', "' + type + '");\'>'
		+ '<img class="fg-menu-icon" src="images/zoomin.png" title="Explore detailed health status."/>Details</a></li>';
	if (type === "XBRC"){
		actions += '<li><a href="#inv_tr_' + oItem.id + '" onclick=\'deactivateHealthItem("' + oItem.id + '", "' + oItem.ip + '", "' + type + '");\'>'
				+ '<img class="fg-menu-icon" src="images/deactivate-16.png" title="Temporarily disable this item\'s monitoring."/>Deactivate</a></li>'
				+ '<li><a href="' + oItem.adminUrl + '" target="_blank">'
				+ '<img class="fg-menu-icon" src="images/configure_16.png" title="Administer this xBRC."/>Configure</a></li>';
	}
	actions += '<li><a href="#inv_tr_' + oItem.id + '" onclick=\'removeHealthItem("' + oItem.id + '", "' + oItem.ip + '", "' + type + '");\'>'
		+ '<img class="fg-menu-icon" src="images/delete-16.png" title="Permamently delete this item from the database."/>Delete</a></li>'
		+ '</ul></div>';
	aRowData.push(actions);
	
	return aRowData;
}

function refresh()
{
	if (refreshing){
		return;
	}
	
	$.ajax({
		url: "systemhealthajax.action",
        cache: false,
		success: function(data){
			if (data.err !== undefined && data.err !== null){
				alert(data.err);
			}
			
			try
			{
				refreshing = true;
				
				var newrows = new Array();
				
				NextSystemHealth = data.inventory;
				if (NextSystemHealth == null){
					$allTables = $("table.healthItems");
					if ($allTables.length > 0){
						// remove all DataTable cached info
						$allTables.dataTable().fnDestroy();
						// empty the systems list
						$("#monitoredSystems").children().each(function(){
							$(this).remove();
						});
					}
					return;
				}
				
				var slaveItems = new Array();
				for (var key in NextSystemHealth) 
				{
					$table = $("#table_" + key);
					
					var healthItems = NextSystemHealth[key];
					if (healthItems == null || healthItems.length == 0){
						if ($table.length > 0){
							// remove all DataTable cached info
							$table.dataTable().fnDestroy();
							// remove this system's DOM items form the list
							$table.parent().remove();
						}
						continue;
					}

					if ($table.length == 0){
						if (healthItems.length == 0){
							continue;
						}
						var allDynamicFields = healthItems[0].fields;
						if (allDynamicFields === null){
							continue;
						}
						var mandatoryDynamicFields = new Array();
						for (var i = 0; i < allDynamicFields.length; i++){
							if (allDynamicFields[i].mandatory){
								mandatoryDynamicFields.push(allDynamicFields[i].name);
							}
						}
						var healthTable = createDataTable(key, mandatoryDynamicFields);
					}
					else {
						var healthTable = $table.dataTable();
					}
					
					// note for later what rows we have at this time in order to purge stale rows
					var staleRows = new Array();
					$("#table_" + key + " tbody tr").each(function(){
						var id = $(this).prop("id");
						if (id !== undefined && $.trim(id).length > 0)
						{
							staleRows.push(id);
						}
					});
					
					// refresh the non-slave health items
					for (var i = 0; i < healthItems.length; i++)
					{
						var oHi = healthItems[i];
						var oItem = oHi.item;
						var oSlave = oHi.slave;
							
						if (oItem.haStatus === undefined || oItem.haStatus !== "slave")
						{
							// these items will ocupy a fist level row
							var rowId = "inv_tr_" + oItem.id;
							var row = $("#table_" + key + " #" + rowId);
							if (row.length == 0)
							{
								// new item
								var addId = healthTable.fnAddData(createRowData(oItem, oHi, key), false);
								var theNode = healthTable.fnSettings().aoData[addId[0]].nTr;
								theNode.setAttribute('id',rowId);
								newrows.push(oItem.id);
							} else {
								// existing item
								var rowPos = healthTable.fnGetPosition(row.get(0));
								refreshRow(healthTable, oItem, oHi, rowPos, $("#table_" + key + " tbody tr#" + rowId + " td"), key);
								
								// remove from the stale rows list
								staleRows[staleRows.indexOf(rowId)] = null;
							}
							
							if (oSlave != null){
								// global info on slaves
								slaves[oItem.id] = oHi.slaveAsString;
								// slaves info for this iteration
								slaveItems[oSlave.id] = oHi;
								// make sure this slave is not displaying as a main row
								var slaveRow = $("#table_" + key + " tbody #inv_tr_" + oSlave.id);
								if (slaveRow.length > 0)
								{
									healthTable.fnDeleteRow(healthTable.fnGetPosition(slaveRow.get(0)), null, false);
								}
							}
						}
					}
					
					// remove the stale rows
					for (var i = 0; i < staleRows.length; i++)
					{
						if (staleRows[i] === null || staleRows[i] === undefined)
							continue;

						var staleRow = $("#table_" + key + " tbody #" + staleRows[i]);
						if (staleRow.length > 0)
						{
							healthTable.fnDeleteRow(healthTable.fnGetPosition(staleRow.get(0)), null, false);
						}
					}
						
					// refresh the slave health items
					if (slaveItems.length > 0)
					{
						var openRows = healthTable.fnSettings().aoOpenRows;
						for (var i = 0; i < openRows.length; i++)
						{
							var slaveInfoList = openRows[i].nTr.firstChild.firstChild;
							var oHi = slaveItems[slaveInfoList.id];
							var oSlave = oHi.slave;
	
							$(slaveInfoList).children().each(function(){
								$li = $(this);
								$valueSpan = $li.find("span.value");
								if ($li.prop("id").indexOf("address_") == 0){
									$valueSpan.html(oHi.slaveHostname + ":" + oSlave.port);
								} else if ($li.prop("id").indexOf("vip_") == 0){
									$valueSpan.html(oSlave.vip + ":" + oSlave.port);
								} else if ($li.prop("id").indexOf("version_") == 0){
									$valueSpan.html(oSlave.version);
								} else if ($li.prop("id").indexOf("discovery_") == 0){
									$valueSpan.html(oHi.slaveLastDiscoveryAgo);
								} else if ($li.prop("id").indexOf("message_") == 0){
									if (oSlave.status == null || oSlave.status.message === ""){
										var message = "Message not available";
									} else {
										var message = oSlave.status.message;
									}
									$valueSpan.html(message);
								}
							});
						}
					}
					
					// redraw the table
					healthTable.fnDraw();
					
					// activate actions menues
					for (var i = 0; i < newrows.length; i++)
					{
						$('#inv_menu_' + newrows[i]).menu({
							content: $('#inv_content_' + newrows[i]).html(),		
							maxHeight: 180,
						});
					}
					
					// activate on click event to open the slave row
					if (key === "XBRC")
					{
						$("#table_" + key + " tbody tr td div img.openSlave").click(function(){
							toggleRow(healthTable, this);
						});
					}
				}
			} catch (exception ) {
				Debugger.log(exception);
			} finally {
				refreshing = false;
			}
		},
		error: function(data){
			var message = "Service temporarily unavailable. Come back in a few minutes and refresh this page.";
			if (data.status === 401 || data.responseText.indexOf("Authentication") != -1) {
				message = "Your session has expired. Please use the 'Logout' link to start a new session.";
			}
			else if (data.err !== undefined){
				message += " Error: " + data.err;
			}
			alert(message);
			refreshing = false;
		},
		dataType: "json",
		type: "GET",
	});
}

function createDataTable(type, dynamicHeaders)
{
	if (type === undefined){
		return;
	}
	
	// we only display name and vip columns for xBRCs
	var isXbrc = false;
	
	var displayType = type;
	if (displayType === "XBRC") {
		displayType = "xBRC";
		isXbrc = true;
	} else if (displayType === "XBRMS") {
		displayType = "xBRMS";
	}
	
	$table = $("#table_" + type);
	if ($table.length == 0)
	{
		$monitoredSystemsList = $("ul#monitoredSystems");
		// create table title
		var li = '<li id="system_' + type + '"><h3 class="blueText clear" id="tableTitle_"' + type + '>' + displayType + '</h3>';
		li += '<table class="healthItems light round" id="table_' + type + '" style="width:940px;max-width:940px;"></table></li>';
		
		// create table html element and insert it into the DOM
		$monitoredSystemsList.append(li);
		$table = $("#table_" + type);
	}
	
	// create DataTables header structure
	var aoColumns = new Array();
	if (isXbrc)
		aoColumns.push({"sTitle":"Name","sClass":"center"});
	aoColumns.push({"sTitle":"Host Name","sWidth":"60px","bAutoWidth":false,"sClass":"center"});
	aoColumns.push({"sTitle":"IP","sClass":"center"});
	if (isXbrc)
		aoColumns.push({"sTitle":"VIP","sWidth":"60px","bAutoWidth":false,"sClass":"enter"});
	aoColumns.push({"sTitle":"Version","sClass":"center"});
	aoColumns.push({"sTitle":"Last Ping Time","sClass":"center"});
	if (dynamicHeaders !== undefined && dynamicHeaders.length > 0)
	{
		for (var i = 0; i < dynamicHeaders.length; i++)
		{
			aoColumns.push({"sTitle":dynamicHeaders[i],"sClass":"center"});
		}
	}	
	aoColumns.push({"sTitle":"Health","sClass":"center","bSearchable":false});
	aoColumns.push({"sTitle":"Status","sWidth":"150px","bAutoWidth":false,"sClass":"center","bSortable":false});
	aoColumns.push({"sTitle":"","sWidth":"5px","bAutoWidth":false,"sClass":"center","bSortable":false,"bSearchable":false});

	// decorate the table with DataTables
	var dataTable = $table.dataTable({
		"aoColumns": aoColumns,
		"bPaginate": false,
		"bFilter": true,
		"oLanguage": {
			"sEmptyTable": "There are no " + type + "s being monitored at the moment.",
			"sSearch": "Filter " + type + "s",
		},
	});
	
	$('#table_' + type + ' thead th').last().append('<img style="margin: 2px 0px 0px 2px" src="images/configure_16.png">');
	
	return dataTable;
}

$(function(){
	$("#addXbrcDialog").dialog({
		height:260,
		width:310,
		autoOpen:false,
		modal:true,
	});
    //dojo.event.topic.subscribe("/accessDenied", refreshHealthPage);
    
    <s:iterator value="inventory" var="app">	
		var dynamicHeaders = new Array();
		<s:iterator value="defs" var="def"><s:if test="desc.mandatory()">
			dynamicHeaders.push("<s:property value='desc.name()'/>");
		</s:if></s:iterator>
	    
		createDataTable("${type}", dynamicHeaders);
	    
    </s:iterator>
    
    refresh();
    
    setInterval(function() {
    	refresh();
    }, 30000); // pull every 5 seconds
});
</script>
