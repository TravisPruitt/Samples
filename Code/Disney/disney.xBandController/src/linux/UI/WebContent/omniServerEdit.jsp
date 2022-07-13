<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round">

<div class="round lightBlueBackground shadow padding">

<h2 class="pageTitle"><s:text name="page.title"/></h2>

<table class="light round dataTable" id="omniServers">
<thead><tr>
	<th>Server</th>
	<th>Port</th>
	<th>Description</th>
	<th>Active</th>
	<th>Reason</th>
	<th>Readers</th>
	<th>Actions</th>
</tr></thead>
<tbody>
	<s:iterator value="omniServers">
	<tr class="centered">
		<td class="break-word">${key.hostname}</td>
		<td style="width: 40px">${key.port}</td>
		<td>${key.description}</td>
		<td style="width: 40px">${key.active}</td>
		<td>${key.notActiveReason}</td>
		<td style="width: 40px">${value}</td>
		<td style="width: 100px">
			<a id="menu-${key.id}" style="background: none; border: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all">Actions <span class="ui-icon ui-icon-triangle-1-s"/></a>
			<div id="content-${key.id}" class="hidden">
			<ul>
				<li><a href="#" onclick="deleteOmniServer('${key.id}','${key.hostname}')">
				<img class="fg-menu-icon" src="images/remove.png" title="Delete Omni Server."/>Delete</a>
				</li>
				<li><s:a action="locationlistedit">
				<img class="fg-menu-icon" src="images/settings16.png" title="Edit Omni Server's Configuration."/>Add to Readers</s:a>
				</li>
			</ul>
			</div>
		</td>
	</tr>
	</s:iterator>
</tbody>
</table>

<div class="clear"></div>
<button onclick='openAddOmniServerDilog(); return false;' class="blueText">Add Server</button> 

<s:form action="omniserverdelete" id="idOmniServerDeleteForm" theme="simple"><div>
	<s:hidden name="omniServer.id"/>
</div></s:form>

<!-- add omni begins -->
<div id="add-omni-dialog" style="display: none;">
<s:form action="omniserveradd" id="idOmniServerAddForm" theme="simple"><div>
	<s:hidden name="omniServer.id"/>

	<dl class="firstColumn">
		<dt><label><s:text name="omniServer.hostname"/></label> <span class="error" style="display: none;color:red" id="error_omniServer_hostname"><s:text name="error.omniServer.hostname"/></span></dt>
		<dd><s:textfield name="omniServer.hostname" id="omniServer_hostname" maxlength="255"/></dd>
		<dt><label><s:text name="omniServer.description"/></label> <span class="error" style="display: none;color:red" id="error_omniServer_description"><s:text name="error.omniServer.description"/></span></dt>
		<dd><s:textarea name="omniServer.description" id="omniServer_description" maxlength="1024" cols="20" rows="5"/></dd>
		<dt><label><s:text name="omniServer.active"/></label></dt>
		<dd><s:checkbox name="omniServer.active" id="omniServer_active"/></dd>
	</dl>
	<dl class="secondColumn">
		<dt><label><s:text name="omniServer.port"/></label> <span class="error" style="display: none;color:red" id="error_omniServer_port"><s:text name="error.omniServer.port"/></span></dt>
		<dd><s:textfield name="omniServer.port"  id="omniServer_port" maxlength="10"/></dd>
		<dt><label><s:text name="omniServer.notActiveReason"/></label> <span class="error" style="display: none;color:red" id="error_omniServer_notActiveReason"><s:text name="error.omniServer.notActiveReason"/></span></dt>
		<dd><s:textarea name="omniServer.notActiveReason" id="omniServer_notActiveReason" maxlength="1024" cols="20" rows="5"/></dd>
	</dl>
	
	<div class="clear"></div>
	<input type="button" value="Add Omni Server" onclick="addOmniServer()"/>
</div></s:form>
</div>
<!-- add omni ends -->

</div></div>
