<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
     
<div id="idMenu">
	<tiles:insertTemplate template="mainMenu.jsp" />
</div>
<s:if test="problems.size() > 0">
	<span id="xbrmsStatusLinkId">
		<a href="xbrmsstatus">New Status Messages!</a>
	</span>
</s:if>
