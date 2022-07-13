<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
$(function(){
	var statusColors = new Array();
		statusColors[0] = "Green";
		statusColors[1] = "Yellow";
		statusColors[2] = "Red";
		
		var statusMessage, hostName, facilityId, readerName, readerTypeDesc, readerVersion, readerIp, readerLastTimeHello, deviceId;
		
	    /// Elements affecting sort order. Changes to these force page refresh.
	    var xbrcName = "";
	    var locName = "";
	    var statusColor = "";
	    var previousXbrcCount = -1;
	    
	    // Refresh when xbrc has been added or deleted.
	    // Doesn't take care of the unlikely case of same number of xbrcs being removed and added at the same time, but is fast.
	    $xbrcsContainer = $("ul#xbrcs");
	    if ($xbrcsContainer.length == 0){
	    	// grid view
	    	$xbrcsContainer = $("table#xbrcs");
	  		previousXbrcCount = $xbrcsContainer.find("tr.captionRow").length;
	    } else {
	    	//list view
	    	previousXbrcCount = $xbrcsContainer.children("li.xbrc").length;
	    }
	   
	    var currentXbrcCount = <s:property value='inventory.size()'/>;
	    if (previousXbrcCount !== currentXbrcCount){
	    	refreshHealthPage();
            return;
	    }
	    
	    <s:iterator var="anXbrc" value="inventory" status="xbrcStat">

	    // refresh when xbrc had been removed
        $item = $("#xname_${anXbrc.xbrc.id}");
        if ($item.length == 0) {
            refreshHealthPage();
            return;
        }

        xbrcName = "${anXbrc.xbrc.name}";
        if (xbrcName !== $item.html()){
        	// change affecting sort order
        	refreshHealthPage();
            return;
        }
       	$item.html(xbrcName);

       	hostName = "${anXbrc.xbrc.hostname}";
        $item = $("#xhostname_${anXbrc.xbrc.id}");
        if (hostName !== $item.html()){
        	$item.html(hostName);
        }

        facilityId = "${anXbrc.xbrc.facilityId}";
        $item = $("#xfacid_${anXbrc.xbrc.id}");
        if (facilityId !== $item.html()){
        	refreshHealthPage();
            return;
        }

        <s:iterator var="locInfo" value="loc.readerlocationinfo" status="locStat">
        
     	// exists on the small grid view only
        $item = $("#lname_${locInfo.id}");
        locName = "${locInfo.name}";
        if ($item.length > 0 && $item.html() !== locName){
        	// change affecting sort order
        	refreshHealthPage();
        	return;
        }
        
        <s:iterator var="readerInfo" value="readers" status="readerStat">
            
        	if ($("#readerInfo_${anXbrc.xbrc.id}_${locInfo.id}_${id}") == undefined) {
        		refreshHealthPage();
                return;
            }

        	// exists on the list view only
        	$item = $("#lname_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            locName = "${locInfo.name}";
            if ($item.length > 0 && $item.html() !== locName){
            	// change affecting sort order
            	refreshHealthPage();
            	return;
            }
            
            readerName = "${name}";
            $item = $("#rname_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (readerName !== $item.html()){
            	$item.html(readerName);
            }

            readerTypeDesc = "${type.description}";
            $item = $("#rdescription_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (readerTypeDesc !== $item.html()){
            	$item.html(readerTypeDesc);
            }

            readerVersion = "${version}";
            $item = $("#rversion_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (readerVersion !== $item.html()){
            	$item.html(readerVersion);
            }

            readerIp = "${ipAddress}";
            $item = $("#rip_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (readerIp !== $item.html()){
            	$item.html(readerIp);
            }
            
            deviceId = "${deviceId}";
            $item = $("#rdeviceid_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (deviceId !== $item.html()){
            	$item.html(deviceId);
            }
         
            readerLastTimeHello = "<s:property value='getFormattedTimeLastHello("MMM d yyyy HH:mm:ss")'/>";
            $item = $("#rhello_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (readerLastTimeHello !== $item.html()){
            	$item.html(readerLastTimeHello);
            }
            
            statusColor = "${status}";
            $item = $("#rstatus_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if ($item !== undefined) {
            	var statusImage = '<img src="images/' + statusColor.toLowerCase() + '-light.png">';
                
                if (jQuery.trim($item.html()) !== statusImage){
                	// change affecting sort order
                	refreshHealthPage();
                	return;
                }
            }
              
            $item = $("#grid-rstatus_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            <s:if test="statusMessage == null">
            	statusMessage = "";
            </s:if>
            <s:else>
            	statusMessage = "${statusMessage}";
            </s:else>
            
            if ($item !== undefined){
            	// link element on grid view
            	$item.addClass(statusColor);
            	for (var i = 0; i < statusColors.length; i++) {
            		if (statusColors[i] !== statusColor) {
            			$item.removeClass(statusColors[i]);
            		}
            	}
            	
               	$item.attr('onclick', "showReaderDetailInfo('${status}','${locInfo.name}','${name}','${type.description}','${version}','${ipAddress}','${macAddress}','" + statusMessage + "')");
            }

            $item = $("#rstatusmessage_${anXbrc.xbrc.id}_${locInfo.id}_${id}");
            if (statusMessage !== $item.html()){
            	$item.html(statusMessage);
            }

        </s:iterator>
    	</s:iterator>
        
    </s:iterator>
});
</script>
