<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<script src="script/xbrcui_utils.js" type="text/javascript"></script>

<div id="idBody" class="round lightBackground shadow">
<div id="readersContainer">
<h1>Readers at ${location.location.name}</h1>
<s:if test="testRunning">
	<button id="stopTestButton" onclick='stopTest("${location.location.id}");'>Stop Simulation</button>
</s:if>
<s:else>
	<button onclick='startTest("${location.location.id}", document.getElementById("testSuite").value, document.getElementById("maxReaders").value);'>Start Simulation</button><s:select id="testSuite" list="testSuiteList" name="suiteId" listValue="name" listKey="id"/>
	<span>Run test using: <s:textfield id="maxReaders" name="maxReaders"/> Readers</span>
	<s:if test="lastTestResults != null">
	<button onclick="window.open('testresults','testresults')">Test Results</button>
	</s:if>
</s:else>
<br>
<s:iterator value="readers">
	<div class="xfpeReaderContainer round">
		<div class="title round">${readerId}</div>
		<div class="toolbox">
			<label>Test</label> <span id="idCurrentTest_${readerId}"></span><br>
			<label>Action</label> <span id="idCurrentAction_${readerId}"></span><br>
			<button class="tapButton" type="button" value="" onClick="tapReader('${readerId}', 'tap','378d9396759a2b59', '');">Tap</button>
			<button class="tapButton" type="button" value="" onClick="scanReader('${readerId}', 'scan','', 'BIO378d9396759a2b59');">Scan</button>
		</div>
		<div id="id_${readerId}" class="xfpeReader">
			<img class="xfpeLight" id="id_${readerId}_blue" src="images/xfpe/XfpeBlueLight.png"/>
			<img class="xfpeLight" id="id_${readerId}_green" src="images/xfpe/XfpeGreenLight.png"/>
			<img class="fpLight" id="id_${readerId}_fpwhite" src="images/xfpe/FocalPointWhiteLight.png"/>
			
			<div class="omniStatus round">
				<b>Omni Status</b>
				<p><span id="idOmniStatus_${readerId}"></span></p>
				<button class="logoutButton" id="idLogoutButton_${readerId}" type="button" value="" onClick="logoutCastMember('${readerId}');">Logout</button>
			</div>
		</div>
	</div>
</s:iterator>
<div class="clear"></div>
</div>
</div>

<s:form theme="simple" id="readerEventForm" action="readerevent">
	<s:hidden name="readerId"/>
	<s:hidden name="action"/>
	<s:hidden name="bandId"/>
	<s:hidden name="bioData"/>
</s:form>

<sx:div
	id="readerEventDiv"
	theme="ajax"
	href="readerevent"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleReaderEvent" 
	formId="readerEventForm" >
</sx:div>

<s:url id="showreadersajax" action="showreadersajax" >
	<s:param name="locationId" value="%{location.location.id}" />
</s:url>

<sx:div
	id="showreadersajax"
	theme="ajax"
	href="%{showreadersajax}"
	executeScripts="true"
	preload="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="none" 
	updateFreq="1000">
</sx:div>