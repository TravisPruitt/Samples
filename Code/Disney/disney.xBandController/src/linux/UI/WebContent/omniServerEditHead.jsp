<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/new/legacy-jquery-custom-blue/js/jquery-ui-1.9.2.custom.min.js"></script>
<link type="text/css" href="script/new/legacy-jquery-custom-blue/css/legacy-custom-blue-theme/jquery-ui-1.9.2.custom.min.css" rel="stylesheet" />

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />

<script type="text/javascript" src="script/OmniServer.js"></script>
<script type="text/javascript" src="script/OmniServerCollection.js"></script>
<script type="text/javascript" src="script/NetworkAddress.js"></script>
<script type="text/javascript" src="script/TextInputSanitizer.js"></script>

<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<style type="text/css">
.dataTables_filter label:AFTER {content: "" !important;}
</style>

<script type="text/javascript">
function openAddOmniServerDilog()
{
	$("#add-omni-dialog").dialog('open');
}
function addOmniServer()
{
	$form = $("#idOmniServerAddForm");

	var omniServer = new OmniServer();
	omniServer.setHostname($form.find("#omniServer_hostname").val());
	omniServer.setDescription($form.find("#omniServer_description").val());
	omniServer.setActive($form.find("#omniServer_active").val());
	omniServer.setPort($form.find("#omniServer_port").val());
	omniServer.setNotActiveReason($form.find("#omniServer_notActiveReason").val());
	
	try
	{
		if (omniServer.isValid()) {
			$form.submit();
		} else {
			$errorSpans = $form.find("span.error").each(function(){
				$this = $(this);
				if ($this.attr("id").search("hostname") >= 0){
					if (omniServer.invalid["hostname"]){
						$this.show();
					}
				} else if ($this.attr("id").search("description") >= 0){
					if (omniServer.invalid["description"]){
						$this.show();
					}
				} else if ($this.attr("id").search("active") >= 0){
					if (omniServer.invalid["active"]){
						$this.show();
					}
				} else if ($this.attr("id").search("port") >= 0){
					if (omniServer.invalid["port"]){
						$this.show();
					}
				} else if ($this.attr("id").search("notActiveReason") >= 0){
					if (omniServer.invalid["notActiveReason"]){
						$this.show();
					}
				}
			});
			
			return false;
		};
	} catch (exception) {
		Debugger.log(exception);
	} finally {
		omniServer = null;	// prepare for Garbage COllection
	}
}
function deleteOmniServer(omniId, omniIp)
{
	if (omniId === undefined){
		return;
	}
	
	var userResponse = confirm("You are about to permanently delete an omni server " + omniIp);
	if (userResponse){
		$form = $("#idOmniServerDeleteForm");
		$form.find("input#idOmniServerDeleteForm_omniServer_id").val(omniId);
		$form.submit();
	}
	
	return false;
}

$(function()
{
	$("#add-omni-dialog").dialog({
		autoOpen: false,
		closeOnEscape: true,
		draggable: true,
		resizable: true,
		position: 'center',
		title: 'Add a New Omni Server',
		show: 'slide',
		width: 500,
	});
	
	<s:iterator value="omniServers">
	$('#menu-${key.id}').menu({
		content: $('#content-${key.id}').html(),		
		maxHeight: 180
	});
	</s:iterator>
	
	$("#omniServers").dataTable({
		"bPaginate": false,
		"bFilter": true,
		"oLanguage": {
			"sEmptyTable": "There are no omni servers configured.",
			"sSearch": "Filter items",
		},
	});	
});
</script>
