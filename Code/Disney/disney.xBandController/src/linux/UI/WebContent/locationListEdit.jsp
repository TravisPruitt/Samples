<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody">
   
<div id="locationListEditContainer" class="container">

<div id="leftPanel" class="left">

<!-- The idLocationSaveForm is defined in locationListEditAjax.jsp -->
<sx:div 
	theme="ajax" 
	href="locationsave" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	errorNotifyTopics="/accessDenied"
	listenTopics="handleLocationSave" 
	formId="idLocationSaveForm"
	preload="false" >
</sx:div>
<sx:div 
	theme="ajax" 
	href="identifyreader" 
	executeScripts="false"
	showLoadingText="false"
	showErrorTransportText="true"
    errorNotifyTopics="/accessDenied"
    errorText="Your session has expired"
	listenTopics="handleIdentifyReader"
	formId="idReaderIdentifyForm"
	preload="false" >
</sx:div>

<div id="editContainer" class="round blueText shadow padding lightGreyBorder lightBlueBackground">

<sx:div 
	theme="ajax" 
	href="editlocation" 
	executeScripts="true"
	showLoadingText="true"
	showErrorTransportText="false"
    errorNotifyTopics="/accessDenied"
    errorText="Your session has expired"
	listenTopics="handleEditLocation"
	formId="idEditLocationForm"
	preload="true" >
</sx:div>
<s:form theme="simple" id="idEditLocationForm" action="editlocation">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/>
	<s:hidden name="showTransmitTab"/>
</s:form>

<s:form theme="simple" id="idAddReaderForm" action="addreader">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/>
</s:form>

</div>


<div class="topMargin">
	<div id="newLocationButton"  class="left round blueText shadow lightBlueBorder lightBlueBackground centerContent padding">
	<s:form action="newlocation" id="idNewLocationForm" theme="simple"><div>
		<s:submit value="Create New Location" class="round shadow"/>
	</div></s:form>
	</div>
	
	<div id="newReaderButton" class="right round blueText shadow lightBlueBorder lightBlueBackground centerContent padding">
	<s:form action="newreader" id="idNewReaderForm" theme="simple"><div>
		<s:submit value="Create New Reader" class="round shadow"/>
	</div></s:form>
	</div>
	
</div>

<div style="clear: both;"></div>

<div class="topMargin">
	<div id="notifyXbrcButton" class="round blueText shadow lightBlueBorder lightBlueBackground centerContent padding">
		<s:form action="notifyxbrc" id="idNotifyXbrcForm" theme="simple">
			<div>
				<s:submit value="Finished making changes. Notify the xBRC." class="round shadow"/>
			</div>
		</s:form>
		</div>
	</div>
</div>

<div id="accordionContainer" class="shadow round right">
<div id="locationAccordion" class="accordion innerGlow round">

<s:form action="removereader" id="idReaderUnlinkForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/> 
</s:form>

<s:form action="deletereader" id="idReaderDeleteForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/> 
</s:form>

<s:form action="identifyreader" id="idReaderIdentifyForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/> 
</s:form>

<s:form action="restartreader" id="idReaderRestartForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/> 
</s:form>
<sx:div 
	theme="ajax" 
	href="restartreader" 
	executeScripts="false"
	showLoadingText="false"
	showErrorTransportText="true"
    errorNotifyTopics="/accessDenied"
    errorText="Your session has expired"
	listenTopics="handleRestartReader"
	formId="idReaderRestartForm"
	preload="false" >
</sx:div>

<s:form action="rebootreader" id="idReaderRebootForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="locationId"/>
	<s:hidden name="readerId"/> 
</s:form>
<sx:div 
	theme="ajax" 
	href="rebootreader" 
	executeScripts="false"
	showLoadingText="false"
	showErrorTransportText="true"
    errorNotifyTopics="/accessDenied"
    errorText="Your session has expired"
	listenTopics="handleRebootReader"
	formId="idReaderRebootForm"
	preload="false" >
</sx:div>
						
<s:iterator var="location" value="locMap" status="statLoc">
	<h3>
        <div style="width: 100%;overflow: hidden;">
		    <a href="#${key.id}" id="locationName_${key.id}" onclick="editLocation(${key.id},false)"><s:if test="key.name == 'UNKNOWN'">Unlinked Readers</s:if><s:else><s:property value="key.name"/></s:else></a>
	    </div>
    </h3>
	<div>
		<table class="dark">
			<thead>
				<tr><th>Readers</th></tr>
			</thead>
			<tbody>
			<s:iterator var="reader" value="value" status="statRead"> 
				<tr id="reader-${reader.id}">
					 <td>					 	
					 	<a id="menu-${reader.id}" style="background: none; color: #d7d7d7; border: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#"><span class="ui-icon ui-icon-triangle-1-s"></span><s:text name="readerId"/></a>
						<div id="content-${reader.id}" class="hidden">
						<ul>
                            <li><a href="#reader-${reader.id}" onclick="editReader(this, ${reader.id})"><img class="fg-menu-icon" src="images/settings16.png" title="Modify the reader information."/>Edit</a></li>                            
                            <li><a href="#reader-${reader.id}" onclick="replaceReader(${reader.id}, ${key.id}, '${reader.readerId}')"><img class="fg-menu-icon" src="images/replace.png" title="Replace this reader with a new reader."/>Replace</a></li>
                            <li><a href="#reader-${reader.id}" onclick="deleteReader(${reader.id}, ${key.id}, '${reader.readerId}')"><img class="fg-menu-icon" src="images/remove.png" title="Permanently delete this reader."/>Delete</a></li>
                            <li><a href="#reader-${reader.id}" onclick="restartReader(this, ${reader.id}, ${key.id})"><img class="fg-menu-icon" src="images/restart_16.png" title="Restart the reader application."/>Restart</a></li>
                            <li><a href="#reader-${reader.id}" onclick="rebootReader(this, ${reader.id}, ${key.id})"><img class="fg-menu-icon" src="images/reboot_16.png" title="Reboot the reader."/>Reboot</a></li>
                            <s:if test="type.hasLight() != false">
                                <li><a href="#reader-${reader.id}" onclick="identifyReader(this, ${reader.id}, ${key.id})">
                                    <img class="fg-menu-icon" src="images/light-16px.png" title="Play an identification media sequence."/>Identify Reader</a>
                                </li>
                            </s:if>
                            <s:if test="key.name != 'UNKNOWN'">
                                <li><a href="#reader-${reader.id}" onclick="unlinkReader(${reader.id}, ${key.id}, '${reader.readerId}')"><img class="fg-menu-icon" src="images/unlink16.png" title="Unlink this reader from its location and put it in the unasigned readers' bucket."/>Unlink</a></li>
                            </s:if>
						</ul>
						</div>						
					 </td>					 
				</tr>
			</s:iterator>
			</tbody>
			<s:if test="key.name != 'UNKNOWN'">
				<tfoot>
					<tr><td colspan="4">
					<s:if test="unlinkedReaders.size() > 0">
						<span>Unlinked readers:</span>
						<select onchange="addReader(this, ${key.id})">
							<option>Select to add to this location</option>
							<s:iterator var="ur" value="unlinkedReaders">
								<option value="${ur.id}">${ur.macAddress} <s:if test='readerId != null'>${ur.readerId}</s:if></option>
							</s:iterator>
						</select>
					</s:if>
					<s:else>
						<span>There are no unlinked readers.</span>
					</s:else>
					</td></tr>
				</tfoot>
			</s:if>
		</table>
	</div>
</s:iterator>
</div>
</div>

</div>
</div>

<!-- remove reader -->
<sx:div 
	theme="ajax" 
	href="removereader" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="true"
    errorNotifyTopics="/accessDenied"
    errorText="Your session has expired"
	listenTopics="handleRemoveReader" 
	formId="idRemoveReaderForm"
	preload="false" >
</sx:div>
<s:form theme="simple" id="idRemoveReaderForm" action="removereader">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="readerId"/>
	<s:hidden name="locationId"/>
</s:form>


<s:form theme="simple" id="idReplaceReaderForm" action="replacereader">
	<s:hidden name="NoAcRedirect" value="1"/>
	<s:hidden name="readerId"/>
	<s:hidden name="readerName"/>
	<s:hidden name="newReaderId"/>
	<s:hidden name="locationId"/>	
</s:form>

<!-- dialog to add a new xBRC based on IP address -->
<div id="replaceReaderDialog" style="display: none;">
	<h3>Select a reader to replace the <span id="readerToReplace"></span> reader.</h3>
	
	<s:if test="unlinkedReaders.size() > 0">
	<h4>
		Unlinked readers
	</h4>
	<table class="light" id="unlinkedReplacement">
	<thead>
		<tr><th>Reader Name</th><th>IP</th><th>MAC Address</th><th>Type</th></tr>
	</thead>
	<tbody>
		<s:iterator var="ur" value="unlinkedReaders">
		<tr>
			<td style="word-wrap: break-word;min-width:5.5em;max-width:5.5em;width:5.5em;"><a href="#" onclick="applyReplaceReader(${ur.id}, '${readerId}')"><s:if test='readerId != null'>${ur.readerId}</s:if></a></td>
			<td>${ur.ipAddress}</td>
			<td>${ur.macAddress}</td>
			<td>${ur.type.description}</td>
		</tr>
		</s:iterator>		
	</tbody>
	</table>
	</s:if>
			
	<s:iterator var="location" value="locMap" status="statLoc">
	<s:if test="key.name != 'UNKNOWN'">
	<h4>
		<s:property value="key.name"/>
	</h4>
	<div>
		<table class="light" id="linkedReplacement">
			<thead>
				<tr><th>Reader Name</th><th>IP</th><th>MAC Address</th><th>Type</th></tr>
			</thead>
			<tbody>			
			<s:iterator var="reader" value="value" status="statRead"> 
				<tr>
					 <td style="word-wrap:break-word;min-width:5.5em;max-width:5.5em;width:5.5em;"><a href="#" onclick="applyReplaceReader(${id}, '${readerId}')">${readerId}</a></td>
					 <td>${ipAddress}</td>
					 <td>${macAddress}</td>
					 <td>${type.description}</td>
				</tr>
			</s:iterator>
			</tbody>
			<tfoot>				
			</tfoot>
		</table>
	</div>
	</s:if>
	</s:iterator>
</div>


<!-- The idReaderSaveForm is defined in locationListEditAjax.jsp -->
<sx:div 
	theme="ajax" 
	href="readersave" 
	executeScripts="true"
	showLoadingText="false"
	errorNotifyTopics="/accessDenied"
	showErrorTransportText="false"
	listenTopics="handleReaderSave" 
	formId="idReaderSaveForm"
	preload="false" >
</sx:div>

<!-- The idTransmitReaderSaveForm is defined in locationListEditAjax.jsp -->
<sx:div 
		theme="ajax" 
		href="transmitreadersave" 
		executeScripts="true"
		showLoadingText="false"
		showErrorTransportText="false"
		errorNotifyTopics="/accessDenied"
		listenTopics="handleTransmitReaderSave" 
		formId="idTransmitReaderSaveForm"
		preload="false" >
</sx:div>

<sx:div
	id="transmitCommandDialog"
	theme="ajax"
	href="edittransmitcommand"
	executeScripts="true"
	preload="true"
	showLoadingText="false"
	errorNotifyTopics="/accessDenied"
	showErrorTransportText="true"
	listenTopics="handleShowTransmitCommandForm" 
	formId="transmitCommandForm" >
</sx:div>

<s:form action="deletetransmitcommand" id="idCommandDeleteForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<input type="hidden" id="locationId" name="locationId" value="${location.id}"/>
	<s:hidden name="commandId"/> 
</s:form>

<s:form action="transmitedit" id="transmiteditForm" theme="simple">
	<s:hidden name="NoAcRedirect" value="1"/>
	<input type="hidden" id="locationId" name="locationId" value="${location.id}"/>
</s:form>
