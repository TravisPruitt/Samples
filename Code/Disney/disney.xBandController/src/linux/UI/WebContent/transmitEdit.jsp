<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>	

<fieldset>
<legend>Transmit Zone Group</legend>
<p>
The transmit zone group can be used to separate readers into two separate groups with only
one group transmitting at a time.
</p> 
<select  id="transmitZoneGroup" name="location.transmitZoneGroup">
	<option value="">None (Reader will transmit non-stop)</option>
	<option value="A" <s:if test='location.transmitZoneGroup == "A"'>selected</s:if> >Group A (Reader will transmit for the first 30 seconds)</option>
	<option value="B" <s:if test='location.transmitZoneGroup == "B"'>selected</s:if> >Group B (Reader will transmit for the second 30 seconds)</option>
</select>
</fieldset>

<br>

<fieldset>
<legend>Transmit Commands Sent by Readers at this Location</legend>

<!-- Existing transit commands -->
<div style="clear: both;">

<s:if test="commands.size() > 0">
<p>xBand transmit commands already configured for readers at this location</p>
<table class="light">
<thead>
	<tr><th>Command</th><th>Frequency</th><th>Timeout (sec)</th><th>Transmit Mode</th><th>Recipients</th><th>Delete</th></tr>
</thead>
<tbody>
	<s:iterator var="cmd" value="commands">
	<tr>
		<td>${key.command.title}</td>
		<td>${key.interval}</td>
		<td>${key.timeoutSec}</td>
		<td>${key.mode}</td>
		<td>${value}</td>
		<td><a href="#" onclick="deleteCommand(${key.id}, ${location.id})" title="Permamently delete this command."><img class="fg-menu-icon" src="images/remove.png" title="Permanently delete this command."/></a></td>
	</tr>
	</s:iterator>		
</tbody>
</table>
</s:if>

<h3 style="line-height: 20px;" >Add a command <img style="vertical-align: middle;" class="fg-menu-icon cursorPointer" onclick="showTransmitCommandDialog(${location.id},-1)" src="images/add.png" title="Add new command"/></h3>
</div>
</fieldset>

<br> 

<fieldset>
<legend>Transmit Readers</legend>

<s:if test="transmitReaders.size() > 0">
<p>Readers available at this location for transmitting ordered in HA transmit priority</p>
<table class="light" id="transmitReadersTable">
<thead>
	<tr><th>Name</th><th>IP</th><th>Hardware Type</th><th>Enable Transmit</th><th>HA Priority</th><th>Up/Down</th></tr>
</thead>
<tbody>	

	<s:iterator var="rdr" value="transmitReaders">
	<tr>
		<td><input type="hidden" name="transmitterId" value="${id}"/>${readerId}</td>
		<td>${ipAddress}</td>
		<td>${hardwareType}</td>
		<td style="text-align: center;"><input type="checkbox" name="transmitter" value="${id}" <s:if test="transmitter">checked</s:if>/></td>
		<td class="haPriority"><s:textfield maxlength="5" size="5" name="transmitterHaPriority"></s:textfield></td>
		<td>
			<img class="fg-menu-icon cursorPointer" onclick="moveReaderUpDown(${id}, $(this), true)" src="images/uparrow.png" title="Raise priority"/>
			<img class="fg-menu-icon cursorPointer" onclick="moveReaderUpDown(${id}, $(this), false)" src="images/downarrow.png" title="Lower priority"/>
		</td>
	</tr>
	</s:iterator>	
</tbody>
</table>
</s:if>
<s:else>
	There are no long range readers installed at this location.
</s:else>
</fieldset>

