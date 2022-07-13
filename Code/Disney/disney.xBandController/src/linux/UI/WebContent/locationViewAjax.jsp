<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="readers">
<s:iterator var="reader" value="statusMap" status="statReader">
	<s:if test="#statReader.index % 2 == 0">
	<div class="readerDiv round shadow">	
	<div class="innerGlow round">
	<h3>Guests last seen at reader ${key.readerId}&nbsp;&nbsp;&nbsp;Count: <s:property value="value.size"/></h3>
	<table class="light narow" style="width:100%;">
	<thead>
	    <tr><th>No.</th><th>Guest Id</th><th>Guest Name</th><th>XPass</th><th>State</th><th><th></tr>  
	</thead>
    <tbody>
	<s:iterator var="guest" value="value" status="statGuest">
		<s:url id="xbandview" action="xbandview" >
	    	<s:param name="bandId" value="%{firstBandId}" />
	    	<s:param name="guestId" value="%{gst.id}" />
		</s:url>
		<tr>
			<td><s:property value="#statGuest.index + 1"/></td>
			<td><s:property value="gst.id"/></td>
			<td><s:a href="%{xbandview}"><s:property value="guest.firstName"/> <s:property value="guest.lastName"/></s:a></td>
			<td><s:if test="gst.xPass">Yes</s:if><s:else>No</s:else></td>
			<td><s:property value="gst.State"/></td>
			<td><s:a href="%{xbandview}"><img class="zoomin" src="images/zoomin.png"/></s:a></td>
		</tr>
	</s:iterator>
	</tbody>
	</table>
	</div>
	</div>
	</s:if>
</s:iterator>
</div>

<div class="readers">
<s:iterator var="reader" value="statusMap" status="statReader">
	<s:if test="#statReader.index % 2 == 1">
	<div class="readerDiv round shadow">	
	<div class="innerGlow round">
	<h3>Guests last seen at reader ${key.readerId}&nbsp;&nbsp;&nbsp;Count: <s:property value="value.size"/></h3>
	<table class="light narow" style="width:100%;">
	<thead>
	    <tr><th>No.</th><th>Guest Id</th><th>Guest Name</th><th>XPass</th><th>State</th><th><th></tr>  
	</thead>
    <tbody>
	<s:iterator var="guest" value="value" status="statGuest">
		<s:url id="xbandview" action="xbandview" >
	    	<s:param name="bandId" value="%{firstBandId}" />
	    	<s:param name="guestId" value="%{gst.id}" />
		</s:url>
		<tr>
			<td><s:property value="#statGuest.index + 1"/></td>
			<td><s:property value="gst.id"/></td>
			<td><s:a href="%{xbandview}"><s:property value="guest.firstName"/> <s:property value="guest.lastName"/></s:a></td>
			<td><s:if test="gst.xPass">Yes</s:if><s:else>No</s:else></td>
			<td><s:property value="gst.State"/></td>
			<td><s:a href="%{xbandview}"><img class="zoomin" src="images/zoomin.png"/></s:a></td>
		</tr>
	</s:iterator>
	</tbody>
	</table>
	</div>
	</div>
	</s:if>
</s:iterator>
</div>

<script type="text/javascript">
hideProgress();
</script>

