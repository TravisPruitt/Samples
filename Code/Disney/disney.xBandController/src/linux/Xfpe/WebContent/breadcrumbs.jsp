<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="bc" uri="/struts-arianna-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                  
<ul id="menu" class="xbreadcrumbs">
<bc:breadcrumbs var='c' status='s'>
    <!-- use the usual way to build the url for a struts action -->
    <s:url var="surl" action="%{action}" namespace="%{namespace}" includeContext="false"/>
                
    <!-- use the tags: url and param to effectively build the url -->
    <c:url var="url" value="${surl}">
        <!-- iterate on parameters -->             
        <c:forEach var="p" items="${c.params}">
            <!-- iterate on parameter values -->             
            <c:forEach var="v" items="${p.value}">${v}
                <c:param name="${p.key}" value="${v}"/>
            </c:forEach>
        </c:forEach>
    </c:url>

    <!-- retrieve the url built and render an HTML anchor -->
    <li class="${cssClass}">
    	<s:if test='action.startsWith("home")'>
    		<s:a cssClass="home" href="%{#attr['url']}">${c.name}</s:a>
   		<!-- 
    		<ul>
               <li><a href="showreaders">Show Readers</a></li>
          	</ul>
         -->
    	</s:if>
    	<s:else>
    		<s:a href="%{#attr['url']}">${c.name}</s:a>
    	</s:else>    	
    </li>
</bc:breadcrumbs>
</ul>