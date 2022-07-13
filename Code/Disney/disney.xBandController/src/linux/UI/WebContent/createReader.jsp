<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle"><s:text name="page.title"/></h2>

<s:form action="newreadersave" id="idReaderSaveForm" theme="simple"><div>

   <div id="readerIdentification">
   <fieldset>
	<legend>Identification</legend>
	<dl class="firstColumn leftJustified">
		<dt><label><s:text name="reader.readerId"/></label> <span class="fieldError" id="reader_readerId"><s:text name="reader.readerId.required"/></span></dt> 
		<dd><s:textfield name="reader.readerId" /></dd>

		<dt><label><s:text name="reader.location"/></label></dt>
		<dd><s:select key="reader.location.id" list="locations" listKey="%{key}" listValue="%{value}" name="selectedLocation"></s:select></dd>
	</dl>

	<dl class="secondColumn leftJustified">
		<dt><label><s:text name="reader.lane"/></label> <span class="fieldError" id="reader_lane"><s:text name="reader.lane.incorrect"/></span><s:fielderror fieldName="reader.lane" /></dt>
		<dd><s:textfield name="reader.lane"/></dd>
	</dl>

	<dl class="thirdColumn leftJustified">
		<dt><label><s:text name="reader.type"/></label></dt>
		<dd><s:select key="reader.type.ordinal()" list="readerTypes" listKey="key" listValue="value" name="readerType" onchange="typeChanged()"/></dd>

		<dt><label for="idReaderSaveForm_reader_deviceId"><s:text name="reader.deviceId"/></label> <span class="fieldError" id="reader_deviceId"><s:text name="reader.deviceId.incorrect"/></span><s:fielderror id="deviceIdError" fieldName="reader.deviceId" /></dt>
		<dd><s:textfield name="reader.deviceId"/></dd>
	</dl>

    </fieldset>
    </div>

   <div id="readerNetworking">
   <fieldset>
   <legend>Networking</legend>
	<dl class="firstColumn leftJustified">
		<dt><label><s:text name="reader.macAddress"/></label> <span class="fieldError" id="reader_macAddress"><s:text name="reader.macAddress.incorrect"/></span></dt>
		<dd><s:textfield name="reader.macAddress"/></dd>
	</dl>

	<dl class="secondColumn leftJustified">
		<dt><label><s:text name="reader.ipAddress"/></label> <span class="fieldError" id="reader_ipAddress"><s:text name="reader.ipAddress.incorrect"/></span></dt>
		<dd><s:textfield name="reader.ipAddress"/></dd>
	</dl>

	<dl class="thirdColumn leftJustified">
		<dt><label><s:text name="reader.port"/></label> <span class="fieldError" id="reader_port"><s:text name="reader.port.incorrect"/></span><s:fielderror fieldName="reader.port" /></dt>
		<dd><s:textfield name="reader.port"/></dd>
	</dl>
    </fieldset>
    </div>

	<div id="readerSettings">
	<fieldset>
	<legend>Settings</legend>
	<dl class="firstColumn leftJustified">
		<dt><label><s:text name="reader.enabled"/></label></dt> 
		<dd><s:checkbox name="reader.enabled" onClick='document.getElementById("idDisabledReason").disabled = this.checked' fieldValue="true" label="Enabled"/></dd>
	</dl>

	<dl class="secondColumn leftJustified">
		<dt><label><s:text name="reader.disabledReason"/></label></dt> 
		<dd><s:textfield id="idDisabledReason" name="reader.disabledReason" disabled="true"/></dd>
	</dl>

	<dl class="thirdColumn leftJustified">
	</dl>

	</fieldset>
	</div>

   <div id="readerXBR" hidden="true">
   <fieldset>
    <legend>xBR Settings</legend>
	<dl class="firstColumn leftJustified">
		<dt><label>
				<s:text name="reader.signalStrengthThreshold">
					<s:param>${readerConfig.minimumThreshold}</s:param>
					<s:param>${readerConfig.maximumThreshold}</s:param>
				</s:text>
			</label> <span class="fieldError" id="reader_signalStrengthThreshold"><s:text name="reader.signalStrengthThreshold.incorrect"/></span><s:fielderror fieldName="reader.signalStrengthThreshold"/></dt>
		<dd><s:textfield name="reader.signalStrengthThreshold" /></dd>
	</dl>

	<dl class="secondColumn leftJustified">
		<dt><label>
				<s:text name="reader.gain">
					<s:param>${readerConfig.minimumGain}</s:param>
					<s:param>${readerConfig.maximumGain}</s:param>
				</s:text>
			</label> <span class="fieldError" id="reader_gain"><s:text name="reader.gain.incorrect"/></span><s:fielderror fieldName="reader.gain" /></dt>
		<dd><s:textfield name="reader.gain"/></dd>
	</dl>

	<dl class="thirdColumn leftJustified">
		<dt><label title="xBR4: Some antennas may be powered down in groups to conserve energy."><s:text name="reader.antennasLabel"/></label></dt>
		<dd>
		<s:checkboxlist theme="vertical-checkboxlist" title="Antennas Rx 2 to Rx 7 may be powered down in pairs."
                  list="readerAvailableChannelGroups"
                  name="readerChosenChannelGroups"
                  value="readerDefaultChannelGroups" />
		</dd>
	</dl>

    </fieldset>
    </div>

   <div id="readerXBIO" hidden="true">
   <fieldset>
    <legend>xBio</legend>
	<dl class="leftJustified firstColumn">
		<dt><label for="idReaderSaveForm_reader_bioDeviceType"><s:text name="reader.bioDeviceType"/></label> <span class="fieldError" id="reader_bioDeviceType"><s:text name="reader.bioDeviceType.incorrect"/></span><s:fielderror fieldName="reader.bioDeviceType" /></dt> 
		<dd><s:textfield name="reader.bioDeviceType"/></dd>
	</dl>
	<dl class="leftJustified secondColumn">
	</dl>
	<dl class="leftJustified thirdColumn">
	</dl>
    </fieldset>
    </div>

	<div class="clear"></div>
	
	<s:include value="fragments/readerOmniConfiguration.jsp"/>
	
	<input type="button" value="Save Reader" onkeydown="return false;" onclick="saveReader(this.form);" class="blueText topMargin"/>
</div></s:form>

<s:include value="fragments/readerOmniConfigurationDialog.jsp"/>

</div>
