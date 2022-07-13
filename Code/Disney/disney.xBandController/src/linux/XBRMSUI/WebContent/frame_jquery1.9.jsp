<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<link rel="stylesheet" type="text/css" href="script/new/legacy-jquery-custom-blue/css/legacy-custom-blue-theme/jquery-ui-1.9.2.custom.min.css" />
<link rel="stylesheet" type="text/css" href="css/global.css" />
<link rel="stylesheet" type="text/css" href="css/header.css" />
<link rel="stylesheet" type="text/css" href="css/footer.css" />
<link rel="stylesheet" type="text/css" href="css/new/jqsimplemenu.css" />
<script type="text/javascript" src="script/new/legacy-jquery-custom-blue/js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="script/new/legacy-jquery-custom-blue/js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" src="script/new/jqsimplemenu.js"></script>
<script type="text/javascript" src="script/ErrorDialog.js"></script>

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
<title>xConnect Management System <% if ((status != null) && (status.getId()) != null && !status.getId().trim().isEmpty()) { %>
		- <%=status.getId()%>
	<%}%>
</title>

<script type="text/javascript">
$(function(){
	//////drop down menu related /////// 
	var menu = $('.jq-menu');
	
	// decorate the list with jq simple menu features
	menu.jqsimplemenu();
	$(menu).show();
	
	// mark the appropriate top menu item as active
	var active = $.trim($('div#active').text());
	if (active.length > 0)
	{
		var activeMenu = $('li#' + active, menu);
		if (activeMenu.length > 0)
		{
			activeMenu.addClass("active-menu");
		}
	}
	
	///// help dialog /////
	$infoDialog = $("#infoDialog");
	if ($infoDialog.length > 0)
	{
		$infoDialog.dialog({
			minHeight:100,
			minWidth:200,
			autoOpen:false,
			draggable: true,
			closeOnEscape: true,
			modal: false,
			show: "medium"
		});
	}
	
	buildHelpDialog("helpDialog", "helpLink");
	
	ErrorDialog.init();
});

var Debugger = function() {};
Debugger.log = function(message) {
	try {
		console.log(message);
	} catch (exception ) {
		return;
	}
};

function openHelpDialog() {
	$helpDialog.dialog('open');
}

function buildHelpDialog(dialogId, iconId) {
	$helpDialog = $("#"+dialogId);
	if ($helpDialog.length > 0)
	{
		$helpDialog.dialog({
			height:400,
			width:400,
			autoOpen:false,
			draggable: true,
			closeOnEscape: true,
			modal: false,
			show: "medium",
			title: "Help",
			open: function (){
				$(this).scrollTop(0);
			}
		});
		$("#" + iconId).show();
	} else {
		$("#" + iconId).hide();
	}
} 

function openInfoDialog(element)
{
	if ($infoDialog.dialog("isOpen"))
		$infoDialog.dialog("close");
	
	if (element == "undefined")
		return;
	
	var message = element.attr("title");
	if (message === ""){
		message = "<s:text name='title.no.description'/>";
	}
	
	if ($infoDialog.length == 0)
		return;
	
	$infoDialog.html(message);
	$infoDialog.dialog('open');
}
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
		<div id="active" style="display: none;"><tiles:getAsString name="active-menu" ignore="true"/></div>
	</div>
</body>
</html>
