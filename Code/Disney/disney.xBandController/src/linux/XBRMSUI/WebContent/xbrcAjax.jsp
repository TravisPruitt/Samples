<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
$(function(){
	
var readerHello,readerStatus,readerStatusMessage,idSuffix,readerName,locationName,xbrcId;

xbrcId = "${id}";
	
<s:iterator var="locInfo" value="rlInfo.readerlocationinfo" status="locStat">

locationName = "${name}";
$locRow = $("td#loc_${locInfo.id}");
if ($locRow.html() != null && $locRow.html().search(locationName) < 0){
	// change affecting sort order
	refreshHealthPage(xbrcId);
    return;
}

<s:iterator var="readerInfo" value="readers" status="readerStat">
readerStatus = "<s:property value='status.name()' />";
readerStatusMessage = "${readerInfo.statusMessage}";
readerHello = "<s:property value='getFormattedTimeLastHello("MMM d yyyy HH:mm:ss")'/>";
readerName = "${readerInfo.name}";
idSuffix = "${locInfo.id}_${readerInfo.id}";

$tr = $("tr#readerInfo_" + idSuffix);
if ($tr.length == 0){
	refreshHealthPage(xbrcId);
	return;
}

$tr.children("td#rstatus_" + idSuffix).each(function(i){
	if ($(this).html().search(readerStatus.toLowerCase()) < 0){
		// change affecting sort order
       	refreshHealthPage(xbrcId);
           return;
	}
});
	
$tr.children("td#rname_" + idSuffix).each(function(i){
	if ($(this).html() !== readerName){
		// change affecting sort order
       	refreshHealthPage(xbrcId);
   		return;
	}
});
	
$tr.children("td#rstatusmessage_" + idSuffix).each(function(i){
	if ($(this).html() !== readerStatusMessage){
		$(this).html(readerStatusMessage);
	}
});
	
$tr.children("td#rhello_" + idSuffix).each(function(i){
	if ($(this).html() !== readerHello){
		$(this).html(readerHello);
	}
});

</s:iterator>

</s:iterator>
});
</script>