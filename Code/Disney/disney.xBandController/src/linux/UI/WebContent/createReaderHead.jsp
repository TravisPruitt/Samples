<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/form.css" />

<link type="text/css" href="script/jquery-ui-1.8.16.custom.darkblue/css/custom-theme/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />
<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />

<script type="text/javascript" src="script/jquery-ui-1.8.16.custom.darkblue/js/jquery-ui-1.8.16.custom.min.js"></script>

<link rel="stylesheet" type="text/css" href="css/createReader.css" />

<script type="text/javascript" src="script/validation_utils.js"></script>
<script type="text/javascript" src="script/OmniServer.js"></script>
<script type="text/javascript" src="script/OmniServerCollection.js"></script>
<script type="text/javascript" src="script/NetworkAddress.js"></script>
<script type="text/javascript" src="script/TextInputSanitizer.js"></script>
<script type="text/javascript" src="script/Reader.js"></script>

<script type="text/javascript">

var minThreshold = ${readerConfig.minimumThreshold};
var maxThreshold = ${readerConfig.maximumThreshold};
var minGain = ${readerConfig.minimumGain};
var maxGain = ${readerConfig.maximumGain};

var omniServerCollection = new OmniServerCollection();
var associtedOmniServerCollection = new OmniServerCollection();
var isParkEntry = ${parkEntry};

function saveReader(){
	$form = $("form#idReaderSaveForm");
	if ($form.length == 0){
		return;
	}
	
	$errorSpans = $form.find("span.fieldError");
	
	// hide all old error messages
	$errorSpans.each(function(){
		$(this).hide();
	});
	
	var valid = true;

	try
	{
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
		reader.setBioDeviceType($form.find("#idReaderSaveForm_reader_bioDeviceType").val());
		
		valid = reader.isValid();
		
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
				} else if ($this.attr("id").search("reader_macAddress") == 0){
					if (reader.invalid["macAddress"]){
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
	}
	
	if (valid)
	{
		try {
			if (isParkEntry)
			{
				for (var i = 0; i < associtedOmniServerCollection.getLength(); i++)
				{
					var os = associtedOmniServerCollection.getOmniAtIndex(i);
					
					if (os === undefined){
						continue;
					}
					
					var value = os.getId() + ":" + i;
					$form.append('<input type="hidden" id="addedOmniServers_' + os.getId() + '" name="addedOmniServers" value="' + value + '"/>');
				};
			};
		} catch (exception) {
			Debugger.log(exception);
		} finally {
			os = null;
			value = null;
		}
		
		$form.submit();
	};
}

function openAddOmniServerDilog()
{	
	$("#add-omni-dialog").dialog('open');
}

function addOmniServer()
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
	};
}

function removeOmniServer(omniId) {
	if (omniId === undefined){
		return;
	}
	
	try {
		var omniServer = associtedOmniServerCollection.getOmni(omniId);
		var priority = associtedOmniServerCollection.getPriority(omniId);
		var index = associtedOmniServerCollection.getIndex(omniId);
		
		if (omniServer === null){
			return;
		}
		
		omniServerCollection.addOmni(
				omniServer.getId(),
				omniServer.getHostname(),
				omniServer.getDescription(),
				omniServer.getActive(),
				omniServer.getPort(),
				omniServer.getNotActiveReason());
		
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
		index = null;
	};
}

function typeChanged()
{
	try {
		$select = $("select#idReaderSaveForm_readerType");
		var selectedHtml = $select.find("option:selected").html();

		if (selectedHtml === "Long Range"){
			$("#readerXBR").show();
			$("input#idReaderSaveForm_reader_deviceId").hide();
			$("input#idReaderSaveForm_reader_deviceId").val("");
			$('label[for="idReaderSaveForm_reader_deviceId"]').hide();
			$("#deviceIdError").hide();
			$("#reader_deviceId").hide();
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

$(function()
{
	<s:if test="parkEntry">
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
	
	<s:iterator value="omnisForGrabs">
		omniServerCollection.addOmni(${id},"${hostname}","${description}",${active},${port},"${notActiveReason}");
	</s:iterator>
	
	$select = $("select#idReaderSaveForm_readerType");
	var selectedHtml = $select.find("option:selected").html();
	if (selectedHtml === "Tap + xBio"){
		$("div#configureOmnis").show();
	} else {
		$("div#configureOmnis").hide();
	};
	</s:if>

	$("input[name=readerChosenChannelGroups]").each(function() {
		var $this = $(this);
		if ($this.val() == "0") {
			$this.prop('disabled', true);
			$this.prop('title', "Antennas Rx 0, Rx1 are always on.");
		}
		var title = $this.prop('title');
		$("label[for=" + $this.attr('id') +"]").prop('title', title);
	});

	typeChanged();
});
</script>
