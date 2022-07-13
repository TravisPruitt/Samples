<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:iterator value="guestStatusIcons" var="gsi">
	<img class="guest" src='images/<s:property value="image"/>' style='top:<s:property value="topPos"/>px; left:<s:property value="leftPos"/>px;' 
		onclick="showGuests(<s:property value="locationId"/>,'<s:property value="state"/>', '<s:property value="xpassonly"/>');" />
</s:iterator>
<s:iterator value="guestStatusCounts" var="gsc">
	<span class="guestCount" style='top:<s:property value="topPos"/>px; left:<s:property value="leftPos"/>px;'><s:property value="count"/></span>
</s:iterator>

<div class="round guestStats">
	<dl class="counts">
		
		<s:if test="totalGuestCount != xpassGuestCount">
		<dt>Total Guests in Queue</dt> 
		<dd>${totalGuestCount}</dd>
		</s:if>
		
		<dt>FastPass Guests in Queue</dt> 
		<dd>${xpassGuestCount}</dd>
		
		<dt>Total FastPass Guests Redeemed</dt> 
		<dd><s:if test="xbrcStatus == null">N/A</s:if><s:else>${xbrcStatus.totalxPassGuestsSinceStart}</s:else></dd>
	</dl>
</div>

<script type="text/javascript">
showtime();
</script>
