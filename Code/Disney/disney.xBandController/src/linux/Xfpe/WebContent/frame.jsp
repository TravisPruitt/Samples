<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:if test="actionTitle != null">
<title><s:text name="actionTitle"/></title>
</s:if>
<s:else>
<title>XBand Administration Console</title>
</s:else>
<sx:head/>
<link rel="stylesheet" type="text/css" href="css/global.css" />
<link rel="stylesheet" type="text/css" href="css/xbreadcrumbs.css" />
<script type="text/javascript" src="script/xbrcui_utils.js"></script>
<script type="text/javascript" src="script/jquery.js"></script>	
<script type="text/javascript" src="script/xbreadcrumbs.js"></script>

<script type="text/javascript">
$(function(){
	$("#menu").xBreadcrumbs(); 	
});
</script>

<tiles:insertAttribute name="pagehead" ignore="true"/>
     
</head>
<body>
	<div id="idMainContainer">
		<div id="idProgress" class="round"><img src="images/ajax-loader.gif" alt="Loading content..."/></div>
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="menu" />
		<s:include value="fragments/errorAndMessagePanels.jsp"/>
		<tiles:insertAttribute name="submenu" ignore="true"/>
		<tiles:insertAttribute name="body" />
		<tiles:insertAttribute name="footer" />
	</div>
</body>
</html>