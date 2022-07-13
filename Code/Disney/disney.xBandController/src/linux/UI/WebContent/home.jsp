<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style type="text/css">
ul.home li a { text-decoration: none; }
</style>

<div id="idBody" class="round lightBlueBackground shadow padding">
<h2 class="pageTitle"><s:text name="page.title"/></h2>
<ul class="home">
<li><a href="locationlistedit"><s:text name="locationlistedit"/></a></li>
<li><a href="showqueue"><s:text name="showqueue"/></a></li>
<li><a href="attractionview"><s:text name="attractionview"/></a></li>
<li><a href="attractionedit"><s:text name="attractionedit"/></a></li>
<s:if test='model.indexOf("parkentry") >= 0'>
	<li><a href="configureomni"><s:text name="configureomni"/></a></li>
</s:if>
<li><a href="scheduler">Scheduled Tasks</a></li>
<li><a href="schedulerlogs">Scheduler Logs</a></li>
</ul>
</div>
