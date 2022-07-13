<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script type="text/javascript" src="script/Reader.js"></script>

<script type="text/javascript">
$(function(){
	
	var xbrcId = "${xbrcId}";
	var locationName = "${locationName}";
	
	var Readers = new Array();
	
	<s:if test="locationReaders != null">
	<s:iterator var="readerInfo" value="locationReaders" status="readerStat">
		var reader = new Reader();
		
		reader.locationName = locationName;
		reader.xbrcId = xbrcId;
		
		reader.id = "${id}";
		reader.name = "${name}";
		reader.typeDescription = "${type.description}";
		reader.version = "${version}";
		reader.ip = "${ipAddress}";
		reader.mac = "${macAddress}";
		reader.deviceId = "${deviceId}";
		reader.timeLastHello = "<s:property value='getFormattedTimeLastHello("MMM d yyyy HH:mm:ss")'/>";
		reader.status = "${status}";
		reader.statusMessage = "${statusMessage}";
		reader.hasLight = ${type.hasLight};
		reader.batteryLevel = "${batteryLevel}";
		reader.hardwareType = "${hardwareType}";
	
		Readers[<s:property value="#readerStat.index"/>] = reader;
		
	</s:iterator>
	</s:if>
	
	locationReaders(Readers);
});
</script>
