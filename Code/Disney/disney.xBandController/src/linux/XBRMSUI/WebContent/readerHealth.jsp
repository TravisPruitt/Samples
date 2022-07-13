<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
	<h2 class="pageTitle"><s:text name="page.title"/></h2>
	<img class="clickable" style="position: absolute; top:1em; right:1em; z-index: 1000;" title="Refresh the content of this page without reloading it." onclick="refreshStatus()" src="images/restart_16.png">
	<div class="inner-menu">
	    <div class="view-toggle round lightGreyBorder lightBlueBackground">
	    	<div id="gridView" class="lightGreyBorder-right">
	    		<div class="inner-view-toggle bold centered" onclick='switchView("SMALL_GRID")'>Grid</div>
	    	</div>
	    	<div id="listView">
	    		<div class="inner-view-toggle bold centered" onclick='switchView("LIST")'>List</div>
	    	</div>
	    </div>
    </div>
    <div class="clear"></div>

	<s:if test="viewType == 'SMALL_GRID'">
		<s:include value="readerHealth-smallGridView.jsp"/>
	</s:if>
	<s:else>
		<s:include value="readerHealth-listView.jsp"/>
	</s:else>
    
    <div style="height: 50px">
        <dl class="horisontalLegend" style="float:left; clear: right; ">
            <dt><img src="images/green-light.png"/></dt>
            <dd><s:text name="legend.light.green"/></dd>
            <dt><img src="images/yellow-light.png"/></dt>
            <dd><s:text name="legend.light.yellow"/></dd>
            <dt><img src="images/red-light.png"/></dt>
            <dd><s:text name="legend.light.red"/></dd>
        </dl>
    </div>

    <!-- periodically pull xbrc health status -->
   <sx:div
        theme="ajax"
        href="readerlisthealthajax"
        executeScripts="true"
        showLoadingText="false"
        showErrorTransportText="true"
        errorNotifyTopics="/accessDenied"
        errorText="Your session has expired"
        listenTopics="/refresh,checkAccess"
        startTimerListenTopics="/startTimer"
        stopTimerListenTopics="/stopTimer"
        updateFreq="120000"
        preload="false">
    </sx:div>

    <sx:div
    	id="refreshStatusDiv"
    	theme="ajax"
     	href="readerhealthrefresh"
    	executeScripts="false"
     	preload="false"
    	showLoadingText="false"
    	showErrorTransportText="false"
   		listenTopics="refreshStatus">
    </sx:div>
    
    <s:form action="identifyreader-readerhealth" id="idReaderIdentifyForm" theme="simple">
		<s:hidden name="xbrcId"/>
		<s:hidden name="readerId"/> 
	</s:form>
	<sx:div 
		theme="ajax" 
		href="identifyreader-readerhealth" 
		executeScripts="false"
		showLoadingText="false"
		showErrorTransportText="false"
		listenTopics="handleIdentifyReader"
		formId="idReaderIdentifyForm"
		preload="false" >
	</sx:div>
	
	<s:form action="restartreader-readerhealth" id="idReaderRestartForm" theme="simple"><div>
		<s:hidden name="xbrcId"/>
		<s:hidden name="readerId"/> 
	</div></s:form>
	<sx:div 
		theme="ajax" 
		href="restartreader-readerhealth" 
		executeScripts="false"
		showLoadingText="false"
		showErrorTransportText="false"
		listenTopics="handleRestartReader"
		formId="idReaderRestartForm"
		preload="false" >
	</sx:div>
	
	<s:form action="rebootreader-readerhealth" id="idReaderRebootForm" theme="simple"><div>
		<s:hidden name="xbrcId"/>
		<s:hidden name="readerId"/> 
	</div></s:form>
	<sx:div 
		theme="ajax" 
		href="rebootreader-readerhealth" 
		executeScripts="false"
		showLoadingText="false"
		showErrorTransportText="false"
		listenTopics="handleRebootReader"
		formId="idReaderRebootForm"
		preload="false" >
	</sx:div>

</div>
<div class="clear"></div>
