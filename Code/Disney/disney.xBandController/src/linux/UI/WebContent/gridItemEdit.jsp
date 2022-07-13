<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:fielderror />
<s:form id="gridItemAjaxForm" action="griditemsave" theme="simple" method="post"><div>		
	<s:hidden name="gridItemId"/>
		
	<dl>
		<dt><label><s:text name="gridItem.label"/></label></dt> 
		<dd><s:textfield name="gridItem.label" /></dd>
		
		<dt><label><s:text name="gridItem.description"/></label></dt> 
		<dd><s:textfield name="gridItem.description"/></dd>
		
		<dt><label><s:text name="gridItem.sequence"/></label></dt> 
		<dd>
			<span class="fieldError" id="gridItem_sequence"><s:text name="gridItem.sequence.error"/></span>
			<s:textfield name="gridItem.sequence"/>
		</dd>
		
		<dt><label><s:text name="gridItem.locationId"/></label></dt>
		<dd>			
			<s:select id="xPassOnly" key="gridItem.xpassOnlyOrdinal" list="XpassOnlyStates" listKey="%{ordinal()}" listValue="%{name()}"/>
		</dd>
	</dl>
		
	<fieldset class="group">
		<legend><input type="radio" name="selector" value="location" <s:if test="selector == 'location'">checked</s:if> onclick="document.getElementById('locationId').disabled='';document.getElementById('gridItemState').disabled='disabled';"/><s:text name="useLocation"/></legend>

		<dl>				
			<dt><label><s:text name="gridItem.locationId"/></label></dt>
			<dd>
				<s:if test="selector == 'state'">
					<s:select id="locationId" key="gridItem.locationId" list="locationsMap" disabled="true"/>
				</s:if>
				<s:else>
					<s:select id="locationId" key="gridItem.locationId" list="locationsMap" />	
				</s:else>
			</dd>	
		</dl>
	</fieldset>
	
	<fieldset class="group">
		<legend><input type="radio" name="selector" value="state" <s:if test="selector == 'state'">checked</s:if> onclick="document.getElementById('gridItemState').disabled='';document.getElementById('locationId').disabled='disabled';"/><s:text name="useState"/></legend>

		<dl>				
			<dt><label><s:text name="gridItem.state"/></label></dt>
			<dd>
				<s:if test="selector == 'location'">
					<s:select id="gridItemState" key="gridItem.state" list="guestStatusStates" disabled="true"/>
				</s:if>
				<s:else>
					<s:select id="gridItemState" key="gridItem.state" list="guestStatusStates" />
				</s:else>
			</dd>
		</dl>
	</fieldset>
	
	<div class="centered">
		<input type="button" value="Apply" class="submit" onkeydown="return false;" onclick="saveGridItem(this.form);"/>
	</div>
</div></s:form>
