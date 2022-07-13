<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
<H1>Xfpe Reader Emulator</H1>
<s:a href="managetests">Manage Test Suites</s:a>
<h3>Click on a location below to see readers at that location.</h3>
<ul>
<s:iterator value="locations" var="loc">
	<s:url id="showreaders" action="showreaders" >
	    <s:param name="locationId" value="%{key}" />
	</s:url>
	<li><s:a href="%{showreaders}">${loc.value.location.name}</s:a></li>
</s:iterator>
</ul>
</div>