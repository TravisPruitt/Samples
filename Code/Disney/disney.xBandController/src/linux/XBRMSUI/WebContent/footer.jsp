<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idFooter">

<%@ page import="com.disney.xband.xbrms.common.model.XbrmsStatusDto" %>
<%@ page import="com.disney.xband.xbrms.common.XbrmsUtils" %>
<% 
XbrmsStatusDto status = null; 
try {
	status = XbrmsUtils.getRestCaller().getStatus();
}
catch(Exception e) {
}
%>

<ul>
	<% if ((status != null) && (status.getName() != null)) { %>
		<li><h2 class="lightText"><%= status.getName() %></h2></li>
	<%}%>
	<% if ((status != null) && (status.getVersion() != null)) { %>
		<li><h3 class="lightText"><%= status.getVersion() %></h3></li>
	<%}%>
	<% if ((status != null) && (status.getId() != null) && !status.getId().isEmpty()) { %>
		<li><h3 class="lightText">(<%= status.getId() %>)</h3></li>
	<%}%>
</ul>

<div class="clear"></div>

</div>
