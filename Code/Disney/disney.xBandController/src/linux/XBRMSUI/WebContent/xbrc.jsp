<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
<h2 class="pageTitle">${xbrc.item.name} on ${xbrc.item.ip}<s:if test="xbrc.item.hostname != '' && xbrc.item.hostname != xbrc.item.ip">/${xbrc.item.hostname}</s:if></h2>

<s:url id="statusUrl" action="goToDocumentTypeGridAction" namespace="/setup"/>

<div id="tabs">
   
	<ul>
		<li><a href="#readers"><s:text name="tab.readers.health"/></a></li>
		<li><a href="#status"><s:text name="tab.status"/></a></li>
	</ul>
	
	<div id="readers">
		<div class="innerTab lightBlueBackground round lightGreyBorder">


		<!-- Learn about the table before displaying it -->
		<s:set name="hasBattery" value="false"/>
		<s:iterator var="locInfo" value="rlInfo.readerlocationinfo" status="locStat">
			<s:iterator var="readerInfo" value="readers" status="readerStat">
				<s:if test="batteryLevel != null || hardwareType == 'xBR4'">
					<s:set name="hasBattery" value="true"/>
				</s:if>
			</s:iterator>
		</s:iterator>

		<s:set name="numberOfColumns" value="11"/>
		<s:if test="#hasBattery == true">
			<s:set name="numberOfColumns" value="12" />
		</s:if>
	
		<table id="readerTable" class="light round rlInfo">
			<thead>
				<tr><th>Name</th><th style="width: 40px;">Type</th><th>Version</th><th>IP Address</th><th style="width: 40px;">Port</th><th>MAC Address</th><th style="width: 40px;">Device ID</th><th>Last Time Hello</th><s:if test="#hasBattery == true"><th style="width: 40px;">Battery</th></s:if><th style="width: 40px;">Status</th><th>Status Message</th><th>Actions</th></tr>
			</thead>
			<tbody>
			<s:iterator var="locInfo" value="rlInfo.readerlocationinfo" status="locStat">
				<s:if test="readers.size() > 0">				
				<tr class="captionRow"><td colspan="${numberOfColumns}" id="loc_${locInfo.id}">Readers at location <em>${name}</em></td></tr>

				<s:iterator var="readerInfo" value="readers" status="readerStat">
					<tr id="readerInfo_${locInfo.id}_${id}">
						<td id="rname_${locInfo.id}_${id}">${name}</td>
						<td id="rdescription_${locInfo.id}_${id}">${type.description}</td>
						<td id="rversion_${locInfo.id}_${id}">${version}</td>
						<td id="rip_${locInfo.id}_${id}">${ipAddress}</td>
						<td id="rport_${locInfo.id}_${id}">${port}</td>
						<td id="rmac_${locInfo.id}_${id}">${macAddress}</td>
						<td id="rdeviceid_${locInfo.id}_${id}">${deviceId}</td>
						<td id="rhello_${locInfo.id}_${id}"><s:property value='getFormattedTimeLastHello("MMM d yyyy HH:mm:ss")'/></td>
						<s:if test="#hasBattery == true">
							<s:if test="batteryLevel == null">
								<td id="rbattery_${locInfo.id}_${id}" title="No battery detected">-</td>
							</s:if>
							<s:else>
								<td id="rbattery_${locInfo.id}_${id}" title="${batteryLevel}% charged">
								<s:if test="batteryLevel <= 10.0">
									<img src="images/battery-0.png"/>
								</s:if><s:elseif test="batteryLevel <= 20.0">
										<img src="images/battery-20.png"/>
								</s:elseif>
								<s:elseif test="batteryLevel <= 40.0">
										<img src="images/battery-40.png"/>
								</s:elseif>
								<s:elseif test="batteryLevel <= 60.0">
										<img src="images/battery-60.png"/>
								</s:elseif>
								<s:elseif test="batteryLevel <= 80.0">
										<img src="images/battery-80.png"/>
								</s:elseif>
								<s:else>
									<img src="images/battery-100.png"/>
								</s:else>
								</td>
							</s:else>
						</s:if>

						<td id="rstatus_${locInfo.id}_${id}" class="centered">
						<s:if test="status.name() == 'Green'">
							<img src="images/green-light.png"/>
						</s:if>
						<s:elseif test="status.name() == 'Yellow'">
							<img src="images/yellow-light.png"/>
						</s:elseif>
						<s:else>
							<img src="images/red-light.png"/>
						</s:else>
						</td>
						<td id="rstatusmessage_${locInfo.id}_${id}" >${statusMessage}</td>
						<td id="raction_${locInfo.id}_${id}">
							<s:if test="timeSinceLastHello > (2 * (60*1000))">
								<span title='The xbrc is not able to communicate with this reader.'>Unreachable</span>
							</s:if>
							<s:else>					 	
						 		<a id="menu-${locInfo.id}_${id}" style="background: none; border: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#readerInfo_${locInfo.id}_${id}"><span class="ui-icon ui-icon-triangle-1-s"></span>Execute</a>
								<div id="content-${locInfo.id}_${id}" class="hidden">
								<ul>
									<s:if test="type.hasLight() == true">
									  <li><a href="#readerInfo_${locInfo.id}_${id}" onclick="identifyReader(this, ${id})">
									  	<img class="fg-menu-icon" src="images/light-16px.png" title="Play an identification media sequence."/>Light Up</a>
									  </li>
									</s:if>
									<li><a href="#readerInfo_${locInfo.id}_${id}" onclick="restartReader(this, ${id})">
									  	<img class="fg-menu-icon" src="images/restart_16.png" title="Reastart the reader application."/>Restart</a>
									</li>
									<li><a href="#readerInfo_${locInfo.id}_${id}" onclick="rebootReader(this, ${id})">
									  	<img class="fg-menu-icon" src="images/reboot_16.png" title="Reboot reader."/>Reboot</a>
									</li>
								</ul>
								</div>	
							</s:else>					
						</td>					 
					</tr>
				</s:iterator>
				</s:if>
			</s:iterator>
			</tbody>
		</table>
		</div>
		
		<dl class="horisontalLegend">
			<dt><img src="images/green-light.png"/></dt><dd><s:text name="legend.light.green"/></dd>
			<dt><img src="images/yellow-light.png"/></dt><dd><s:text name="legend.light.yellow"/></dd>
			<dt><img src="images/red-light.png"/></dt><dd><s:text name="legend.light.red"/></dd>
		</dl>

		<!-- periodically refresh xbrc data -->
		<sx:div 
			theme="ajax" 
			href="xbrcajax" 
			executeScripts="true"
			showLoadingText="false"
		    showErrorTransportText="false"
		    errorNotifyTopics="/accessDenied"
		    errorText="Your session has expired"
			listenTopics="refreshreadersstatus"
			formId="idXbrcAjaxForm"
			preload="false"
			notifyTopics="ajaxRefreshReadersStatus">
		</sx:div>
		<s:form theme="simple" id="idXbrcAjaxForm" action="xbrcajax">
			<input type="hidden" name="id" value="${xbrc.item.id}"/>
		</s:form>
		
		<s:form action="identifyreader" id="idReaderIdentifyForm" theme="simple">
			<input type="hidden" name="id" value="${xbrc.item.id}"/>
			<s:hidden name="readerId"/> 
		</s:form>
		<sx:div 
			theme="ajax" 
			href="identifyreader" 
			executeScripts="false"
			showLoadingText="false"
			showErrorTransportText="false"
			listenTopics="handleIdentifyReader"
			formId="idReaderIdentifyForm"
			preload="false" >
		</sx:div>
		
		<s:form action="restartreader" id="idReaderRestartForm" theme="simple">
			<input type="hidden" name="id" value="${xbrc.item.id}"/>
			<s:hidden name="readerId"/> 
		</s:form>
		<sx:div 
			theme="ajax" 
			href="restartreader" 
			executeScripts="false"
			showLoadingText="false"
			showErrorTransportText="false"
			listenTopics="handleRestartReader"
			formId="idReaderRestartForm"
			preload="false" >
		</sx:div>
		
		<s:form action="rebootreader" id="idReaderRebootForm" theme="simple">
			<input type="hidden" name="id" value="${xbrc.item.id}"/>
			<s:hidden name="readerId"/> 
		</s:form>
		<sx:div 
			theme="ajax" 
			href="rebootreader" 
			executeScripts="false"
			showLoadingText="false"
			showErrorTransportText="false"
			listenTopics="handleRebootReader"
			formId="idReaderRebootForm"
			preload="false" >
		</sx:div>
	</div>
	<div id="status">
		<div id="statusMenu"><h3>Showing data from <span></span> to <span></span></h3></div>
		<img class="clickable" style="position:absolute;top:1em;right:1em;z-index:1000;" 
			title="Refresh the content of this tab without reloading this page." 
			onclick="refreshPerfMetricTab()" src="images/restart_16.png">
		<div id="perfErrors" class="error" style="display: none;"></div>
 		<hr>
 		<ul class="perfMetrics">
 		<s:iterator var="metadata" value="metricsMeta" status="row">
 			<li>
 				<div class="whatIsIt" style="z-index:1000;">
 					<span>?</span>
 					<div class="graphDescription">
 						<s:if test='value.description == ""'>
							<p>Description not available.</p>
						</s:if>
						<s:else>
							<p>${value.description}</p>
						</s:else>
						<p><span>To enlarge, click anywhere inside the graph.</span></p>
 					</div>
 				</div>
 				<div id="GraphContainer_${value.name}" 
 					style="height:200px; width:450px;"
 					onclick='largeGraph("${value.name}","${value.version}");'>
 				</div>
 			<li>
 		</s:iterator>
 		</ul>
 		
 		<div class="clear"></div>
		
		<div id="largeGraph">
			<div id="largeGraphContainer" style="height:400px; width:2000px; padding-bottom: 50px;"></div>
		</div>
		
	</div>
</div>

</div>
