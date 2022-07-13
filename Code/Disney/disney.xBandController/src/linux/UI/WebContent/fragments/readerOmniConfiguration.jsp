<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="parkEntry">
<div id="configureOmnis">
	<h2>Omni Server(s) associated with this reader</h2>
	<ul class="sortable" id="omniServers" style="display: none">
	</ul>
	<button onclick='openAddOmniServerDilog(); return false;' class="blueText">Associate an Omni Server</button> 
	<h3><a href="configureomni">Configure Omni Servers</a></h3>
</div>
</s:if>