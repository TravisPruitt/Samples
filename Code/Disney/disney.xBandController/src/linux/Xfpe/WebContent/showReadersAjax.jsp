<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
var running = 0;
<s:iterator value="readers">
	setReaderLight('${readerId}', '${currentLight}', ${fpLightOn});
	setOmniStatus('${readerId}', ${connected}, ${loggedIn});
	<s:if test="currentTest != null && currentTest.test != null">
		setCurrentTestDesc('${readerId}', '${currentTest.desc}','${currentTest.currentAction}');
		running++;
	</s:if>
	<s:else>
		setCurrentTestDesc('${readerId}', '','');		
	</s:else>
</s:iterator>

if (running == 0)
{
	<s:if test="testRunning == false">
		allTestHaveFinished(${locationId});
	</s:if>
}
</script>
