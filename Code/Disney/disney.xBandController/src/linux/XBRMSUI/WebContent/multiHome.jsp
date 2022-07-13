<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
	
	<h2 class="pageTitle">Welcome to the xConnect Management System</h2>
	
    <s:if test="isMultiParkConfig()">
        <h3 class="blueText">Choose xBRMS server</h3>
        <ul>
            <s:iterator var="link" value="servers.entrySet()">
                <li>
                    <a href="${link.key}">${link.value}</a>
                </li>
            </s:iterator>
        </ul>
    </s:if>
    <s:if test="canAccessAsset('Deny maintenance role')">
        <div style="margin-top: 20px;">
            <h3 class="blueText">Configure xBRMS server</h3>
            <ul>
                <li><a href="parkssetup">xBRMS Topology</a></li>
            </ul>
        </div>
    </s:if>
</div>
