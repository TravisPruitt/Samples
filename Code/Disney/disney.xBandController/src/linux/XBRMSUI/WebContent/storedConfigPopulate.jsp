<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
document.forms.addConfigForm.name.value="${conf.name}";
document.forms.addConfigForm.venueCode.value="${conf.venueCode}";
document.forms.addConfigForm.venueName.value="${conf.venueName}";
var error = "${error}";
if (error != "")
{
	alert(error);
}
$("div#getXbrcConfigStatus").html("");
</script>
