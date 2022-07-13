<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
$(function(){
	// elements refreshed by ajax
	var id = "";	
	var ip = "";	
	var type = "";
	var nameId = "";	
	var portId = "";
	var lastDiscId = "";
	var healthId = "";
	var statusColor = "";
	var statusMessage = "";
	
	// sortable elements
	var venueId = "";
	var haStatus = "";
	
	<s:iterator var="inv" value="inventory" status="statInv">
		<s:iterator var="item" value="value" status="statItem">
		id = "${item.id}";
		ip = "${hostname}";
		type = "${item.type}";		
		
		nameId = "inv_name_" + id;
		portId = "inv_port_" + id;
		lastDiscId = "inv_lastDiscovery_" + id;
		versionId = "inv_version_" + id;
		healthId = "inv_health_" + id;
		msgId = "inv_msg_" + id;
		statusColor = "${statusColor}";
		statusMessage = "${statusMessage}";
		
		venueId = "inv_facilityId_" + id;
		haStatus = "inv_haStatus_" + id;
		
		if (statusMessage == "")
			statusMessage = "OK";
		
		// If we cannot find the health item on the main page, then the main page needs to be refreshed.
		if (document.getElementById("inv_tr_" + id ) == null) {
			refreshHealthPage();
			return;
		}
		
		$("tr#inv_tr_" + id).children("td").each(function(i){
			tdId = $(this).attr("id");
			if (tdId === nameId) {
				$(this).html("${item.name}");
			} else if (tdId === portId){
				$(this).html("${item.port}");
			} else if (tdId === lastDiscId){
				$(this).html("${lastDiscoveryAgo}");
			} else if (tdId === versionId){
				$(this).html("${item.version}");
			} else if (tdId === healthId){
				var currentHealthValue = $(this).find("a").find("img").prop("src");
				
				if (currentHealthValue.indexOf(statusColor.toLowerCase()) == -1)
				{	
					if (statusColor === "Green")
						icon = "green-light.png";
					else if (statusColor === "Yellow")
						icon = "yellow-light.png";
					else if (statusColor === "Red")
						icon = "red-light.png";
					
					$(this).html("<a href='#' onclick='submitHealthItemForm(" + id + ",\"" + type + "\")'><img src='images/" + icon + "'/></a>");
				}
			} else if (tdId === msgId) {
				if ($(this).html() !== statusMessage){
					$(this).html(statusMessage);
				}
			} 
		});
		
		<s:iterator var="fld" value="fields">
			var elementId = 'inv_<s:property value="desc.id()"/>_' + id;
			$element = $('#' + elementId);
			
			if ($element.length == 1) {
				if ('<s:property value="desc.type()"/>' === "Url") {
					url = "<a href='${value}' target='_new'>UI</a>";
					if (url !== $element.html()) {
						$element.html(url);
					}
				} else {
					var currentValue = $element.html();
					var newValue = '<s:property value="value"/>';
					if (currentValue !== newValue && (elementId == venueId || elementId == haStatus)){
						refreshHealthPage();
						return;
					}
					
					$element.html(newValue);
				}
			}
		</s:iterator>
		
		</s:iterator>
	</s:iterator>
});
</script>
