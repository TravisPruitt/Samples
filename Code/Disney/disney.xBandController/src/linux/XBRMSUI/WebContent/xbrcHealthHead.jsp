<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/home.css" />
<link rel="stylesheet" type="text/css" href="css/xbrcHealth.css" />
<script type="text/javascript" src="script/NetworkAddress.js"></script>
<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<script type="text/javascript">
function refreshHealthPage() {
	location.href = "xbrchealth.action";
}

function populateTable(id, ip, venue, hostname, lastDiscovery, nextDiscovery, alive){

	var venueId = "inv_venue_" + id;
	var hostId = "inv_hostname_" + id;
	var modelId = "inv_model_" + model;
	var lastDiscId = "inv_lastDiscovery_" + id;
	var urlId = "inv_url_" + id;
	var healthId = "inv_health_" + id;
	
	$("tr#inv_tr_" + id).children("td").each(function(i){
		tdId = $(this).attr("id");
		if (tdId === venueId) {
			$(this).html(venue);
		} else if (tdId === hostId){
			$(this).html(hostname);
		} else if (tdId === ipId){
			$(this).html(ip);
		} else if (tdId === lastDiscId){
			$(this).html(lastDiscovery);
		} else if (tdId === modelId){
			$(this).html(modelId);
		} else if (tdId === urlId){
			url = "<a href='http://" + ip + ":8080/UI' target='_new'>http://" + ip + ":8080/UI</a>";
			$(this).html(url);
		} else if (tdId === healthId){
			if (alive){
				$(this).html("<a href='http://" + ip + ":8080/UI' target='_new'><img src='images/green-light.png'/></a>");
			} else {
				$(this).html("<a href='http://" + ip + ":8080/UI' target='_new'><img src='images/red-light.png'/></a>");
			}
		}
	});
}

function submitHealthItemForm(id, type){
	
	if (id === "undefined"){
		return;
	}	
	
	$form = $("form#" + type + "Form");
	$hidden = $form.children("input#id").attr("value", id);
	
	if ($form.length > 0){
		$form.submit();
	}
}

function removeHealthItem(id, ip){
	var message = "You are about to permanently delete item " + ip + 
		" from a list of watched systems. This action will delete all historical data " +
		"associated with that item, ex: performance metric data";
	
	var userResponse = confirm(message);
	if (userResponse === true){
        dojo.event.topic.publish('checkAccess');
		$("div#idProgress").toggle();
		document.forms.removeItemForm.id.value = id;
		document.forms.removeItemForm.submit();
	}
}

function inactivateHealthItem(id, ip){
	var message = "You are about to deactivate item " + ip + 
		" from a list of watched systems. This action will remove it from" +
		" this page, but will NOT delete historical data associated with it.";
	
	var userResponse = confirm(message);
	if (userResponse === true){
        dojo.event.topic.publish('checkAccess');
		$("div#idProgress").toggle();
		document.forms.inactivateItemForm.id.value = id;
		document.forms.inactivateItemForm.submit();
	}
}

function showAddXbrcDialog() {
    dojo.event.topic.publish('checkAccess');
    
	$("#addXbrcDialog").dialog('option', 'title', 'Add Health Item ');
	$("#addXbrcDialog").dialog('open');
	$("span#addXbrcStatus").html("");
}

function submitAddXbrc() {
	var validPort = true;
	var validIp = true;
	var status = "Invalid input: ";
	
	try {
		$field = $("input#addXbrcPort");
		if ($field.length > 0){
			validPort = NetworkAddress.isValidPort($field.val());
			if (!validPort){
				status += "port";
				$("span#addXbrcStatus").html(status);
			}
		}
		
		$field = $("input#xbrcIp");
		if ($field.length > 0){
			validIp = NetworkAddress.isValidHostname($field.val());
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
			
			if (document.forms.addXbrcForm.xbrcIp.value == "") {
				refreshAddXbrcDialog("Please enter the IP address", false);
				return;
			}
			
			var status =  "Connecting to " + document.forms.addXbrcForm.xbrcIp.value + " ...";
			$("span#addXbrcStatus").html(status);
			dojo.event.topic.publish('handleAddXbrc');
			
			return;
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
		window.location.href="xbrchealth";
	}
}

function refreshStatus() {
    dojo.event.topic.publish('checkAccess');
	dojo.event.topic.publish('refreshStatus');
}

$(document).ready(function()
{	
	$("#addXbrcDialog").dialog({height:260,width:310,autoOpen:false});
    dojo.event.topic.subscribe("/accessDenied", refreshHealthPage);
    
    <s:iterator value="inventory" var="app">
    <s:iterator value="values" var="val">
	    $('#inv_menu_${item.id}').menu({
			content: $('#inv_content_${item.id}').html(),		
			maxHeight: 180
		});
    </s:iterator>
    </s:iterator>
});
</script>
