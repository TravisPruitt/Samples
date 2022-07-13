<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
$("div#idProgress").toggle();
var error = "${error}";
if (error != "")
{
	$("span#dployConfigStatus").html(error);
}
else
{
	$("#deployConfigDialog").dialog('close');
	$("span#dployConfigStatus").html("");
	alert("The configuration has been deployed.");
}

</script>
