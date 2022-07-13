<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="hasActionErrors()">
    <div class="error lightBackground round shadow" id="errorPanelDiv">
    	<a class="closePanelImg" href="#" onclick='$("#errorPanelDiv").hide();'><img src="images/close.gif" alt="Dismiss error message."/></a>
        <s:actionerror/>
    </div>
</s:if>

<s:if test="hasActionMessages()">
    <div class="message lightBackground round shadow" id="messagePanelDiv">
    	<a class="closePanelImg" href="#" onclick='$("#messagePanelDiv").hide();'><img src="images/close.gif" alt="Dismiss message."/></a>
        <s:actionmessage/>
    </div>
</s:if>