<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle"><s:text name="page.title"/></h2>

<s:form id="removeConfigForm" action="storedconfig" theme="simple">
	<input type="hidden" name="id"/>
	<input type="hidden" name="action" value="delete"/>
</s:form>

<s:form id="getXbrcConfigForm" action="getxbrcconfig" theme="simple">
	<input type="hidden" name="id"/>
</s:form>

<s:form id="downloadConfigForm" action="downloadconfig" theme="simple" target="_blank">
	<input type="hidden" name="id"/>
</s:form>
				
<table id="configList" class="light round">
	<thead>
		<tr><th>Model</th><th>Venue ID</th><th>Venue Name</th><th>Configuration Name</th><th>Description</th><th>Created On</th>
		<th>Download</th><th>Deploy</th><th>Remove</th></tr>
	</thead>	
	<tbody>
		<s:iterator value="inventory" var="val">
		<tr id="inv_tr_${conf.id}">
			<td>${model}</td>
			<td>${conf.venueCode}</td>
			<td>${venueName}</td>
			<td>${name}</td>
			<td>${description}</td>
			<td>${createTime}</td>
			<td>
				<a href="#" onclick='downloadConfig("${conf.id}");'><img src="images/download.png"/></a>				
			</td>		
			<td>
				<a href="#" onclick='showDeployConfigDialog("${conf.id}", "${venueName}", "${name}", "${description}", "${model}");'><img src="images/deploy.png"/></a>				
			</td>
			<td>
				<a href="#" onclick='removeConfigItem("${conf.id}", "${venueName}", "${name}");'><img src="images/delete.png"/></a>				
			</td>
		</tr>
		</s:iterator>
	</tbody>
</table>

<div style="height: 50px">
<button type="button" class="submit" style="float: right; margin: 1em 5px 0 0" onclick="showAddConfigDialog();">Add New</button>
</div>

<!-- dialog to add a new configuration -->
<div id="addConfigDialog">
	<s:form id="addConfigForm" action="storedconfig" method="post" enctype="multipart/form-data" theme="simple"><div>
	<p>You may upload a new configuration file from your local computer or get one from a running xBRC.</p>
	
	<dl class="leftJustified">
		<dt><label>*<s:text name="conf.name"/></label></dt> 
		<dd><s:textfield id="idAddName" required="true" name="name"/><s:fielderror fieldName="name"/></dd>
		
		<dt><label>*<s:text name="conf.description"/></label></dt> 
		<dd><s:textfield id="idAddDescription" required="true" name="description"/><s:fielderror fieldName="description"/></dd>
		
		<dt><label>*<s:text name="conf.venueCode"/></label></dt> 
		<dd><s:textfield id="idAddVenueCode" required="true" name="venueCode" /><s:fielderror fieldName="venueCode"/></dd>
		
		<dt><label>*<s:text name="conf.venueName"/></label></dt> 
		<dd><s:textfield id="idAddVenueName" required="true" name="venueName"/><s:fielderror fieldName="venueName"/></dd>		
					
	</dl>
	
	<fieldset class="group">
		<legend><input type="radio" name="action" value="uploadFromFile" checked onclick="toogleUploadElements(false);"/><s:text name="conf.fromFile"/></legend> 
		<s:file id="idAddXmlfile" name="xmlfile" accept="text/xml"/>
	</fieldset>
	
	<fieldset class="group">
		<legend><input type="radio" name="action" value="uploadFromXbrc" onclick="toogleUploadElements(true);"/><s:text name="conf.fromXbrc"/></legend>					
		<s:select cssStyle="max-width: 290px;" name="xbrcId" listKey="id" listValue="vipDisplayName" id="idUploadXbrcList" list="xbrcList" disabled="true"/>
		<a id="idRefreshUploadXbrcList" style="position: relative; display: none;" href="#" onclick='populateFromXbrcConfig();'><img style="position: absolute; top: 0px; left: 2px;" title="Populate form fields from xBRC configuration" alt="Populate form fields from xBRC configuration" src="images/refresh.png"/></a>
		<div id="getXbrcConfigStatus" style="height: 15px;"></div>				
	</fieldset>
	
	<div class="centered" style="clear: both; margin-top: 10px;">
		<s:submit id="idSubmit" onclick="return validateAddConfigForm()"></s:submit>
	</div>
	
	<br>
	<span id="addConfigStatus"></span>		
	</div></s:form>
</div>

<div id="deployConfigDialog">
	<s:form id="deployXbrcConfigForm" action="deployxbrcconfig" theme="simple"><div>
	<p>Select the xBRC to which you would like to deploy the following configuration.</p>
	<table class="light round narrow">
		<tbody>
		<tr><td>Model:</td><td id="idDeployModel"></td></tr>
		<tr><td>Name:</td><td id="idDeployName"></td></tr>
		<tr><td>Venue Name:</td><td id="idDeployVenueName"></td>
		<tr><td>Description:</td><td id="idDeployDescription"></td>
		</tbody>
	</table>
		
	<input type="hidden" name="configId"/>
	
	<p>
	<s:select cssStyle="max-width: 300px;" name="xbrcId" listKey="id" listValue="dipDisplayName" id="idDownloadXbrcList" list="targetXbrcList"/>
	</p>
	
	<div class="centered" style="clear: both; margin-top: 10px;">
		<button id="idSubmitDeploy" type="button" onclick="return submitDeployXbrcConfigForm()">Submit</button>
	</div>
	
	<br>
	<span id="dployConfigStatus"></span>
	</div></s:form>
</div>

</div>

<sx:div
	id="idXbrcConfig"
	theme="ajax"
	href="getxbrcconfig"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleGetXbrcConfig" 
	formId="getXbrcConfigForm" >
</sx:div>

<sx:div
	id="idDeployXbrcConfig"
	theme="ajax"
	href="deployxbrcconfig"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleDeployXbrcConfig" 
	formId="deployXbrcConfigForm" >
</sx:div>
