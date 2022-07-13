<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<script type="text/javascript">
function updatePort()
{
    var eSelect = document.getElementById("idSelectType");
    var ePort = document.getElementById("addXbrcPort");

    ePort.value = eSelect.options[eSelect.selectedIndex].getAttribute('myPort');
}
</script>

<div id="idBody" class="round lightBackground shadow padding">
<H1>Monitored Systems</H1>

<s:form id="removeItemForm" action="removexbrc" theme="simple">
	<input type="hidden" name="id"/>
</s:form>

<s:form id="inactivateItemForm" action="inactivatexbrc" theme="simple">
	<input type="hidden" name="id"/>
</s:form>
				
<s:iterator value="inventory" var="app">
<H2>${type}</H2>
<table class="light round">
	<thead>
       <tr><th>Name</th><th>Direct Address</th><th>Port</th>
        <s:if test='type == "XBRC"'>
            <th>Vip</th>
        </s:if>
        <th>Version</th><th>Last Ping Time</th>

		<s:iterator value="defs" var="def">
			<s:if test="desc.mandatory()">
				<th><s:property value="desc.name()"/></th>
			</s:if>
		</s:iterator>
		<th>Health</th><th>Status</th><th>Actions</th></tr>
	</thead>	
	<tbody>
		<s:iterator value="values" var="val">
		<tr id="inv_tr_${item.id}">
			<td id="inv_name_${item.id}">${item.name}</td>
			<td id="inv_host_${hostname}">${hostname}</td>
			<td id="inv_port_${item.id}">${item.port}</td>
            <s:if test='type == "XBRC"'>
			<td id="inv_vip_${item.id}">${item.vip}</td>
            </s:if>

			<td id="inv_version_${item.id}">${item.version}</td>
			<td id="inv_lastDiscovery_${item.id}">${lastDiscoveryAgo}</td>
			<s:iterator value="fields" var="fld">
				<s:if test="desc.mandatory()">
					<s:if test="desc.type().name() == 'Url'">
						<td id="inv_<s:property value="desc.id()"/>_${item.id}" style="width:5px;"><a href="${value}" target="_new">UI</a></td>
					</s:if>
					<s:else>
						<td id="inv_<s:property value="desc.id()"/>_${item.id}">${value}</td>
					</s:else>
				</s:if>
			</s:iterator>			
			<td id="inv_health_${item.id}" class="centered" style="max-width:40px;padding:0;margin:0;">
				<a href='#' onclick="submitHealthItemForm(${item.id}, '${type}')"><img src="images/${statusIcon}"/></a>
			</td>
			<td id="inv_msg_${item.id}" class="last">
				${statusMessage}
			</td>
			<td>
				<a id="inv_menu_${item.id}" style="background: none; border: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#"><span class="ui-icon ui-icon-triangle-1-s"></span>Execute</a>
				<div id="inv_content_${item.id}" class="hidden">
				<ul>
					<li><a href="#inv_tr_${item.id}" 
							onclick='submitHealthItemForm(${item.id}, "${type}");'>
						<img class="fg-menu-icon" src="images/zoomin.png" title="Explore detailed health status."/>Details</a>
					</li>
					<li><a href="#inv_tr_${item.id}" 
							onclick='inactivateHealthItem("${item.id}", "${item.ip}");'>
						<img class="fg-menu-icon" src="images/deactivate-16.png" title="Temporarily disable this item's monitoring."/>Deactivate</a>
					</li>
					<li><a href="#inv_tr_${item.id}" 
							onclick='removeHealthItem("${item.id}", "${item.ip}");'>
						<img class="fg-menu-icon" src="images/delete-16.png" title="Permamently delete this item from the database."/>Delete</a>
					</li>
				</ul>
				</div>
			</td>
		</tr>
		</s:iterator>
	</tbody>
</table>
</s:iterator>

<div style="height: 50px">
<dl class="horisontalLegend" style="float:left; clear: right; ">
	<dt><img src="images/green-light.png"/></dt><dd><s:text name="legend.light.green"/></dd>
	<dt><img src="images/yellow-light.png"/></dt><dd><s:text name="legend.light.yellow"/></dd>
	<dt><img src="images/red-light.png"/></dt><dd><s:text name="legend.light.red"/></dd>
</dl>
<button type="button" class="submit" style="float: right; clear: right; margin: 1em 0 0 0" onclick="refreshStatus();">Refresh</button>
<button type="button" class="submit" style="float: right; margin: 1em 5px 0 0" onclick="showAddXbrcDialog();">Add Item</button>
</div>

<form id="XBRCForm" action="xbrc">
	<input type="hidden" name="id" id="id"/>
</form>

<form id="IDMSForm" action="idms">
	<input type="hidden" name="id" id="id"/>
</form>

<form id="JMSLISTENERForm" action="jmslistener">
	<input type="hidden" name="id" id="id"/>
</form>

<!-- periodically pull xbrc health status -->
<sx:div 
	theme="ajax" 
	href="xbrclisthealthajax" 
	executeScripts="true"
	showLoadingText="false"
    showErrorTransportText="true"
    errorNotifyTopics="/accessDenied"
    errorText="Your session has expired"
    listenTopics="/refresh,checkAccess"
	startTimerListenTopics="/startTimer"
	stopTimerListenTopics="/stopTimer"
	updateFreq="5000"
	preload="true">
</sx:div>

<!-- dialog to add a new XBRC based on IP address -->
<div id="addXbrcDialog">
	<s:form id="addXbrcForm" action="addxbrc" theme="simple"><div>
	<p>Enter host name and port of the application you wish to monitor. Please use physical (not virtual) address here.</p>	
	
	<div class="horizontalFormFields">	
		<dl>
			<dt><label>Type</label><s:fielderror fieldName="itemClassName"/></dt>
			<dd><select id="idSelectType" name="itemClassName" onchange="updatePort()">
				<s:iterator value="healthItemTypes" var="healthItemType">
                    <option value="<s:property value="className()" />" myPort="<s:property value="port()" />"  ><s:property value="name()"/></option>
                </s:iterator>
                </select></dd>
		</dl>

		<dl>
			<dt><label>Host Name</label><s:fielderror fieldName="xbrcIp"/></dt>
			<dd><s:textfield id="xbrcIp" name="xbrcIp" cssStyle="width: 100px;"/></dd>
		</dl>	
		
		<dl>
			<dt><label>Port</label><s:fielderror fieldName="xbrcPort"/></dt>
			<dd><s:textfield id='addXbrcPort' cssStyle="width: 40px;" name="xbrcPort" value="8080" autofocus="autofocus"/></dd>
		</dl>
	</div>
	
	<div class="centered" style="clear: both;">
		<input type="button" class="submit" onclick="submitAddXbrc()" value="Apply"/>
	</div>
	
	<br>
	<span id="addXbrcStatus"></span>		
	</div></s:form>
</div>

<sx:div
	id="addXbrcDiv"
	theme="ajax"
	href="addxbrc"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleAddXbrc" 
	formId="addXbrcForm" >
</sx:div>

<sx:div
	id="refreshStatusDiv"
	theme="ajax"
	href="xbrchealthrefresh"
	executeScripts="false"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="refreshStatus" >
</sx:div>

</div>
