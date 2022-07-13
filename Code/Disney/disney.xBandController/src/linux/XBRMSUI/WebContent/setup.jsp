<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBackground shadow padding">

<div id="xbrmsSetupForm" class="container round" style="width: 700px;">

<s:if test="status.status.name() != 'Green'">
	<h3><s:property value="status.statusMessage"/></h3>
</s:if>

<dl class="leftJustified firstColumn">
	
	<dt><label>Database Schema Version</label></dt> 
	<dd><s:property value="status.databaseVersion"/></dd>
	
	<dt><label><s:text name="setup.xbrmsDbUrl"/></label></dt> 
	<dd>${xbrmsDbUrl}</dd>
	
	<dt><label><s:text name="setup.xbrmsDbUser"/></label></dt> 
	<dd>${xbrmsDbUser}</dd>	
</dl>
<dl class="leftJustified secondColumn">
	
</dl>

</div>
</div>