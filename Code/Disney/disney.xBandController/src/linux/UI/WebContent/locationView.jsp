<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round">
<div id="locationViewContainer" class="container">
<h2 class="pageTitle">Guests at ${location.name} location</h2>

<s:url id="locationviewajax" action="locationviewajax" >
	<s:param name="locationId" value="%{locationId}" />
</s:url>

<sx:div 
	theme="ajax" 
	href="%{locationviewajax}" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="/refresh"
	startTimerListenTopics="/startTimer"
	stopTimerListenTopics="/stopTimer"
	updateFreq="2000">
</sx:div>

<div style="clear:both"></div>
</div>
</div>