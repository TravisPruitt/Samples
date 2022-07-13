<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<HTML>
<HEAD>
<link rel="stylesheet" type="text/css" href="css/testResults.css" />
</HEAD>
<H2>Test Results</H2>
<H3>Test Suite: ${lastTestSuite.name}</H3>
<H3>Overall Result: <s:if test="success"><span class="success">SUCCESS</span>. All ${testCount} tests passed</s:if><s:else><span class="fail">FAIL</span>. Total tests: ${testCount} , ${failedCount} failed, ${successCount} passed</s:else></H3>
<BODY>
<div>
	<table class="light narrow">
		<thead>
			<tr><th>No.</th><th>Test Id</th><th>Description</th><th>Guest Name</th><th>Band Id</th><th>Expected Result</th><th>Actual</th><th>Success</th></tr>			
		</thead>
		<s:iterator value="lastTestResults" status="row">
			<tr>
				<td><s:property value="#row.index + 1"/></td>
				<td>${test.id}</td>
				<td>${test.desc}</td>
				<td>${test.name}</td>
				<td>${test.bandId}</td>
				<td>${test.finalResult}</td>
				<td>${actualResult}</td>
				<s:if test="success"><td class="success">OK</td></s:if><s:else><td class="fail">FAIL</td></s:else>
			</tr>
		</s:iterator>
	</table>
</div>
</BODY>

</HTML>