<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idSubmenu" class="round">
	<img src="images/play.png" onclick="dojo.event.topic.publish('/startTimer')"/>
	<div class="menuSeparator"></div>
	<img src="images/pause.png" onclick="dojo.event.topic.publish('/stopTimer')"/>
</div>