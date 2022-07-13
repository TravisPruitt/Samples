<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody">

<div id="editContainer" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle">Scheduler Logs</h2>

<div id="logsFilterDiv" class="round">
<s:form theme="simple" id="schedulerLogsForm">	
	<s:hidden name="jobClassName" />
	
	<select name="itemKey">
		<option selected value="">Show logs for all tasks</option>
		<s:iterator var="it" value="items">			
			<option value="${item.itemKey}">${item.description}</option>	
		</s:iterator>
	</select>
	
	
	<label>Show Days</label>
	<s:textfield name="days" maxlength="4" size="4"/>
	
	<input class="right" type="button" value="Apply Filter" onclick="applyLogsFilter()"/>
	
</s:form>
<div class="clear"></div>
</div>

<sx:div 
	theme="ajax" 
	href="schedulerlogsajax" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="true"
	listenTopics="refreshschedulerlogs"
	formId="schedulerLogsForm">
</sx:div>
	
</div>
</div>
