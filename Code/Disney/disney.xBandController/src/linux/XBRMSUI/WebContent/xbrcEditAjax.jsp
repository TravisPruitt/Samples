<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

var common = new Array();
var userInput = new Array();
var model = "";
var update = "";
var currentTab = "";

<s:if test="common == null">
	// must have lost connection to all xBRCs during this ajax call
	refreshPropertiesPage();
</s:if>
<s:else>
	<%int index = 0;%>
	<s:iterator var="m" value="common" status="modelIndex">
	<s:if test="tabSelected == #modelIndex.index">
		<s:iterator var="cmn" value="value" status="tabIndex">
			common[<%=index%>] = new Array();
			common[<%=index%>][0] = "${cmn.key}";
			common[<%=index%>][1] = "${cmn.value.value}";
			common[<%=index%>][2] = "${cmn.value.type}";
			
			<%index++;%>
		</s:iterator>
		
		currentTab = "${tabSelected}";
		model = "${key}";
		update = "${updateXbrcs}";
	</s:if>
	</s:iterator>
	
	<%int userInputIndex = 0;%>
	<s:iterator var="ui" value="userInput">
		userInput[<%=userInputIndex%>] = "${key}";
	</s:iterator>
	
	setCommon(model, common, update, currentTab, userInput);
</s:else>
</script>