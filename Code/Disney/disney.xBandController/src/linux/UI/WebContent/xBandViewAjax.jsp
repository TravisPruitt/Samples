<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

var ssaMin = ${eventConfig.ssaMin};
var ssaMax = ${eventConfig.ssaMax};

function updateSingulation(elemId, value){
	var dd = $("div#singulation table tr td#" + elemId);
	if (dd){
		dd.html("<span>" + value + "</span>");
	}
}

$(function(){	
	
	// clear out all the previous progress bar values
	$("div.ui-progressbar").each(function(i){
		$(this).progressbar("option", "value", 0);
		//show actual ampified signal strength on hover
		$(this).attr("title", "");
		$(this).prev("span").html("");
	});
	
	// update the page with current events
	var locId, readerId, channelId, locAmpSS, posLocAmpSS, ampSS, formattedAmpSS, ampSSProgBar, readerScaledThreshold;
	<s:iterator var="loc" value="locBeans" status="statLoc">
		locId = "${loc.id}";
		locAmpSS = "${loc.readersSSMax}";
		posLocAmpSS = parseFloat("${loc.strongestFormattedReader}");		
		
		//populate a location avarage amplified signal strength
		var locSSProgBar = $("#" + "pb_loc_" + locId);
		if (locSSProgBar) {
			locSSProgBar.progressbar("option", "value", posLocAmpSS);
			locSSProgBar.attr("title", locAmpSS);
			
			//show actual ampified signal strenth number next to the progress bar
			//with the signal strength being in a negative range what does indicate "no signal"? Using zero as no signal
			if (locAmpSS == 0){
				locSSProgBar.prev("span#" + "pb_loc_" + locId + "_value").html("");
			} else {
				locSSProgBar.prev("span#" + "pb_loc_" + locId + "_value").html(locAmpSS);
			}
		}

		//populate amplified signal strength of the individual channels
		<s:iterator var="read" value="readers" status="statRead">
			readerId = "${read.readerId}";
			
			<s:iterator var="chan" value="channelCollection" status="statChan">
			
				channelId = "${chan.channelId}";
				ampSS = "${chan.signalStrength}";
				formattedAmpSS = parseFloat("${chan.formattedSignalStrength}");
				ampSSProgBar = $("#" + "pb_" + locId + "_" + readerId + "_" + channelId);
				readerScaledThreshold = "${read.scaledThreshold}%";
				
				if (ampSS == 0){
					formattedAmpSS = 0;
				}else if (ampSS <= ssaMin){
					formattedAmpSS = 0;
				} else if (ampSS >= ssaMax){
					formattedAmpSS = 101;
				}
				
				if (ampSSProgBar) {
					//progress of the progress bar scaled to 63 being 100%
					ampSSProgBar.progressbar("option", "value", formattedAmpSS);
					//show actual ampified signal strenth on hover
					ampSSProgBar.attr("title", formattedAmpSS);
					//indicate the threshold
					ampSSProgBar.children(".pbThreshold").css("width", readerScaledThreshold);				
					
					//show actual ampified signal strenth number next to the progress bar
					//with the signal strength being in a negative range what does indicate "no signal"? Using zero as no signal
					if (ampSS == 0){
						ampSSProgBar.prev("span#" + "pb_" + readerId + "_" + channelId + "_signalStrength_value").html("");
					} else {
						ampSSProgBar.prev("span#" + "pb_" + readerId + "_" + channelId + "_signalStrength_value").html(ampSS);
					}
				}
			</s:iterator>
		</s:iterator>
	</s:iterator>
	
	//populate singulation output
	updateSingulation("gstState", "${guestStatus.gst.state}");
	updateSingulation("gstReader", "${guestStatus.gst.lastReader}");
	updateSingulation("gstTime", "${guestStatus.formattedTimeLatestAtReader}");

});
</script>

<script type="text/javascript">
showtime();
hideProgress();
</script>
