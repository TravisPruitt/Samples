<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

    <h2 class="pageTitle">Welcome to the xConnect Management System</h2>
    
    <ul>
        <s:if test="!isGlobalXbrms()">
            <s:if test="canAccessAsset('Deny maintenance role')">
                <li><a href="systemhealth">System Health</a></li>
                <li><a href="readerhealth">Readers Health</a></li>
                <li><a href="powermanagement">Readers Power Schedule</a></li>
            </s:if>
        </s:if>
        <s:else>
            <s:if test="canAccessAsset('Deny maintenance role')">
                <li><a href="systemhealth">System Health</a></li>
            </s:if>

            <li><a href="unassignedreaders">Assign Readers</a></li>
            <li><a href="replaceReader">Replace Readers</a></li>
        </s:else>

        <s:if test="canAccessAsset('Deny maintenance role')">
            <s:if test="!isGlobalXbrms()">
                <li><a href="editxbrc">xBRC Properties</a></li>
                <li><a href="storedconfig">xBRC Stored Configurations</a></li>
            </s:if>

            <s:if test="isGlobalXbrms()">
                <li><a href="properties">xBRMS Properties</a></li>
            </s:if>
            <s:else>
                <li><a href="properties">xBRMS Properties</a></li>
            </s:else>
            <li><a href="parkssetup2">xBRMS Topology</a></li>
            <li><a href="scheduler">Scheduled Tasks</a></li>
			<li><a href="schedulerlogs">Scheduler Logs</a></li>
        </s:if>
    </ul>
</div>
