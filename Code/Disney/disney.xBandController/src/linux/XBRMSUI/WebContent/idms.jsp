<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
<h2 class="pageTitel">${idms.item.name} on ${idms.item.hostname}:${idms.item.port}</h2>
<div id="tabs">
	<ul>
		<li><a href="#status"><s:text name="tab.status"/></a></li>
	</ul>

	<div id="status">
	<div class="innerTab lightBlueBackground round lightGreyBorder">
		<dl class="leftJustified firstColumn">
		<dt><label><s:text name="idms.readOnly"/></label></dt>
		<dd><s:text name=""/>${idms.item.readOnly}</dd>
		<dt><label><s:text name="idms.databaseURL"/></label></dt>
		<dd><s:text name=""/>${idms.item.databaseURL}</dd>
		<dt><label><s:text name="idms.databaseUserName"/></label></dt>
		<dd><s:text name=""/>${idms.item.databaseUserName}</dd>
		<dt><label><s:text name="idms.databaseVersion"/></label></dt>
		<dd><s:text name=""/>${idms.item.databaseVersion}</dd>
		<dt><label><s:text name="idms.status"/></label></dt>
		<dd>
            <s:if test="idms.item.status.status.name() == 'Green'">
                <img src="images/green-light.png"/>
            </s:if>
            <s:elseif test="idms.item.status.status.name() == 'Yellow'">
                <img src="images/yellow-light.png"/>
            </s:elseif>
            <s:else>
                <img src="images/red-light.png"/>
            </s:else>
        </dd>
		
		<dt><label><s:text name="idms.statusMessage"/></label></dt> 
		<dd><s:text name=""/>${idms.item.status.message}</dd>		
		</dl>
	<div class="clear"></div>
	</div>
	</div>
</div>
</div>
