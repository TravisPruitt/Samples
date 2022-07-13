<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<!-- park entry specific -->
<s:if test="parkEntry">
<!-- add omni begins -->
<div id="add-omni-dialog" style="display: none;">
	<dl class="leftJustified firstColumn">
		<dt><label><s:text name="Select Omni Server to Add"/></label></dt> 
		<dd>
			<select id="selectOmni" style="width:45%;">
				<s:iterator value="omnisForGrabs">
					<option value="${id}" id="selectOmni_${id}">${hostname}</option>
				</s:iterator>
			</select>
		</dd>
	</dl>
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="Specify Priority"/></label> <span class="fieldError" id="omni_priority"><s:text name="omni.priority.missing"/></span></dt>
		<dd>
			<select id="omniPriority" style="width:45%;">
				<s:iterator value="omnisForGrabs" status="index">
					<option value='<s:property value="#index.index"/>' id="omniPriority_<s:property value="#index.index"/>"><s:property value="#index.index + 1"/></option>
				</s:iterator>
			</select>
		</dd>
	</dl>
	<div class="clear"></div>
	<input type="button" value="Add Omni Server" onclick="addOmniServer()"/>
</div>
<!-- add omni ends -->
</s:if>

<!-- park entry specific ends -->