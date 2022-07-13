<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
<h2 class="pageTitle"><s:text name="page.title"/></h2>
<div id="attractionViewContainer" class="round" <s:if test="hasBackground">style="background: url('${backgroundImageUrl}') no-repeat"</s:if> >
<div class="legend round">
	<p class="title"><s:text name="legend"/></p>
	<p><img src="images/guest.png"/><span><s:text name="guest"/></span></p>
	<p><img src="images/xguest.png"/><span><s:text name="guest.fastpass"/></span></p>
</div>
<div id="attractionPlan">	
	<s:if test="showSubwayMap"> 
	<s:iterator value="gridItems" var="gi">
		<div class='gridItem divlink' style="background: url('images/grid/<s:property value='image'/>'); top:<s:property value='topPos'/>px; left:<s:property value='leftPos'/>px;" 
			title='<s:property value="gridItem.description"/>'
			onclick="showGuests(<s:if test="gridItem.state.toString() != 'INDETERMINATE'">-1</s:if><s:else>${gridItem.locationId}</s:else>,'${gridItem.state}','${gridItem.xPassOnly}');" >
			<span class="gridItemLabel"><s:property value='gridItem.label'/></span>
		</div>
	</s:iterator>
	</s:if>
	<sx:div 
		theme="ajax" 
		href="attractionviewajax" 
		executeScripts="true"
		showLoadingText="false"
		showErrorTransportText="false"
		listenTopics="/refresh"
		startTimerListenTopics="/startTimer"
		stopTimerListenTopics="/stopTimer"
		updateFreq="500">
	</sx:div>
</div>
</div>
</div>

<s:form theme="simple" id="showGuestsForm" action="showguests">
	<s:hidden name="locationId"/>
	<s:hidden name="state"/>
	<s:hidden name="xpass"/>
</s:form>

<sx:div
	id="showGuestsDialog"
	theme="ajax"
	href="showguests"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleShowGuests" 
	formId="showGuestsForm" >
</sx:div>
