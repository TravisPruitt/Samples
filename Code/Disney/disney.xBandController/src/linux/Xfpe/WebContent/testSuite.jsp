<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<HTML>
<HEAD>
<link rel="stylesheet" type="text/css" href="css/testResults.css" />
</HEAD>
<H3>Test Suite: ${testSuite.name}</H3>
<H3>Total tests: ${testCount}</H3>
<BODY>
<div>
	<table class="light narrow">
		<thead>
			<tr><th>No.</th><th>Test Id</th><th>Description</th><th>Guest Name</th><th>Band Id</th><th>Expected Result</th><th>Template Size</th></tr>			
		</thead>
		<s:iterator value="testList" var="val" status="row">
			<tr>
				<td><s:property value="#row.index + 1"/></td>
				<td>${id}</td>
				<td>${desc}</td>
				<td>${name}</td>
				<td>${bandId}</td>
				<td>${finalResult}</td>
				<s:url id="testtemplate" action="testtemplate" >
					<s:param name="testId" value="%{id}" />
				</s:url>
				<td><s:a href="%{testtemplate}" target="_new${id}">${fn: length(bioTemplate)} bytes</s:a></td>
			</tr>
		</s:iterator>
	</table>
</div>
</BODY>

</HTML>