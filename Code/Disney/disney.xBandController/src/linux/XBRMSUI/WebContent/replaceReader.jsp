<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
	
	<h2 class="pageTitle"><s:text name="page.title"/></h2>
	
	<img src="images/help.png" class="clickable" style="position: absolute; top:1em; right:1em; z-index: 1000; width:19px;height:19px;padding:none;margin:none;" onclick="openHelpDialog();" id="helpLink" title="click to show the help dialog"/>
	<img src="images/restart_16.png" onclick="refresh()" style="position: absolute; top:1.1em; right:3.2em; z-index: 1000;"title="Refresh the content of this page without reloading it." class="clickable"/>
	
	<s:if test="canAccessAsset('Deny maintenance role')">
	<s:checkbox name="filter" fieldValue="true" label="Only show parks and xBRCs which have at least one reader in the Red health status" labelposition="right"
		onchange="refresh()"/>
	</s:if>
	
	<h3 class="instruction blueText">1. Select the reader you would like to replace:</h3>
	
	<ul id="toBeReplaced" class="horisontal-list color-white">
		<li class="first-layer"><s:select id="parks" cssClass="dk" name="parkId" list="parks" listKey="%{key}" listValue="%{value}" headerKey="-1" headerValue="-- Select Park --" tabindex="1"></s:select><li>
		<li class="first-layer"><s:select id="venues" cssClass="dk" name="facilityId" list="existingReaders" headerKey="-1" headerValue="-- Select Venue --" tabindex="2"></s:select><li>
		<li class="first-layer"><s:select id="readers" cssClass="dk" name="readerMac" list="existingReaders" headerKey="-1" headerValue="-- Select Reader --" tabindex="3"></s:select><li>
	</ul>
	
	<div class="clear"></div>
	
	<div id="replacement-container" style="display: none;">
		<h3 class="instruction blueText">2. Select the replacement reader:</h3>
	</div>
	
	<div class="clear"></div>

	<div id="summary" class="lightBackground lightGreyBorder-left">
		<h3 class="blueText">You are about to replace:</h3>
		
		<hr/>
		<h3>Reader: </h3>
		<ul id="existingReader" style="list-style: none; padding: 0; margin: 0;">
			<li>Venue: <span id="erVenue" class="blueText"></span></li>
			<li>Name: <span id="erName" class="label blueText"></span></li>
			<li>Type: <span id="erType" class="label blueText"></span></li>
			<li>IP Address: <span id="erIp" class="label blueText"></span></li>
			<li>MAC Address: <span id="erMac" class="label blueText"></span></li>
		</ul>
		<h3>With:</h3>
		<ul id="replacementReader" style="list-style: none; padding: 0; margin: 0;">
			<li>IP Address: <span id="rrIp" class="label blueText"></span></li>
			<li>MAC Address: <span id="rrMac" class="label blueText"></span></li>
		</ul>
		
		<div id="buttons">
			<input type="button" value="Cancel" id="cancel" class="button" onclick="$('#confirmCancelDialog').dialog('open');"/>
			<input type="button" value="Replace" id="replace" class="button" onclick="replaceClicked()"/>
		</div>
	</div>
	
	<div id="confirmCancelDialog">
		<p>This operation will clear all your selections.</p>
		<p>Do you wish to proceed?</p>
	</div>
	<div id="confirmReaplceDialog">
		<p>Are you sure that you wish to proceed?</p>
	</div>
	<div id="confirmDialog"></div>
</div>

<div id="helpDialog">
<s:if test="canAccessAsset('Deny maintenance role')">
<h1 class="blueText">Replace Reader</h1> 
<h2 class="blueText">Overview</h2>
<p>
On this page you can replace a reader with a new one. Parks, xBRCs, and readers presented on this page are filtered according to the rules listed below.
</p>
<h3 class="blueText">Filtering Rules</h3>
<p>Both current and new readers can be in <span style="color:green">Green</span>, 
<span style="color:GoldenRod">Yellow</span>, or <span style="color:red">Red</span> health status.</p>
<p>You can filter the content of this page by reader health status and only show parks and xBRCs which have at least one reader in the <span style="color:red">Red</span> health status. Checking
the <em>check box</em> at the top of this page will filter the content by reader health status.</p>
<p>Furthermore, xBRCs meeting the following criteria are also excluded:
<ol>
<li>slave xBRCs</li>
<li>xBRCs with empty/unavailable HA (High Availability) Status</li>
<li>xBRCs with HA enabled and either no VIP or VIP starting with a '#', for example: '#nge-alpha-mk.w.d.com'</li>
<li>xBRCs with HA disabled which have HA Status other than solo</li>
<li>xBRCs with HA status of unknown which either don't have a VIP configured or are not reachable by VIP</li>
</ol>
</p>
<h3 class="blueText">Replace a reader</h3>
<p>Select the park and the xBRC to which the faulty reader is currently assigned and then select the faulty reader itself.</p>
<p>Once the reader is selected a table containing all suitable replacement readers will appear. Readers must meet the following requirements to be considered suitable:
<ol>
<li>They must be of the same type as the faulty reader. For example, a touch reader can be used to replace another touch reader, but not a long range reader or a touch reader with an xBio unit.</li>
<li>They must either be assigned to the same xBRC the faulty reader is assigned to and residing in the Unlinked Readers bucket, or not yet be assigned to 
any xBRC and sending HELLO messages directly to the Global xBRMS.</li>
</ol>
</p>
<p>Next, select the new reader from the replacement readers table.</p> 
<p>Once you have made all your choices a summary of the replacement you are about to perform will be displayed on the right
side of the page. Click the <em>Replace</em> button to initiate the replacement or <em>Cancel</em> to clear your selections and start over.</p>
<p>Right before the request is sent to the server you will be asked for a confirmation and will be given a choice to either cancel the request or carry it out.</p>
<p>At the end you will be presented with a dialog either confirming a successful execution or an error message, and the content of the page
will be reloaded in order to pull in the most current state of the system.</p>
<p>If the selected replacement reader is reporting directly to the Global xBRMS that reader will be
removed from the <a href="unassignedreaders" style="color:blue;font-style:italic;white-space:nowrap;" target="_blank">Assign Readers</a> page
after the replacement.</p>
<h3 class="blueText">Clear your choices</h3>
<p>Use the <em>Cancel</em> button to clear all your current choices and start over.</p>
<h3 class="blueText">Refresh the content of this page</h3>
<p>To refresh the content of this page without reloading the page itself click the <img src="images/restart_16.png"/> icon located in the top right corner of this page.
It is a good idea to refresh before performing your selections so that you work with the most current state of the system.</p>
</s:if>
<s:else>
<h1 class="blueText">Replace Reader</h1> 
<h2 class="blueText">Overview</h2>
<p>
On this page you can replace a reader with a new one. Parks, xBRCs, and readers presented on this page are filtered according to the rules listed below.
</p>
<h3 class="blueText">Filtering Rules</h3>
<p>Only current readers in <span style="color:red">Red</span> health status are listed. New readers can be in <span style="color:green">Green</span>, 
<span style="color:GoldenRod">Yellow</span>, or <span style="color:red">Red</span> health status.</p>
<p>You can filter the content of this page by reader health status and only show parks and xBRCs which have at least one reader in the <span style="color:red">Red</span> health status. Checking
the <em>check box</em> at the top of this page will filter the content by reader health status.</p>
<p>Furthermore, xBRCs meeting the following criteria are also excluded:
<ol>
<li>slave xBRCs</li>
<li>xBRCs with empty/unavailable HA (High Availability) Status</li>
<li>xBRCs with HA enabled and either no VIP or VIP starting with a '#', for example: '#nge-alpha-mk.w.d.com'</li>
<li>xBRCs with HA disabled which have HA Status other than solo</li>
<li>xBRCs with HA status of unknown which either don't have a VIP configured or are not reachable by VIP</li>
<li></li>
</ol>
</p>
<h3 class="blueText">Replace a reader</h3>
<p>Select the park and the xBRC to which the faulty reader is currently assigned and then select the faulty reader itself.</p>
<p>Once the reader is selected a table containing all suitable replacement readers will appear. Readers must meet the following requirements to be considered suitable:
<ol>
<li>They must be of the same type as the faulty reader. For example, a touch reader can be used to replace another touch reader, but not a long range reader or a touch reader with an xBio unit.</li>
<li>They must be assigned (sending HELLO messages) to the same xBRC the faulty reader is assigned, but they must not be linked to any location.</li>
</ol>
</p>
<p>Next, select the new reader from the replacement readers table.</p> 
<p>Once you have made all your choices a summary of the replacement you are about to perform will be displayed on the right
side of the page. Click the <em>Replace</em> button to initiate the replacement or <em>Cancel</em> to clear your selections and start over.</p>
<p>Right before the request is sent to the server you will be asked for a confirmation and will be given a choice to either cancel the request or carry it out.</p>
<p>At the end you will be presented with a dialog either confirming a successful execution or an error message, and the content of the page
will be reloaded in order to pull in the most current state of the system.</p>
<h3 class="blueText">Clear your choices</h3>
<p>Use the <em>Cancel</em> button to clear all your current choices and start over.</p>
<h3 class="blueText">Refresh the content of this page</h3>
<p>To refresh the content of this page without reloading the page itself, click the <img src="images/restart_16.png"/> icon located in the top right corner of this page.
It is a good idea to refresh before performing your selections in order to work with the most current state of the system.</p>
<h3 class="blueText">Found readers</h3>
<p>In case a reader you were expecting to use as a replacement reader doesn't appear on this page you might find it on the 
<a href="unassignedreaders" style="color:blue;font-style:italic;white-space:nowrap;" target="_blank">Assign Readers</a> page. If you do, first assign that reader to the xBRC
of the faulty reader. That reader will then be placed in the Unlinked Readers bucket of that xBRC and will become available 
for selection on this page. Make sure to refresh this page at this point so that the change you'd just made is reflected on this page.</p>
</s:else>
</div>
