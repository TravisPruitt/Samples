<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle">Power Schedule</h2>

<s:if test="inventory.size() > 0">
<table class="light round" id="xbrcs">
    <thead>
        <tr>
            <th>Name</th>
            <th>Scheduled Start</th>
            <th>Scheduled Stop</th>
            <th>Operation</th>
        </tr>
    </thead>
    <tbody>

    <s:iterator var="xbrc" value="inventory" status="xbrcStat">
    <tr class="captionRow">
        <td>${xbrc.name}</td>
        <td>${xbrc.openTime}</td>
        <td>${xbrc.closeTime}</td>
        <td><a id="menu-${xbrc.id}" style="background: none; border: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#"><span class="ui-icon ui-icon-triangle-1-s"></span>Schedule Override</a>
       <div id="content-${xbrc.id}" class="hidden">
       <ul>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 0)">Clear override</a>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 1)">Override 1 hour</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 2)">Override 2 hours</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 3)">Override 3 hours</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 4)">Override 4 hours</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 5)">Override 5 hours</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 6)">Override 6 hours</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 7)">Override 7 hours</a>
           </li>
           <li><a href="#raction_${xbrc.id}"
                   onclick="overrideXbrcSchedule(this, ${xbrc.id}, 8)">Override 8 hours</a>
           </li>
       </ul>
       </div>
        </td>
    </tr>
     </s:iterator>
    </tbody>
</table>
</s:if>
<s:else>
<h3 class="blueText">No xBRCs with an xBR powered by a battery were located.</h3>
</s:else>

<s:form action="overrideschedule-powermanagement" id="idXbrcOverrideScheduleForm" theme="simple"><div>
    <s:hidden name="xbrcId"/>
    <s:hidden name="hours"/>
</div></s:form>
<sx:div
    theme="ajax"
    href="overrideschedule-powermanagement" 
    executeScripts="false"
    showLoadingText="false"
    showErrorTransportText="false"
    listenTopics="handleOverrideSchedule"
    formId="idXbrcOverrideScheduleForm"
    preload="false" >
  </sx:div>
</div>

