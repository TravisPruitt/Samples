<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
<h2 class="pageTitle">${jmslistener.item.name} on ${jmslistener.item.hostname}:${jmslistener.item.port}</h2>
<div id="tabs">	
	<ul>
		<li><a href="#status"><s:text name="tab.status"/></a></li>
	</ul>
	<div id="status">
	<div class="innerTab lightBlueBackground round lightGreyBorder">
		<dl class="leftJustified firstColumn">		
			<dt><label><s:text name="jmslistener.bootTime"/></label></dt> 
			<dd><s:date name=""/>${jmslistener.item.bootTime}</dd>
	
			<dt><label><s:text name="jmslistener.version"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.version}</dd>
	
			<dt><label><s:text name="jmslistener.gffMessagesSinceStart"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.gffMessagesSinceStart}</dd>
	
			<dt><label><s:text name="jmslistener.gxpMessagesSinceStart"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.gxpMessagesSinceStart}</dd>
	
			<dt><label><s:text name="jmslistener.xbmsMessagesSinceStart"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.xbmsMessagesSinceStart}</dd>
	
			<dt><label><s:text name="jmslistener.xbrcMessagesSinceStart"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.xbrcMessagesSinceStart}</dd>
	
			<dt><label><s:text name="jmslistener.cacheSize"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.cacheSize}</dd>
	
			<dt><label><s:text name="jmslistener.cacheCapacity"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.cacheCapacity}</dd>	
			
			<dt><label><s:text name="jmslistener.status"/></label></dt>
			<dd>
            <s:if test="jmslistener.item.status.status.name() == 'Green'">
                <img src="images/green-light.png"/>
            </s:if>
            <s:elseif test="jmslistener.item.status.status.name() == 'Yellow'">
                <img src="images/yellow-light.png"/>
            </s:elseif>
            <s:else>
                <img src="images/red-light.png"/>
            </s:else>
        	</dd>
        	
        	<dt><label><s:text name="jmslistener.statusMessage.label"/></label></dt> 
			<dd><s:text name=""/>${jmslistener.item.status.message}</dd>
		</dl>
	<div class="clear"></div>
	</div>	
	</div>	 
</div>
</div>
