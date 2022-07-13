<link rel="stylesheet" type="text/css" href="css/home.css" />
<link rel="stylesheet" type="text/css" href="css/xbrcHealth.css" />

<script type="text/javascript">
$(document).ready(function()
{	
	$("#addConfigDialog").dialog({height:480,width:360,autoOpen:false});
	$("#deployConfigDialog").dialog({height:330,width:320,autoOpen:false});	
});

function showAddConfigDialog() {
	$("#addConfigDialog").dialog('option', 'title', 'Add xBRC Configuration ');
	$("#addConfigDialog").dialog('open');
	$("span#addConfigStatus").html("");
}

function escape(html) {
	html = html.replace("'", "&#39;");
	html = html.replace("<", "&lt;");
	html = html.replace(">", "&gt;");
	html = html.replace("\"", "&quot;");
	return html;
}

function showDeployConfigDialog(id, venueName, name, description, model) {

	$("td#idDeployModel").html(escape(model));
	$("td#idDeployName").html(escape(name));
	$("td#idDeployVenueName").html(escape(venueName));
	$("td#idDeployDescription").html(escape(description));
	
	$("#deployConfigDialog").dialog('option', 'title', 'Deploy xBRC Configuration ');
	$("span#dployConfigStatus").html("");
	$("#deployConfigDialog").dialog('open');
	
	document.forms.deployXbrcConfigForm.configId.value = id;
}

function removeConfigItem(id, venueName, name) {
	var message = "You are about to delete the " + venueName + " configuration named " + name;
	
	var userResponse = confirm(message);
	if (userResponse === true){
		$("div#idProgress").toggle();
		document.forms.removeConfigForm.id.value = id;
		document.forms.removeConfigForm.submit();
	}
}

function validateAddConfigForm()
{
	var ids = ["idAddName","idAddDescription","idAddVenueCode","idAddVenueName"];
		
	var action = document.forms.addConfigForm.action;
	
	if (action[0].checked)
		ids[ids.length] = "idAddXmlfile";
	
	for (var i = 0; i < ids.length; i++) {
	    id = ids[i];
	    var field = document.getElementById(id);
	    if (field.value == "") {
	    	alert("Please provide values for all the fields.");
	    	return false;
	    }
	}
	return true;	
}

function downloadConfig(id)
{
	document.forms.downloadConfigForm.id.value = id;
	document.forms.downloadConfigForm.submit();
}

function populateFromXbrcConfig()
{
	$("div#getXbrcConfigStatus").html("Connecting to xBRC ...");
	document.forms.getXbrcConfigForm.id.value = document.forms.addConfigForm.xbrcId.value;
	dojo.event.topic.publish('handleGetXbrcConfig');
}

function submitDeployXbrcConfigForm()
{
	$select = $("select#idDownloadXbrcList");
	if ($select.length == 0){
		return;
	}
	
	var xbrcSelected = $select.val();
	if (xbrcSelected === null || $.trim(xbrcSelected).length == 0){
		alert("Please select an xbrc to deploy this configuration to.");
		return;
	}
	
	$("div#idProgress").toggle();
	$("span#dployConfigStatus").html("Deploying configuration ...");
	dojo.event.topic.publish('handleDeployXbrcConfig');
}

function toogleUploadElements(enableUploadElements)
{
	if (enableUploadElements === undefined){
		return;
	}
	
	var addXmlFile = $("#idAddXmlfile");
	var uploadXbrcList = $("#idUploadXbrcList");
	var refreshUploadXbrcList = $("#idRefreshUploadXbrcList");
	
	if (enableUploadElements === false)
	{
		if (addXmlFile.length > 0){
			addXmlFile.prop('disabled', false);
		}
		if (uploadXbrcList.length > 0){
			uploadXbrcList.prop('disabled', true);
		}
		if (refreshUploadXbrcList.length > 0){
			refreshUploadXbrcList.hide();
		}
	}
	else
	{
		if (addXmlFile.length > 0){
			addXmlFile.prop('disabled', true);
		}
		if (uploadXbrcList.length > 0){
			uploadXbrcList.prop('disabled', false);
		}
		if (refreshUploadXbrcList.length > 0){
			refreshUploadXbrcList.show();
		}
	}
}
</script>
