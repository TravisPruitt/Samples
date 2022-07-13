<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="locationId >= 0">
<s:url id="showlocation" action="showlocation" >
	<s:param name="locationId" value="%{locationId}" />
</s:url>
<s:a href="%{showlocation}">View Location</s:a>
</s:if>
			
<table class="light round" style="width:100%;">
	<thead>
	    <tr><th>No.</th><th>Guest Id</th><th>Guest Name</th><th>Car Id</th><th>XPass</th><th>State</th></tr>  
	</thead>	

    <tbody>
    	<s:iterator value="guests" status="row">
	    	<s:url id="xbandview" action="xbandview" >
	    		<s:param name="bandId" value="%{firstBandId}" />
	    		<s:param name="guestId" value="%{gst.id}" />
			</s:url>
			<tr>
				<td><s:property value="#row.index + 1"/></td>
				<td><s:property value="gst.id"/></td>
				<td><s:property value="guest.firstName"/> <s:property value="guest.lastName"/></td>
				<td><s:property value="gst.carId"/></td>
				<td><s:if test="gst.xPass">Yes</s:if><s:else>No</s:else></td>
				<td><s:property value="gst.State"/><s:a onclick="showProgress();" href="%{xbandview}"><img class="zoomin" src="images/zoomin.png"/></s:a></td>
			</tr>
		</s:iterator>
    </tbody>
</table>

<script type="text/javascript">

hideProgress();

$(function()
{	
	<s:if test="location != null">
		$("#showGuestsDialog").dialog('option', 'title', 'Guests last seen at <s:text name="location.name"/>');
	</s:if>
	<s:else>
		$("#showGuestsDialog").dialog('option', 'title', 'Guests in the <s:text name="state"/> state');
	</s:else>
	
	$("#showGuestsDialog").dialog('open');
});
</script>