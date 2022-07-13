<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idHeader">
<div id="disneyLogo">
	<img id="idLogo" src="images/disneyLogo_145_34.png"/>
	<img id="idNgeLogo" src="images/xconnect-wht-sm-logo.png" alt="Disney xConnect"/>
</div>
<s:include value="fragments/clock.jsp"/>
<div id="idProductTitle">
	<h1 class="whiteText">xConnect Management System</h1>
	<div>
		<h3>${productTitle}</h3>
		<div id="idUser">
		<% if ((request.getUserPrincipal() != null) && (request.getUserPrincipal().getName() != null)) { %>
			<h3>Welcome <%= request.getUserPrincipal().getName() %>&nbsp;&nbsp;&nbsp;</h3>
		<%}%>
		<% if (request.getUserPrincipal() != null) { %>
			<h3><a class="whiteText" href="logout.action">Logout</a></h3>
		<%}%>
		</div>
	</div>
</div>
</div>
