<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:include value="fragments/errorAndMessagePanels.jsp"/>
<s:fielderror />

<!-- This div is the add new transit command modal window -->
<h3>Add Transmit Command to readers at location ${location.name}</h3>

<s:form theme="simple" id="transmitCommandForm" action="edittransmitcommand" medhod="POST">
	<s:hidden name="NoAcRedirect" value="1"/>
	<input type="hidden" id="locationId" name="locationId" value="${locationId}"/>
	<input type="hidden" id="commandId" name="commandId" value="${commandId}"/>
	<input type="hidden" id="action" name="action"/>

	<div>
		<select onchange="transmitCommandSelected()" id="commandTypes" name="commandTypes">
			<s:iterator var="cmd" value="displayCommands">
				<option value="${value}" label="${value.title}"
						id="${value.frequencyMin},${value.frequencyMax},${value.frequencyProgrammable},${value.timeoutMin},${value.timeoutMax},${value.timeoutProgrammable}">${value.title}</option>
			</s:iterator>
		</select>
	</div>
	<dl class="leftJustified firstColumn">
		<dt><label>Frequency <span id="frequency_range"></span></label></dt> 
		<dd><s:textfield tabindex="1" name="frequency" id="frequency"/><s:fielderror fieldName="frequency_error"/></dd>

		<dt><label><s:text name="Transmit mode"/></label></dt> 
		<dd>
			<select onchange="transmitModeSelected()" id="transmitMode" name="transmitMode">
			<s:iterator var="mode" value="displayTransmitModes">
				<option value="${value}">${value}</option>
			</s:iterator>
		</select>
	</dl>
	<dl class="leftJustified secondColumn">
		<dt><label>Timeout <span id="timeout_range"></span></label></dt> 
		<dd><s:textfield tabindex="2" name="timeout" id="timeout"/><s:fielderror fieldName="timeout_error"/></dd>
		
		<dt id="recipientContainerLabel"><label><s:text name="Reply to bands at"/></label></dt> 
		<dd>
			<div id="recipientContainer">
				<div>
					<input type="checkbox" name="enableThreshold" id="recipient_sstt" 
						value="true" onchange="enableThresholdSelected()"/>
					<label for="recipient_sst">Above signal strength transmit threshold</label> 
					<s:textfield tabindex="3" name="threshold" id="threshold"/><s:fielderror fieldName="threshold_error"/>
				</div>
				<ul id="locations">
					<s:iterator var="rec" value="locations">
						<li>
							<input type="checkbox" name="recipient" id="recipient_${key}" value="${key}" onchange="byLocationSelected()"/>
							<label for="recipient_${key}">${value}</label>
						</li>
					</s:iterator>
				</ul>
			</div>
		</dd>
	</dl>
	<div style="float: left; width: 100%;">
		<input type="button" value="Save Command" class="submit" onkeydown="return false;" onclick="saveTransmitCommand(this.form);"/>
	</div>
</s:form>

<script>
transmitCommandSelected();
enableThresholdSelected();
<s:if test="commandSaved">
editLocation(${locationId}, true);
$("#transmitCommandDialog").dialog('close');
</s:if>
</script>