<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="noXbrcConnection">
	<div class="lightBackground round shadow blueBorder blueText padding topMargin">
		The Scheduled Logs page is not accessible at this time because the xBRC is not running. Please start the xBRC and refresh this page to proceed.
	</div>
	<br>
</s:if>
<s:else>

<s:include value="fragments/errorAndMessagePanels.jsp"/>

<table class="light round dataTable" id="schedulerLogsList">
<thead>
	<tr><th>No.</th><th>Task Description</th><th>Task Class Name</th><th>Run Date</th><th>Run Time</th><th>Status</th><th>Status Report</th></tr>
</thead>
<tbody>
<s:iterator var="log" value="logs" status="rowstatus">
	<tr>
		<td>${rowstatus.index +1}</td>
		<td>${log.description}</td>
		<td>${shortJobClassName}</td>
		<td>${startDate}</td>
		<td>${runTime}</td>
		<td <s:if test="log.success == false">style="color:red;"</s:if> ><s:if test="log.success == false">ERROR</s:if><s:else>OK</s:else></td>
		<td>${statusReportTruncated}</td>
	</tr>
</s:iterator>
</tbody>
</table>

<br>

<script type="text/javascript">

$(document).ready(function()
{		
	$("#schedulerLogsList").dataTable({
		"bPaginate": false,
		"bFilter": true,
		"oLanguage": {
			"sEmptyTable": "No log records have been found for the filter specified.",
			"sSearch": "Keyword filter",
		},
	});
	
	hideProgress();
});

</script>

</s:else>

