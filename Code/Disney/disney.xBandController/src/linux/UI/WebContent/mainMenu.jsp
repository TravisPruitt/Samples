<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="bc" uri="/struts-arianna-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                  
<ul class="jq-menu" style="display: none;">
	<li class="top" id="home"><a href="home" style="font-weight: bold;">Home</a></li>
	<li class="top" id="monitoring"><a href="#" style="cursor:text;">Monitor</a>
		<ul class="shadow">
			<li><a href="attractionview" style="font-weight: bold;">Facility View</a></li>
			<li><a href="showqueue" style="font-weight: bold;">Marching Guests</a></li>
     	</ul>
	</li>
	<li class="top" id="configuration"><a href="#">Configuration</a>
		<ul class="shadow">
			<li><a href="locationlistedit" style="font-weight: bold;">Edit Readers and Locations</a>
				<ul class="shadow">
					<li><a href="newlocation" style="font-weight: bold;">Create Location</a></li>
					<li><a href="newreader" style="font-weight: bold;">Create Reader</a></li>
				</ul>
			</li>
			<li><a href="attractionedit" style="font-weight: bold;">Facility Designer</a></li>
			<li><a href="scheduler" style="font-weight: bold;">Scheduled Tasks</a>
				<ul>
					<li><a href="schedulerlogs" style="font-weight: bold;">Scheduler Logs</a></li>				
				</ul>
			</li>
			<s:if test='model.indexOf("parkentry") >= 0'>
				<li><a href="configureomni" style="font-weight: bold;">Configure Omni</a></li>
			</s:if>
     	</ul>
	</li>
 </ul>