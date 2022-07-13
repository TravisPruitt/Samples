<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="noXbrcConnection">
	<div class="lightBackground round shadow blueBorder blueText padding topMargin">
		The Scheduled Tasks page is not accessible at this time because the xBRC is not running. Please start the xBRC and refresh this page to proceed.
	</div>
	<br>
</s:if>
<s:else>

<s:include value="fragments/errorAndMessagePanels.jsp"/>

<button onclick="showAddNewItemDialog();">Create New</button> 
<button onclick="window.location.href='schedulerlogs'">View Logs</button> 

<table class="light round dataTable" id="schedulerItems">
<thead>
	<tr><th>Description</th><th>Task Class Name</th><th>Crontab Exp</th><th>Enabled</th><th>Last Run</th><th>Last Status</th><th>Last Status Report</th></tr>
</thead>
<tbody>
<s:iterator var="it" value="items">
	<tr>
		<td><span class="jslink" onclick="showSchedulerItemForm('${item.itemKey}');">${item.description}</span></td>
		<td>${shortJobClassName}</td>
		<td>${item.schedulingExpression}</td>
		<td style="text-align: center;"><s:if test="item.enabled">true</s:if><s:else>false</s:else></td>
		<td>${lastStartDate}</td>
		<td <s:if test="lastTaskSuccess == 'ERROR'">style="color:red;"</s:if> >${lastTaskSuccess}</td>
		<td>${lastTaskTruncatedReport}</td>
<!-- 
		<td class="itemParameters">
			<ul>
			<s:iterator var="param" value="item.parameters">
				<li>${name} : ${value}</li>
			</s:iterator>
			</ul>
		</td>
-->		
	</tr>
</s:iterator>
</tbody>
</table>

<br>

<div id="addNewItemDlg">

	<form id="addNewItemDlgForm">
	<p>
	Select the task you wish to schedule:
	</p>
	<select name="jobClassName" id="jobClassNameSelect">
		<s:iterator var="md" value="metadata">
			<option value="${jobClassName}">${name}</option>
		</s:iterator>
	</select>
	</form>
	
	<p>	
	<button onclick="addNewItem(document.forms.addNewItemDlgForm.jobClassName.value)">OK</button>
	<button onclick="hideAddNewItemDialog();">Cancel</button>
	</p>
</div>

<script type="text/javascript">

$(document).ready(function()
{		
	$("#schedulerItems").dataTable({
		"bPaginate": false,
		"bFilter": true,
		"oLanguage": {
			"sEmptyTable": "There are no scheduled tasks defined.",
			"sSearch": "Filter items",
		},
	});
	
	$("#addNewItemDlg").dialog({height:200,width:320,autoOpen:false});	
});

</script>

</s:else>

