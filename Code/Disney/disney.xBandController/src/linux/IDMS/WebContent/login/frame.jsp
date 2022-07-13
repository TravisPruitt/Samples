<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>xConnect Authentication Gateway</title>
<head/>
<link rel="stylesheet" type="text/css" href="../css/global.css" />
<link rel="stylesheet" type="text/css" href="../css/header.css" />
<link rel="stylesheet" type="text/css" href="../css/footer.css" />

<script type="text/javascript">

var Debugger = function() {};
Debugger.log = function(message) {
	try {
		console.log(message);
	} catch (exception ) {
		return;
	}
};
</script>

<tiles:insertAttribute name="pagehead" ignore="true"/>
     
</head>
<body>
	<div id="idMainContainer">
		<div id="idProgress" class="round"><img src="ajax-loader.gif"/></div>
		<tiles:insertAttribute name="header" />
                <tiles:insertAttribute name="menu" />
                <s:include value="errorAndMessagePanels.jsp"/>
                <tiles:insertAttribute name="submenu" ignore="true"/>
                <tiles:insertAttribute name="body" />
                <tiles:insertAttribute name="footer" />
	</div>
</body>
</html>
