<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
<H1>xBRC Systems</H1>

<table id="xbrcList" class="light round">
	<thead>
		<tr><th>Attraction Name</th><th>xBRC Hostname</th><th>xBRC IP</th><th>Last Ping Time</th><th>Admin URL</th><th>Health</th></tr>
	</thead>	
	<tbody>
		<s:iterator value="inventory" var="info">
		<tr id="inv_tr_${xbrc.id}">
			<td id="inv_venue_${xbrc.id}">${xbrc.venue}</td>
			<td id="inv_hostname_${xbrc.id}">${xbrc.hostname}</td>
			<td id="inv_ip_${xbrc.id}">${xbrc.ip}</td>
			<td id="inv_lastDiscovery_${xbrc.id}">${lastDiscovery}</td>
			<td id="inv_url_${xbrc.id}"><a href='${xbrc.url}' target='_new'>${xbrc.url}</a></td>
			<td id="inv_health_${xbrc.id}" class="centered last">
				<s:if test="alive">
					<a href='${xbrc.url}' target='_new'><img src="images/green-light.png"/></a>
				</s:if>
				<s:else>
					<a href='${xbrc.url}' target='_new'><img src="images/red-light.png"/></a>
				</s:else>
			</td>
		</tr>
		</s:iterator>
	</tbody>
</table>

<dl class="horisontalLegend">
	<dt><img src="images/green-light.png"/></dt><dd><s:text name="legend.light.green"/></dd>
	<dt><img src="images/red-light.png"/></dt><dd><s:text name="legend.light.red"/></dd>
</dl>

</div>

<!-- periodically pull xbrc health status -->
<sx:div 
	theme="ajax" 
	href="xbrclisthealthajax" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="/refresh"
	startTimerListenTopics="/startTimer"
	stopTimerListenTopics="/stopTimer"
	updateFreq="5000"
	preload="true">
</sx:div>
