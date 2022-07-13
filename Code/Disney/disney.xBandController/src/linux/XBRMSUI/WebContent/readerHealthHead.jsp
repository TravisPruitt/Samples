<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/home.css" />
<link rel="stylesheet" type="text/css" href="css/readers-gridView.css" />
<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>
<script type="text/javascript" src="script/Reader.js"></script>

<script type="text/javascript">
var dialogOpened = false;

dojo.event.topic.subscribe("ajaxLocationReaders", function onAjax(data, type, request){
	// the "before" and "load" types are handled someplace elsewhere
	if (type === "before")
	{
		$("form#" + $("div#" + data).attr("formId")).append('<input type="hidden" name="NoAcRedirect" value="1"/>');
	}
	else if (type === "error")
	{
		ErrorDialog.open("<p>Failed to retrieve the requested list of readers.</p><p><b>Reason:</b> " + request.getResponseHeader("errMsg") + "</p>");
	}
});


function refreshHealthPage() {
	if (dialogOpened === true){
		return;
	}
	var scrollTop = $(window).scrollTop();
	
	location.href = "readerhealth.action?viewType=${viewType}&scrollTop=" + scrollTop;
}

function refreshStatus() {
    dojo.event.topic.publish('checkAccess');
	dojo.event.topic.publish('refreshStatus');
}
function identifyReader(element, xbrcId, readerId) {
	if (xbrcId !== undefined && readerId != undefined){
		document.forms.idReaderIdentifyForm.xbrcId.value = xbrcId;
		document.forms.idReaderIdentifyForm.readerId.value = readerId;
		dojo.event.topic.publish('handleIdentifyReader');
	}
}
function rebootReader(element, xbrcId, readerId) {
	if (xbrcId !== undefined && readerId != undefined){
		document.forms.idReaderRebootForm.xbrcId.value = xbrcId;
		document.forms.idReaderRebootForm.readerId.value = readerId;
		dojo.event.topic.publish('handleRebootReader');
	}
}
function restartReader(element, xbrcId, readerId) {
	if (xbrcId !== undefined && readerId != undefined){
		document.forms.idReaderRestartForm.xbrcId.value = xbrcId;
		document.forms.idReaderRestartForm.readerId.value = readerId;
		dojo.event.topic.publish('handleRestartReader');
	}
}
function switchView(viewType){
	location.href = "readerhealth.action?viewType=" + viewType;
}

function moreReaders(xbrcId, xbrcIp, xbrcPort, locationId, locationName)
{
	if (xbrcId === undefined || xbrcIp === undefined || xbrcPort === undefined || locationId === undefined)
		return;
	
	// make ajax call to ReaderHealthAjaxAction.locationReaders()
	document.forms.idLocationReadersForm.xbrcId.value = xbrcId;
	document.forms.idLocationReadersForm.xbrcIp.value = xbrcIp;
	document.forms.idLocationReadersForm.xbrcPort.value = xbrcPort;
	document.forms.idLocationReadersForm.locationId.value = locationId;
	document.forms.idLocationReadersForm.locationName.value = locationName;
	dojo.event.topic.publish('handleLocationReaders');
}
function locationReaders(Readers)
{	
	try
	{
		$dialog = $("div#locationReaders");
		if ($dialog.dialog("isOpen")){
			$dialog.dialog("close");
		}
		
		if (Readers === undefined || Readers.length <= 0)
			return;
		
		var reader, readerTable;
		var locationName = Readers[0].locationName;
		var status = "green";
		var statusMessage = "OK";
		var batteryPresent = false;
		var batteryHeader = "";

		for (var i = 0; i < Readers.length; i++)
		{
			reader = Readers[i];
			if ((reader.batteryLevel != null && reader.batteryLevel != "") || (reader.hardwareType != null && reader.hardwareType == "xBR4"))
			{
				batteryPresent = true;
				break;
			}
		}

		if (batteryPresent)
			batteryHeader = "<th>Battery</th>";

		readerTable = "<table class='light round rlInfo'><thead><tr><th>Location</th>"
			+ "<th>Name</th><th>Type</th><th>Version</th><th>Address</th><th>MAC Address</th><th>Device ID</th>"
			+ "<th>Last Hello</th>" + batteryHeader + "<th>Status</th><th>Status Message</th><th>Actions</th>"
			+ "</tr></thead><tbody>";
		
		for (var i = 0; i < Readers.length; i++)
		{
			reader = Readers[i];
			
			status = reader.status.toLowerCase();
			statusMessage = $.trim(reader.statusMessage).length == 0 ? "OK" : reader.statusMessage;
			
			readerTable += "<tr id='ldi-readerInfo_" + reader.id + "'>";
			
			readerTable += "<td>" + reader.locationName + "</td>";
			readerTable += "<td>" + reader.name + "</td>";
			readerTable += "<td>" + reader.typeDescription + "</td>";
			readerTable += "<td>" + reader.version + "</td>";
			readerTable += "<td>" + reader.ip + "</td>";
			readerTable += "<td>" + reader.mac + "</td>";
			readerTable += "<td>" + reader.deviceId + "</td>";
			readerTable += "<td>" + reader.timeLastHello + "</td>";
			if (batteryPresent)
			{
				if (reader.batteryLevel != null && reader.batteryLevel != "")
				{
					var image = "";
					if (reader.batteryLevel <= 10.0)
						image = "battery-0.png";
					else if (reader.batteryLevel <= 20.0)
						image = "battery-20.png";
					else if (reader.batteryLevel <= 40.0)
						image = "battery-40.png";
					else if (reader.batteryLevel <= 60.0)
						image = "battery-60.png";
					else if (reader.batteryLevel <= 80.0)
						image = "battery-80.png";
					else
						image = "battery-100.png";

			 		readerTable += "<td title='" + reader.batteryLevel + "% charged'><img src='images/" + image + "'/></td>";
				}
				else
					readerTable += "<td title='No battery detected'>-</td>";
			}
			readerTable += "<td><img src='images/" + status + "-light.png'/></td>";
			readerTable += "<td>" + statusMessage + "</td>";
			// begin action menu
			readerTable += "<td>";
			if (reader.timeSinceLastHello > (2 * (60*1000))){
				readerTable += "<span title='The xbrc is not able to communicate with this reader.'>Unreachable</span>";
			} else {
				readerTable += "<a id='menu-" + reader.id + "' style='background: none; border: none;' class='fg-button fg-button-icon-right ui-state-default ui-corner-all' href='#'><span class='ui-icon ui-icon-triangle-1-s'></span>Execute</a>";
				readerTable += "<div id='content-" + reader.id + "' class='hidden'><ul>";
				if (reader.hasLight === true){
					readerTable += "<li><a href='#ldi-readerInfo_" + reader.id + "' onclick='identifyReader(this," + reader.xbrcId + "," + reader.id + ")'><img class='fg-menu-icon' src='images/light-16px.png' title='Play an identification media sequence.'/>Light Up</a></li>";
				}
				readerTable += "<li><a href='#ldi-readerInfo_" + reader.id + "' onclick='restartReader(this," + reader.xbrcId + "," + reader.id + ")'><img class='fg-menu-icon' src='images/restart_16.png' title='Restart the reader application.'/>Restart</a></li>";
				readerTable += "<li><a href='#ldi-readerInfo_" + reader.id + "' onclick='rebootReader(this," + reader.xbrcId + "," + reader.id + ")'><img class='fg-menu-icon' src='images/reboot_16.png' title='Reboot the reader.'/>Reboot</a></li>";
				readerTable += "</ul></div>";
			}
			readerTable += "</td>";
			// end action menu
			
			readerTable += "</tr>";
		}
		readerTable += "</tbody></table>";
		
		$dialog.html(readerTable);
		$dialog.dialog('option','title',"Readers at location: " + locationName);
		$dialog.dialog('open');
		
		// instantiate action menues
		for (var i = 0; i < Readers.length; i++){
			reader = Readers[i];
			
			$("#menu-" + reader.id).menu({
				content: $("#content-" + reader.id).html(),		
				maxHeight: 180
			});
		}
	} catch (exception){
		Debugger.log(exception);
	} finally {
		reader = null;
		readerTable = null;
		locationName = null;
		status = null;
		statusMessage = null;
		$dialog = null;
	}
}

function showReaderDetailInfo(readerStatus,locationName,readerName,readerTypeDescription,
		readerSoftwareVersion,readerIp,readerMac,deviceId,statusMessage)
{
	$("div#rdi-status").html("<img src='images/" + readerStatus.toLowerCase() + "-light.png'/>");
	
	$("span#rdi-locname").html(locationName);
	$("span#rdi-rname").html(readerName);
	$("span#rdi-rdesc").html(readerTypeDescription);
	$("span#rdi-rversion").html(readerSoftwareVersion);
	$("span#rdi-rip").html(readerIp);
	$("span#rdi-rmac").html(readerMac);
	$("span#rdi-rdeviceid").html(deviceId);

	$("span#rdi-rstatusMessage").html(statusMessage);
	
	$dialog = $("#reader-detail-info-dialog");
	$dialog.dialog("option","title",readerName + " at location " + locationName);
	
	if ($dialog.dialog("isOpen")){
		$dialog.dialog("close");
	}
	$dialog.dialog('open');
}

$(document).ready(function(){
    dojo.event.topic.subscribe("/accessDenied", refreshHealthPage);
    
    var scrollTop = parseInt('${scrollTop}');
	$(window).scrollTop(scrollTop);

	<s:iterator var="anXbrc" value="inventory" status="xbrcStat">
		<s:iterator var="locInfo" value="loc.readerlocationinfo" status="statLoc">
			
			<s:iterator var="readerInfo" value="readers"> 
			$('#menu-${anXbrc.xbrc.id}_${locInfo.id}_${id}').menu({
				content: $('#content-${anXbrc.xbrc.id}_${locInfo.id}_${id}').html(),		
				maxHeight: 180
			});
			
			</s:iterator>
			
		</s:iterator>
	</s:iterator>
	
	<s:if test="viewType == 'SMALL_GRID'">
		$("div#gridView").addClass("active");
	</s:if>
	<s:else>
		$("div#listView").addClass("active");
	</s:else>
	
	$("#reader-detail-info-dialog").dialog({
		autoOpen: false,
		closeOnEscape: true,
		draggable: true,
		resizable: true,
		position: 'center',
		show: "slide",
		open: function( event, ui ) {
			if (dialogOpened === true){
				return;
			}
			dialogOpened = true;
		},
		close: function( event, ui ) {
			dialogOpened = false;
		},
	});
	
	$("#locationReaders").dialog({
		autoOpen: false,
		closeOnEscape: true,
		draggable: true,
		resizable: true,
		show: "slide",
		title: "loading...",
		position: 'center',
		width: 1000,
		heigth: 370,
		open: function( event, ui ) {
			if (dialogOpened === true){
				return;
			}
			dialogOpened = true;
		},
		close: function( event, ui ) {
			dialogOpened = false;
		},
	});
	
	ErrorDialog.init();
});
</script>
