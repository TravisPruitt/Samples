<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<sx:head/>

<script type="text/javascript">
function updatePort()
{
    var eSelect = document.getElementById("idSelectType");
    var ePort = document.getElementById("addXbrcPort");

    ePort.value = eSelect.options[eSelect.selectedIndex].getAttribute('myPort');
}
</script>

<div id="idBody" class="round lightBackground shadow padding">

<h2 class="pageTitle"><s:text name="page.title"/></h2>
	
<ul id="monitoredSystems">
</ul>

<div style="height: 50px" class="clear">
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

<!-- dialog to add a new XBRC based on IP address -->
<div id="addXbrcDialog">
	<div>
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
		<input type="button" class="submit" onclick="addHealthItem();" value="Apply"/>
	</div>
	
	<br>
	<span id="addXbrcStatus"></span>		
	</div>
</div>

</div>
