<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
    
    
<img src="images/help.png" class="helpLink" style="display:none; float: right;" onclick="openHelpDialog();" id="tabsHelpLink" title="click to show the help dialog"/>
    
<div class="error" style="display: none;" id="locationErrorsWarning">Cannot save location. Please check all Tabs and correct any field errors.</div>
    
<!-- location configuration page -->
<s:if test = 'location.name == "UNKNOWN"'>
<h3><s:text name="location.not.editable"><s:param>${location.name}</s:param></s:text></h3>
</s:if>
<s:else>
<h2 class="pageTitle"><s:text name="page.title"><s:param>location ${location.name}</s:param></s:text></h2>

<s:form action="deletelocation" id="idLocationDeleteForm" theme="simple"><div>
<s:hidden name="NoAcRedirect" value="1"/>
<s:hidden name="locationId"/>
<input type="button" value="Delete This Location" onclick="deleteLocation(${location.id}, '${location.name}')" id="deleteLocationButton" class="blueText"/> 
</div></s:form>

<s:form action="locationsave" id="idLocationSaveForm" theme="simple">
<div>
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="location.id"/>
	<s:hidden name="location.x"/>
	<s:hidden name="location.y"/>
	<s:hidden name="location.singulationTypeId"/>
	<s:hidden name="location.eventGenerationTypeId"/>

<div id="locationtabs">
	<ul>
		<li><a href="#tabs-general"><s:text name="tab.general"/></a></li>
		<li><a href="#tabs-sequences"><s:text name="tab.sequences"/></a></li>
		<li><a href="#tabs-transmit"><s:text name="tab.transmit"/></a></li>
		<s:if test="attractionModel">
		<li><a href="#tabs-vehicle"><s:text name="tab.vehicle"/></a></li>
        <li><a href="#tabs-locationevents"><s:text name="tab.events"/></a></li>
		</s:if>
	</ul>

	<div id="tabs-general">
		<div class="innerTab lightBlueBackground round lightGreyBorder">
           <dl class="leftJustified">
			<dt><label><s:text name="location.name"/></label><span class="fieldError" id="location_name"><s:text name="location.locationName.required"/></span></dt> 
			<dd><s:textfield name="location.name" maxlength="32" /></dd>
			
			<dt><label><s:text name="location.locationId"/></label> <span class="fieldError" id="location_locationId"><s:text name="location.locationId.incorrectType"/></span></dt>
			<dd><s:textfield name="location.locationId" maxlength="32" /></dd>
				
			<dt><label><s:text name="location.locationTypeId"/></label></dt>
			<dd>
				<s:select key="location.locationTypeId" list="locationTypes" listKey="%{key}" listValue="%{value}"></s:select>
			</dd>
			
			<dt><label><s:text name="location.useSecureId"/></label></dt>
			<dd>
				<select id="useSecureId" 					
					name="location.useSecureId">
					<option value="null" <s:if test='location.useSecureId == null'>selected</s:if>>Use the global configuration setting <s:if test='controllerInfo.secureTapId'>(Secure ID)</s:if><s:else>(Public ID)</s:else></option>
					<option value="false" <s:if test='location.useSecureId == false'>selected</s:if>>Public ID</option>
					<option value="true" <s:if test='location.useSecureId'>selected</s:if>>Secure ID</option>
				</select>				
			</dd>
					
			</dl>
			
			<s:if test="!spaceModel">
				<div id="idSendBandStatusDiv">
				<s:checkbox name="location.sendBandStatus" fieldValue="true" 
						label="Send band status messages at this location"/>Send band status messages at this location. Enable this functionality only if all guests are read by long range readers prior to using a touch point at this location.
				</div>
			</s:if>
		</div>
	</div>

    <div id="tabs-sequences">
      <div class="innerTab lightBlueBackground round lightGreyBorder">
        <dl class="leftJustified">
          <dt><label><s:text name="location.success.seq"/></label></dt>
          <dd>
            <select id="selectCustomSequenceList"
                    onchange="onChangeCustomSequenceList();"
                    name="location.successSequence">
            <option value="false" <s:if test='!useCustomSequenceList'>selected</s:if>>Global configuration setting (${controllerInfo.successSequence},${controllerInfo.successTimeout})</option>
            <option value="true" <s:if test='useCustomSequenceList'>selected</s:if>>Custom sequence list</option>
			</select>
          </dd>

          <s:if test='useCustomSequenceList'>
            <div id="locationSubSequencesContainer">
          </s:if>
          <s:else>
            <div id="locationSubSequencesContainer" style="display: none;">
          </s:else>
              <dt>
                <fieldset><legend title="A Success light sequence will cause one row to be played with the given timeout. The chance a row is chosen is based on the percentage.">Success light sequence</legend>
                  <div style="clear: both;" />
                  <table>
                    <thead><tr><th>Name</th><th title="Length of time this will be played.">Timeout (ms)</th><th title="Chance this row will be chosen as the Success sequence.">Likelihood (%)</th><th></th></tr></thead>
                    <tbody>
                      <s:if test = 'useCustomSequenceList'>
                        <s:iterator value="successSequences" status="status">
                      <tr class="subSequenceItemContainer">
                        <td><s:select
                                cssClass="subSequenceSelect"
                                list="sortedSequenceNames"
                                name="location.successSequence"
                                value="%{name}"/>
                        </td>
                        <td title="Length of time this will be played."><s:textfield cssClass="timeout" value="%{timeout}" maxlength="32" size="32" /></td>
                        <td title="Chance this row will be chosen as the Success sequence."><s:textfield cssClass="ratio" maxlength="3" size="3" value="%{ratio}" /></td>
                        <td><div class="remove" onclick="onRemoveSequence(this);"
                              <s:if test="successSequences.size() < 2">style="display: none;"</s:if>>
                              <img src="images/remove.png" />
                             </div>
                        </td>
                      </tr>
                        </s:iterator>
                      </s:if>
                      <s:else>
                      <tr class="subSequenceItemContainer">
                        <td> <s:select
                                cssClass="subSequenceSelect"
                                list="sortedSequenceNames"
                                name="location.successSequence"/>
                        </td>
                        <td title="Length of time this will be played."><s:textfield cssClass="timeout" value="2000" maxlength="32" size="32" /></td>
                        <td title="Chance this row will be chosen as the Success sequence."><s:textfield cssClass="ratio" maxlength="3" size="3" value="100" />
                        <td><div class="remove" onclick="onRemoveSequence(this);" style="display: none;">
                              <img src="images/remove.png" />
                            </div>
                        </td>
                      </tr>
                        </s:else>
                    </tbody>
                  </table>
                  <div class="add" onclick="onAddSequence();">
                    <div>
                      <img src="images/add.png" />
                    </div>
                    <div id="addLabel">
                      Add sequence
                    </div>
                    <span class="fieldError" id="location_successsequences_invalid"><s:text name="location.successsequences.invalid"/></span>
                  </div>
                </fieldset>
              </dt>
            </div>

			<dt><label><s:text name="location.failure.seq"/></label></dt>
			<dd>			
				<select name="location.failureSequence">
					<option value="null" <s:if test='failureSequence == null'>selected</s:if>>Use the global configuration setting (${controllerInfo.failureSequence}, ${controllerInfo.failureTimeout})</option>
					<s:iterator var="it" value="sortedSequenceNames">
						<option value="${it}" <s:if test='%{failureSequence == #it}'>selected</s:if>>${it}</option>
					</s:iterator>
				</select>				
				<s:textfield cssClass="timeout" name="location.failureTimeout" maxlength="32" size="32" /> milliseconds
	            <span class="fieldError" id="location_failureTimeout"><s:text name="location.sequence.timeout"/></span>
	        </dd>
			
			<dt><label><s:text name="location.error.seq"/></label></dt>
			<dd>
				<select name="location.errorSequence">
					<option value="null" <s:if test='errorSequence == null'>selected</s:if>>Use the global configuration setting (${controllerInfo.errorSequence}, ${controllerInfo.errorTimeout})</option>
					<s:iterator var="it" value="sortedSequenceNames">
						<option value="${it}" <s:if test='%{errorSequence == #it}'>selected</s:if>>${it}</option>
					</s:iterator>
				</select>		
				<s:textfield cssClass="timeout" name="location.errorTimeout" maxlength="32" size="32" /> milliseconds
	            <span class="fieldError" id="location_errorTimeout"><s:text name="location.sequence.timeout"/></span>
	        </dd>
	        
	        <dt><label><s:text name="location.tap.seq"/></label></dt>
			<dd>
				<select name="location.tapSequence">
					<option value="null" <s:if test='tapSequence == null'>selected</s:if>>Use the global configuration setting (${controllerInfo.tapSequence}, ${controllerInfo.tapTimeout})</option>
					<s:iterator var="it" value="sortedSequenceNamesNoColors">
						<option value="${it}" <s:if test='%{tapSequence == #it}'>selected</s:if>>${it}</option>
					</s:iterator>
				</select>				
				<s:textfield cssClass="timeout" name="location.tapTimeout" maxlength="32" size="32" /> milliseconds
	            <span class="fieldError" id="location_tapTimeout"><s:text name="location.sequence.timeout"/></span>
	        </dd>
			
			<dt><label><s:text name="location.idle.seq"/></label></dt>
			<dd>
				<select name="location.idleSequence">
					<option value="null" <s:if test='idleSequence == null'>selected</s:if>>Use the global configuration setting (${controllerInfo.sIdleSequence})</option>
					<s:iterator var="it" value="sortedSequenceNamesNoColors">
						<option value="${it}" <s:if test='%{idleSequence == #it}'>selected</s:if>>${it}</option>
					</s:iterator>
				</select>			
			</dd>
		</dl>
	</div>
	</div>
	
	<div id="tabs-transmit">
		<div class="innerTab lightBlueBackground round lightGreyBorder">
			<s:include value="transmitEdit.jsp"/>
		</div>
	</div>
	
	<s:if test="attractionModel">
	<div id="tabs-vehicle">
		<div class="innerTab lightBlueBackground round lightGreyBorder">
		
			<div id="vaLocationConfigEnabledDiv">
			<s:checkbox name="vaLocationConfigEnabled" onClick='enableVehicleAssociation(this)' fieldValue="true" 
					label="Enable Vehicle Association at This Location"/>Enable vehicle association at this location
			</div>

			<div id="vehicleAssociationDiv" <s:if test="!vaLocationConfigEnabled">style="display: none"</s:if> >
			
			<dl class="leftJustified firstColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblVaAlogrithm"/></label>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.vaAlgorithm.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:select 
	                list="vaAlgorithmChoices"
	                name="vaLocationConfig.vaAlgorithm"
					value="vaLocationConfig.vaAlgorithm"/></dd>
			</dl>
		
			<dl class="leftJustified secondColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblRequireVehicleLaserEvent"/></label>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.requireVehicleLaserEvent.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:select 
					list="#{'false':'VEHICLE only', 'true':'VEHICLE and xTPRA'}" 
					name="vaLocationConfig.requireVehicleLaserEvent" 
					value="vaLocationConfig.requireVehicleLaserEvent" /></dd>
			</dl>
		
			<dl class="leftJustified thirdColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblUseVehicleEventTime"/></label>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.useVehicleEventTime.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:select 
					list="#{'false':'xBRC Timestamp', 'true':'Reader Timestamp'}" 
					name="vaLocationConfig.useVehicleEventTime" 
					value="vaLocationConfig.useVehicleEventTime" /></dd>		
			</dl>
			
			<dl class="leftJustified firstColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblMaxAnalyzeGuestEvents"/></label> <span class="fieldError" id="maxAnalyzeGuestEventsError"><s:text name="maxAnalyzeGuestEvents.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.maxAnalyzeGuestEvents.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.maxAnalyzeGuestEvents" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified secondColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblMaxAnalyzeGuestEventsPerVehicle"/></label> <span class="fieldError" id="maxAnalyzeGuestEventsPerVehicleError"><s:text name="maxAnalyzeGuestEventsPerVehicle.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.maxAnalyzeGuestEventsPerVehicle.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.maxAnalyzeGuestEventsPerVehicle" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified thirdColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblMinReadsToAssociate"/></label> <span class="fieldError" id="minReadsToAssociateError"><s:text name="minReadsToAssociate.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.minReadsToAssociate.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.minReadsToAssociate" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified firstColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblOnrideTimeoutSec"/></label> <span class="fieldError" id="onrideTimeoutSecError"><s:text name="onrideTimeoutSec.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.onrideTimeoutSec.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.onrideTimeoutSec" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified secondColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblVehicleTimeOffsetMs"/></label> <span class="fieldError" id="vehicleTimeOffsetMsError"><s:text name="vehicleTimeOffsetMs.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.vehicleTimeOffsetMs.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.vehicleTimeOffsetMs" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified thirdColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblVehicleHoldTimeMs"/></label> <span class="fieldError" id="vehicleHoldTimeMsError"><s:text name="vehicleHoldTimeMs.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.vehicleHoldTimeMs.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.vehicleHoldTimeMs" maxlength="16" /></dd>
			</dl>
		
			<dl class="leftJustified firstColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblTrainTimeoutSec"/></label> <span class="fieldError" id="trainTimeoutSecError"><s:text name="trainTimeoutSec.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.trainTimeoutSec.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.trainTimeoutSec" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified secondColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblLaserBreaksBeforeVehicle"/></label> <span class="fieldError" id="laserBreaksBeforeVehicleError"><s:text name="laserBreaksBeforeVehicle.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.laserBreaksBeforeVehicle.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.laserBreaksBeforeVehicle" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified thirdColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblLaserBreaksAfterVehicle"/></label> <span class="fieldError" id="laserBreaksAfterVehicleError"><s:text name="laserBreaksAfterVehicle.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.laserBreaksAfterVehicle.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.laserBreaksAfterVehicle" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified firstColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblCarsPerTrain"/></label> <span class="fieldError" id="carsPerTrainError"><s:text name="carsPerTrain.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.carsPerTrain.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.carsPerTrain" maxlength="16" /></dd>
			</dl>
			
			<dl class="leftJustified secondColumnOfThree">
				<dt><label><s:text name="vaLocationConfig.lblVaTimeoutSec"/></label> <span class="fieldError" id="vaTimeoutSecError"><s:text name="vaTimeoutSec.incorrectValue"/></span>
					<img class="fieldInfoImage"
			 				title="<s:text name='vatab.vaTimeoutSec.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
				</dt>
				<dd><s:textfield name="vaLocationConfig.vaTimeoutSec" maxlength="16" /></dd>
			</dl>
			<div style="clear:both"></div>
			</div>
		</div>
	</div>
        <div id="tabs-locationevents">
            <div class="innerTab lightBlueBackground round lightGreyBorder">
            	           
				<s:checkbox name="eventsLocationConfig.sendTapToHTTP"  fieldValue="true"
                                label="PSend TAP LOCATIONEVENT events to the HTTP endpoint"/> Send TAP LOCATIONEVENT events to the HTTP update stream                
                <br/> 
                                            	                
                <s:checkbox name="eventsLocationConfig.sendTapToJMS"  fieldValue="true"
                                label="Publish TAP LOCATIONEVENT events on the JMS bus"/> Publish TAP LOCATIONEVENT events on the JMS bus
				<br/>				
				
				<fieldset class="group">					
		
				<legend>
					<s:checkbox name="eventsLocationConfig.enableConfProcessing" onClick='enableLocationEvents(this)' fieldValue="true"
                    	            label="Enable waypoint functionality"/>
                    	Enable long range events confidence processing for this location
                    	<img class="infoImage"
			 				title="<s:text name='eventstab.enableConfProcessing.help'/>"
			 				src='images/info_16.png'
			 				onclick="openInfoDialog($(this))"/>
                </legend>
                    
                <div id="locationEventsDiv" <s:if test="!eventsLocationConfig.enableConfProcessing">style="display: none"</s:if> >                  
                    <s:checkbox name="eventsLocationConfig.sendConfToJMS"
                                label="Publish LOCATIONEVENT and LOCATIONABANDON events on the JMS bus" />
                        Publish LOCATIONEVENT and LOCATIONABANDON events on the JMS bus
                    <div style="clear:both"></div>
                    
                    <s:checkbox name="eventsLocationConfig.sendDeltaConfToJMS"
                                label="Publish confidence delta LOCATIONEVENT events on the JMS bus" />
                        Publish confidence delta LOCATIONEVENT events on the JMS bus
                    <div style="clear:both"></div>

                    <br/>
                    <dl class="leftJustified firstColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblCastMemberDetectDelay"/></label>
                            <span class="fieldError" id="castMemberDetectDelayError">
                                <s:text name="castMemberDetectDelay.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.castMemberDetectDelay.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.castMemberDetectDelay" maxlength="16" /></dd>
                        
                    </dl>

                    <dl class="leftJustified secondColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblGuestDetectDelay"/></label>
                            <span class="fieldError" id="guestDetectDelayError">
                                <s:text name="guestDetectDelay.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.guestDetectDelay.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.guestDetectDelay" maxlength="16" /></dd>
                    </dl>

                    <dl class="leftJustified thirdColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblPuckDetectDelay"/></label>
                            <span class="fieldError" id="puckDetectDelayError">
                                <s:text name="puckDetectDelay.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.puckDetectDelay.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.puckDetectDelay" maxlength="16" /></dd>
                    </dl>

                    <dl class="leftJustified firstColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblConfidenceDelta"/></label>
                            <span class="fieldError" id="confidenceDeltaError">
                                <s:text name="confidenceDelta.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.confidenceDelta.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.confidenceDelta" maxlength="16" /></dd>
                    </dl>

                    <dl class="leftJustified secondColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblLocationEventRatio"/></label>
                            <span class="fieldError" id="locationEventRatioError">
                                <s:text name="locationEventRatio.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.locationEventRatio.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.locationEventRatio" maxlength="16" /></dd>
                    </dl>

                    <dl class="leftJustified thirdColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblChirpRate"/></label>
                            <span class="fieldError" id="chirpRateError">
                                <s:text name="chirpRate.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.chirpRate.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.chirpRate" maxlength="16" /></dd>
                    </dl>

                    <dl class="leftJustified firstColumnOfThree">
                        <dt><label><s:text name="eventsLocationConfig.lblAbandonTimeout"/></label>
                            <span class="fieldError" id="abandonTimeoutError">
                                <s:text name="abandonTimeout.incorrectValue"/>
                            </span>
                            <img class="fieldInfoImage"
			 					title="<s:text name='eventstab.abandonmentTimeout.help'/>"
			 					src='images/info_16.png'
			 					onclick="openInfoDialog($(this))"/>
                        </dt>
                        <dd><s:textfield name="eventsLocationConfig.abandonmentTimeout" maxlength="16" /></dd>
                    </dl>                    

                    <div style="clear:both"></div>
                </div>
                </fieldset>
            </div>
        </div>
	</s:if>
</div>
<input type="button" value="Save Location" onclick="saveLocation(this.form);" id="saveLocationButton" class="blueText topMargin"/>
</div></s:form>
</s:else>

<div id="tabs-locationevents-help" style="display: none">
<h1>Events Tab Help</h1> 
<h2>Overview</h2>
<p>
The xBRC can be configured at each location to generate LOCATIONEVENT messages. These messages are generated in response to guests
tapping on an XFP reader or in response to long range events. The purpose of the LOCATIONEVENT messages is to inform other 
systems of the guests presence at a location. For TAP events, a LOCATIONEVENT is generated whenever a guest taps on an XFP reader.
For long range events, the LOCATIONEVENT is generated whenever a guest is first detected at a location, moves to another location,
or if configured, whenever the calculated confidence value changes due to the guest moving closer or further from a location. The
Events TAB can be used to control which functionality is enabled for this location and what events are sent on the JMS buss and 
which are posted to the HTTP endpoint.
</p>
    
<script>
$("#locationtabs").tabs({
	show: function(event, ui) { buildHelpDialog(ui.panel.id + "-help", "tabsHelpLink"); }
});

<s:if test="showTransmitTab">
$("#locationtabs").tabs("select","#tabs-transmit");
</s:if>
$("#locationListEditAjaxDiv").hide();
</script>
