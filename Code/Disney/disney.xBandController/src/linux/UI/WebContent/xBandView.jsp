<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round">

<div id="xbandContainer" class="container">
<h2 class="pageTitle"><s:text name="guestStatus.gst.bandId"/>&nbsp;${guestStatus.bandId}</h2>

<div id="singulation" class="round shadow">
	<table class="dark innerGlow round" cellspacing="0" cellpading="0">
		<tr><th colspan="2"><h2><s:text name='singulation.output'/></h2></th></tr>
		<tr>
			<td><label><s:text name="guestStatus.gst.state"/></label></td> 
			<td id="gstState" class="lastRow"></td>
		</tr>
		<tr>
			<td><label><s:text name="guestStatus.gst.reader"/></label></td> 
			<td id="gstReader" class="lastRow"></td>
		</tr>
		<tr>
			<td><label><s:text name="guestStatus.time"/></label></td> 
			<td id="gstTime" class="lastRow"></td>
		</tr>
	</table>
</div>

<div class="accordion">
<s:iterator var="location" value="locMap" status="statLoc">
	<div class="ui-accordion-header shadow ui-helper-reset ui-state-default ui-corner-all" role="tab" aria-expanded="false" aria-selected="false" tabindex="-1">
		<span class="ui-icon ui-icon-triangle-1-e"></span>
		<a href="#"><s:property value="key.name"/></a>
		<div class="locationSignalStrength">
			<span id="pb_loc_${key.id}_value" class="valueText"></span>
			<div id="pb_loc_${key.id}" class="locationProgressbar"></div>
		</div>
	</div>
	<div class="ui-accordion-content shadow ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active" role="tabpanel">
		<s:iterator var="reader" value="value" status="statRead"> 
			<table class="light round">
				<tr><th colspan="4"><h3>Reader <s:property value="readerId"/></h3></th></tr>
				<tr>
					<td class="label"><label><s:text name="reader.gain"/></label></td>
					<td id="gain_loc_${key.id}_${reader.readerId}" class="gain" title="${reader.readerId}">
						<div id="slider_${reader.readerId}" class="gainSlider" title="${reader.gain}"></div>
					</td>
					<td class="label"><label><s:text name="reader.channel.1"/></label></td>
					<td id="loc_${key.id}_${reader.readerId}_0" title="${reader.readerId}" class="lastRow">
						<div id="pb_${reader.readerId}_0_signalStrength" class="readerSignalStrength">
							<span id="pb_${reader.readerId}_0_signalStrength_value" class="valueText"></span>
							<div id="pb_${key.id}_${reader.readerId}_0" class="progressbar">
								<div class="pbThreshold" style="width: ${reader.scaledThreshold}%"></div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="label"><label><s:text name="reader.signalStrengthThreshold"/></label></td>
					<td id="threshold_loc_${key.id}_${reader.readerId}"  class="threshold" title="${reader.readerId}">
						<div id="threshold_${reader.readerId}" class="thresholdSlider" title="${reader.threshold}"></div>
					</td>
					<td class="label"><label><s:text name="reader.channel.2"/></label></td>
					<td id="loc_${key.id}_${reader.readerId}_1" title="${reader.readerId}" class="lastRow">
						<div id="pb_${reader.readerId}_1_signalStrength" class="readerSignalStrength">
							<span id="pb_${reader.readerId}_1_signalStrength_value" class="valueText"></span>
							<div id="pb_${key.id}_${reader.readerId}_1" class="progressbar">
								<div class="pbThreshold" style="width: ${reader.scaledThreshold}px"></div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</s:iterator>
	</div>
</s:iterator>
</div>


</div>
</div>

<!-- periodically update the gain, threshold, and signal strength values for all readers -->
<s:url id="xbandviewajax" action="xbandviewajax">
	<s:param name="bandId" value="%{guestStatus.bandId}"/>
	<s:param name="guestId" value="%{guestStatus.gst.id}"/>
</s:url>
<sx:div 
	theme="ajax" 
	href="%{xbandviewajax}" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="/refresh"
	startTimerListenTopics="/startTimer"
	stopTimerListenTopics="/stopTimer"
	updateFreq="1500"
	preload="true">
</sx:div>

<!-- invoke when user updates gain value -->
<sx:div 
	theme="ajax" 
	href="gainupdate" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleGainUpdate" 
	formId="idGainUpdateForm"
	preload="false" >
</sx:div>
<s:form theme="simple" id="idGainUpdateForm" action="gainupdate">
	<s:hidden name="readerId"/>
	<s:hidden name="gain" />
</s:form>

<!-- invoke when user updates threshold value -->
<sx:div 
	theme="ajax" 
	href="thresholdupdate" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleThresholdUpdate" 
	formId="idThresholdUpdateForm"
	preload="false" >
</sx:div>
<s:form theme="simple" id="idThresholdUpdateForm" action="thresholdupdate">
	<s:hidden name="readerId"/>
	<s:hidden name="threshold" />
</s:form>