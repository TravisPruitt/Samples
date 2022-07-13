<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round shadow padding">

<s:form id="deleteSuiteForm" action="managetests" theme="simple">
	<input type="hidden" name="suiteId"/>
	<input type="hidden" name="action" value="deletesuite"/>
</s:form>

<div class="round  lightBackground  shadow padding">
<H1>Import CSV Test File</H1>
<h3>Add new test cases using CSV files.</h3>
<p>The csv files must contain two fields per row: bandId, template. The template 
may be empty if no template validation is required.</p>
<s:if test="result != null"><h3><span style="color:red"><s:text name="result"/></span></h3></s:if>
<s:fielderror fieldName="csvfilepath"/>
<s:fielderror fieldName="testsuitename"/>

<s:form id="managetestsForm" action="managetests" method="post" enctype="multipart/form-data" theme="simple">
	<s:hidden name="action" value="import"/>
	
	<dl class="leftJustified">
		<dt><label>Test suite name</label></dt> 
		<dd><s:textfield name="testsuitename" /></dd>
		<dt><label>Upload a comma separated csv file with band IDs and BIO templates</label></dt> 
		<s:file id="idAddXmlfile" name="xmlfile"/>
	</dl>
	<br>
	<s:submit/>
</s:form>

</div>
<br>
<div class="round lightBackground  shadow padding">
<h1>Test Suites</h1>
<table id="testSuiteList" class="light round narrow">
	<thead>
		<tr><th>No.</th><th>Suite Name</th><th>Delete</th></tr>
	</thead>	
	<tbody>
		<s:iterator value="testSuiteList" var="val" status="row">
		<tr>
			<td><s:property value="#row.index + 1"/></td>
			<s:url id="testsuite" action="testsuite" >
				<s:param name="suiteId" value="%{id}" />
			</s:url>
			<td><s:a href="%{testsuite}" target="_new${id}">${name}</s:a></td>
			<td class="hcentered">
				<a href="#" onclick='deleteTestSuite("${id}", "${name}");'><img src="images/delete.png"/></a>				
			</td>	
		</tr>
		</s:iterator>
	</tbody>
</table>
</div>

</div>