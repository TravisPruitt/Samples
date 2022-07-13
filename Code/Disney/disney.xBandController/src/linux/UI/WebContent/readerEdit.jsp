<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<!-- reader configuration page -->
	<h2 class="pageTitle"><s:text name="page.title"><s:param>reader ${reader.readerId}</s:param></s:text></h2>
	
	<s:form action="readersave" id="idReaderSaveForm" theme="simple"><div>		
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="reader.id"/>
	<s:hidden name="reader.locationId"/>
	<s:hidden name="reader.timeLastHello"/>
	<s:hidden name="reader.singulationGroup"/>
	<s:hidden name="reader.x"/>
	<s:hidden name="reader.y"/>
	<s:hidden name="reader.lastIdReceived"/>
	<s:hidden name="reader.macAddress"/>
	<s:hidden name="reader.hardwareType"/>
	<s:hidden name="reader.lastReaderTestTime"/>
	<s:hidden name="reader.lastReaderTestSuccess"/>
	<s:hidden name="reader.lastReaderTestUser"/>

	<div id="Identification">
	<fieldset>
	<legend>Identification</legend>

	<dl class="leftJustified firstColumnOfThree">
		<dt><label><s:text name="reader.readerId"/></label> <span class="fieldError" id="reader_readerId"><s:text name="reader.readerId.required"/></span></dt> 
		<dd><s:textfield name="reader.readerId" /></dd>
	</dl>

	<dl class="leftJustified secondColumnOfThree">
		<dt><label><s:text name="reader.lane"/></label> <span class="fieldError" id="reader_lane"><s:text name="reader.lane.incorrect"/></span></dt>
		<dd><s:textfield name="reader.lane"/></dd>
	</dl>

	<dl class="leftJustified thirdColumnOfThree">
		<dt><label><s:text name="reader.type"/></label></dt> 
	    <dd><s:select key="reader.type.ordinal()" list="readerTypes" listKey="key" listValue="value" name="readerType" onkeypress="typeChanged()" onchange="typeChanged()"/></dd>
		<dt><label for="idReaderSaveForm_reader_deviceId"><s:text name="reader.deviceId"/></label> <span class="fieldError" id="reader_deviceId"><s:text name="reader.deviceId.incorrect"/></span></dt> 
		<dd><s:textfield name="reader.deviceId" /></dd>
	</dl>

	</fieldset>
	</div> <!-- Identification -->



	<div id="Networking">
	<fieldset>
	<legend>Networking</legend>

	<dl class="leftJustified firstColumnOfThree">
		<dt><label><s:text name="reader.macAddress"/></label></dt>
		<dd>${reader.macAddress}</dd>
	</dl>

	<dl class="leftJustified secondColumnOfThree">
		<dt><label><s:text name="reader.ipAddress"/></label> <span class="fieldError" id="reader_ipAddress"><s:text name="reader.ipAddress.incorrect"/></span></dt>
		<dd><s:textfield name="reader.ipAddress"/></dd>
	</dl>

	<dl class="leftJustified thirdColumnOfThree">
		<dt><label><s:text name="reader.port"/></label> <span class="fieldError" id="reader_port"><s:text name="reader.port.incorrect"/></span></dt>
		<dd><s:textfield name="reader.port"/></dd>		
	</dl>

	</fieldset>
	</div> <!-- Networking -->


	<div id="readerSettings">
	<fieldset>
	<legend>Settings</legend>

	<dl class="leftJustified firstColumnOfThree">
		<dt><label><s:text name="reader.enabled"/></label></dt> 
		<dd><s:checkbox name="reader.enabled" onClick='document.getElementById("idDisabledReason").disabled = this.checked' fieldValue="true" label="Enabled"/></dd>
	</dl>

	<dl class="leftJustified secondColumnOfThree">
		<dt><label><s:text name="reader.disabledReason"/></label></dt> 
		
		<s:if test="reader.enabled">
			<dd><s:textfield id="idDisabledReason" name="reader.disabledReason" disabled="true"/></dd>
		</s:if>
		<s:else>
			<dd><s:textfield id="idDisabledReason" name="reader.disabledReason"/></dd>
		</s:else>
	</dl>

	<dl class="leftJustified thirdColumnOfThree">
		<dt><label><s:text name="reader.modelData"/></label> <span class="fieldError" id="reader_modelData"><s:text name="reader.modelData.incorrect"/></span></dt> 
		<dd><s:textfield name="reader.modelData" /></dd>		
	</dl>

	</fieldset>
	</div> <!-- readerSettings -->


	<div id="readerXBR" hidden="true">
	<fieldset>
	<legend>xBR Settings</legend>

    <div id="readerXBRcolumns">
	<dl class="leftJustified firstColumnOfThree">
		<dt><label>
				<s:text name="reader.signalStrengthThreshold">
					<s:param>${readerConfig.minimumThreshold}</s:param>
					<s:param>${readerConfig.maximumThreshold}</s:param>
				</s:text>
			</label> <span class="fieldError" id="reader_signalStrengthThreshold">
				<s:text name="reader.signalStrengthThreshold.incorrect">
					<s:param>${readerConfig.minimumThreshold}</s:param>
					<s:param>${readerConfig.maximumThreshold}</s:param>
				</s:text>
				</span></dt>
		<dd><s:textfield name="reader.signalStrengthThreshold" /></dd>
	</dl>

	<dl class="leftJustified secondColumnOfThree">
		<dt><label>
				<s:text name="reader.gain">
					<s:param>${readerConfig.minimumGain}</s:param>
					<s:param>${readerConfig.maximumGain}</s:param>
				</s:text>
			</label> <span class="fieldError" id="reader_gain">
				<s:text name="reader.gain.incorrect">
					<s:param>${readerConfig.minimumGain}</s:param>
					<s:param>${readerConfig.maximumGain}</s:param>
				</s:text>
				</span></dt>
		<dd><s:textfield name="reader.gain"/></dd>
	</dl>

	<s:if test="reader.hardwareType == 'xBR4'">
	<dl class="leftJustified thirdColumnOfThree">
		<dt><label title="xBR4: Some antennas may be powered down in groups to conserve energy."><s:text name="reader.antennasLabel"/></label></dt>
		<dd>
		<s:checkboxlist theme="vertical-checkboxlist" title="Antennas Rx 2 to Rx 7 may be powered down in pairs."
                  list="readerAvailableChannelGroups"
                  name="readerChosenChannelGroups"
                  value="readerDefaultChannelGroups" />
		</dd>
	</dl>
	</s:if>
	
    </div>

	</fieldset>
	</div> <!-- readerXBR -->



	<div id="readerXBIO" hidden="true">
	<fieldset>
	<legend>xBio</legend>

	<dl class="leftJustified firstColumnOfThree">
		<dt><label for="idReaderSaveForm_reader_bioDeviceType"><s:text name="reader.bioDeviceType"/></label> <span class="fieldError" id="reader_bioDeviceType"><s:text name="reader.bioDeviceType.incorrect"/></span></dt> 
		<dd><s:textfield name="reader.bioDeviceType"/></dd>
	</dl>

	<dl class="leftJustified secondColumnOfThree">
	</dl>

	<dl class="leftJustified thirdColumnOfThree">
	</dl>

	</fieldset>
	</div> <!-- readerXBIO -->


	<div id="readerXBRC">
	<fieldset>
	<legend>Reported by xBRC</legend>

	<dl class="leftJustified firstColumnOfThree">
		<dt><label><s:text name="reader.minXbrcVersion"/></label></dt>
		<dd>${reader.minXbrcVersion}</dd>
		<dt><label><s:text name="reader.version"/></label></dt>
		<dd>${reader.version}</dd>
	</dl>

	<dl class="leftJustified secondColumnOfThree">
		<dt><label><s:text name="reader.lastIdReceived"/></label></dt>
		<dd>${reader.lastIdReceived}</dd>		
	</dl>

	<dl class="leftJustified thirdColumnOfThree">
		<dt><label><s:text name="reader.timeLastHello"/></label></dt>
		<dd>${readerTimeLastHello}</dd>
	</dl>
	</fieldset>
		
	<s:if test="reader.lastReaderTestTime != null">
		<div id='idReaderTestStatus'>
		<fieldset>
			<legend>Last reader test status</legend>
		
			<s:if test="reader.lastReaderTestSuccess">			
				Cast member ${reader.lastReaderTestUser} performed reader test on ${reader.lastReaderTestTime}. The test was successful.
			</s:if>
			<s:else>
				Cast member ${reader.lastReaderTestUser} performed reader test on ${reader.lastReaderTestTime} and found problems with the reader.
				<input style="float: right" type="button" value="Clear Test Result" onclick="clearReaderTest(this.form);" class="blueText topMargin"/>
			</s:else>
		</fieldset>
		</div>
	</s:if>
	
	</div> <!-- readerXBRC -->

	<div class="clear"></div>
	
	<s:include value="fragments/readerOmniConfiguration.jsp"/>
	
	<input type="button" value="Save Reader" onclick="saveReader(this.form);" class="blueText topMargin"/>
</div></s:form>

<s:include value="fragments/readerOmniConfigurationDialog.jsp"/>

<script type="text/javascript">
$(function()
{
	<s:if test="parkEntry">
	var omnisForGrabsCollection = new OmniServerCollection();
	var omnisAssociated = new OmniServerCollection();
	
	<s:iterator value="omnisForGrabs">
		omnisForGrabsCollection.addOmni(${id},"${hostname}","${description}",${active},${port},"${notActiveReason}");
	</s:iterator>
	
	<s:iterator value="omnisAssociated">
		omnisAssociated.addOmni(${omniServer.id},"${omniServer.hostname}","${omniServer.description}",${omniServer.active},${omniServer.port},"${omniServer.notActiveReason}",${priority});
	</s:iterator>
	
	initOmniServerElements(omnisForGrabsCollection,omnisAssociated);
	</s:if>

	$("input[name=readerChosenChannelGroups]").each(function() {
		var $this = $(this);
		if ($this.val() == "0") {
			$this.prop('disabled', true);
			$this.prop('title', "Antennas Rx 0, Rx1 are always on.");
		}
		var title = $this.prop('title');
		$("label[for=" + $this.attr('id') +"]").prop('title', title);
	});

	typeChanged();
});
</script>
