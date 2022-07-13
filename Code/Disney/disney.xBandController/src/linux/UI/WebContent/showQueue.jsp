<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<script src="script/xbrcui_utils.js" type="text/javascript"></script>

<div id="idBody" class="round lightBlueBackground shadow padding" style="min-height: 500px">
<h2 class="pageTitle" style="margin-bottom: 0"><s:text name="page.title"/></h2>
<div id="queueAjaxContainer">
<div id="attractionPlan">
	<s:iterator value="readerList" var="guest">
		<img class="reader" src='images/<s:property value="image"/>' style='top:<s:property value="topPos"/>px; left:<s:property value="leftPos"/>px;' />
	</s:iterator>
	<s:if test="model == 'com.disney.xband.xbrc.attractionmodel.CEP'">
		<img src="images/ride.png" id="idRide"/>
	</s:if>
</div>
<div class="legend round">
	<h2>Legend</h2>
	<img src="images/guest.png"/><h3>Guest</h3>
	<img src="images/xguest.png"/><h3>FastPass+ Guest</h3>
	<img src="images/reader.png"/><h3>xBR Reader</h3>
	<img src="images/tap_reader.png"/><h3>xTP Reader</h3>
</div>
<sx:div 
	theme="ajax" 
	href="showqueueajax" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="/refresh"
	startTimerListenTopics="/startTimer"
	stopTimerListenTopics="/stopTimer"
	updateFreq="1000">
</sx:div> 
</div>
</div>

<script type="text/javascript">
<s:iterator value="wallList" var="wall">
	drawLine(<s:property value="x0"/>,<s:property value="y0"/>,<s:property value="x1"/>,<s:property value="y1"/>);
</s:iterator>
</script>