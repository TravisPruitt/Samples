<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBackground shadow">

<div id="findGuestContainer" class="container round">
<h1>Search for a guest by name</h1>
<s:form action="findguest" theme="simple"><div>	
	<dl class="search">
		<dt><label>First Name</label></dt> 
		<dd><s:textfield name="guestFirstName"/><s:fielderror fieldName="guestFirstName"/></dd>
			
		<dt><label>Last Name</label></dt> 
		<dd><s:textfield name="guestLastName"/><s:fielderror fieldName="guestLastName"/></dd>
	</dl>
	<s:submit name="Search"/>
</div></s:form>
</div>

<s:if test="found">
<div id="guestFoundContainer" class="container round">
	<h1>Guest found</h1>
	
	<dl class="guestInfo">
		<dt><label>Name</label></dt> 
		<dd>${gsr.guest.firstName} ${gsr.guest.lastName}</dd>
			
		<dt><label>At Location</label></dt> 
		<dd>${gsr.location.name}</dd>
		
		<dt><label>At Reader</label></dt> 
		<dd>${gsr.reader.readerId}</dd>
		
		<dt><label>Time</label></dt>
		<dd>${gsr.event.timestamp}</dd>
	</dl>
</div>
</s:if>
<s:else>
	<s:if test="guestLastName != '' && guestLastName != null">
	<div id="guestFoundContainer" class="container round">
		<s:if test="gsr == null || gsr.guest == null">
			<h3>Could not find any guests matching first name ${guestFirstName} and last name ${guestLastName}</h3>
		</s:if>
		<s:else>
			<h1>${guestFirstName} ${guestLastName} is not at ${gsr.currentAttractionName}</h1>
		</s:else>
	</div>
	</s:if>
</s:else>

</div>