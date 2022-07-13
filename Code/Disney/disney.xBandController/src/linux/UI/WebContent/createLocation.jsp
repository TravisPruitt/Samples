<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
<h2 class="pageTitle"><s:text name="page.title"/></h2>

<s:form action="newlocationsave" id="idLocationSaveForm" theme="simple"><div>
	<dl class="leftJustified">
		<dt><label><s:text name="location.name"/></label> <span class="fieldError" id="location_name"><s:text name="location.locationName.required"/></span></dt> 
		<dd><s:textfield name="location.name" maxlength="32" cssStyle="width:50%"/></dd>
		
		<dt><label><s:text name="location.locationTypeId"/></label></dt>
		<dd>
			<s:select key="location.locationTypeId" list="locationTypes" listKey="%{key}" listValue="%{value}"></s:select>
		</dd>
	</dl>
	<input type="button" value="Save Location" onkeydown="return false;" onclick="saveLocation(this.form);" class="blueText"/>
</div></s:form>

</div>