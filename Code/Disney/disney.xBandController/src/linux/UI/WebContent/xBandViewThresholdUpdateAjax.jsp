<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
$(function(){
	var locId, readerId, channelId, readerScaledThreshold;
	
	locId = "${reader.location.id}";
	readerId = "${reader.readerId}";
	readerScaledThreshold = "${reader.scaledThreshold}%";
	
	<s:iterator var="chan" value="reader.channelCollection" status="statChan">
		channelId = "${chan.channelId}";
		ampSSProgBar = $("#" + "pb_" + locId + "_" + readerId + "_" + channelId);
				
		if (ampSSProgBar) {
			ampSSProgBar.children(".pbThreshold").css("width", readerScaledThreshold);
		}
	</s:iterator>

});
</script>

<script type="text/javascript">
showtime();
hideProgress();
</script>
