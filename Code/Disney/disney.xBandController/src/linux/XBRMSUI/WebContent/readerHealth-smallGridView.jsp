<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

	<div id="readers-container" class="small-grid lightBackground round-top-left-right">
        <ul id="xbrcs">
        	<s:if test="inventory.size() > 0">
          	<s:iterator var="anXbrc" value="inventory" status="xbrcStat">
          	<li class="xbrc darkBackground round-top-left-right" id="xbrc_${anXbrc.xbrc.id}">
				<div class="xbrc-caption lightText darkBackgroundround-top-left-right clear"><h2 style="display: inline;"><em id="xname_${anXbrc.xbrc.id}">${anXbrc.xbrc.name}</em></h2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:
                	<em id="xhostname_${anXbrc.xbrc.id}">${anXbrc.xbrc.hostname}</em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Facility ID: <em id="xfacid_${anXbrc.xbrc.id}">${anXbrc.xbrc.facilityId}</em>
               	</div>
               	<s:if test="loc.readerlocationinfo.size() > 0">
               	<s:iterator var="locInfo" value="loc.readerlocationinfo" status="locStat">
	           		<s:if test="readers.size() > 0">
	           		<div class="location blueBorder-top blueBorder-right blueBorder-bottom">
	           			<div class="location-caption lightText darkBackground blueBorder-left" title="${locInfo.name}">Location: <em id="lname_${id}">${locInfo.shortName}</em></div>
	           			<!-- Draw the grid -->
	           			<ul class="background-grid darkBackground">
	           				<li class="blueBorder-top blueBorder-left darkBackground"/>
	           				<li class="blueBorder-top blueBorder-left darkBackground"/>
	           				<li class="blueBorder-top blueBorder-left darkBackground"/>
	           				<li class="blueBorder-top blueBorder-left darkBackground"/>
	           				<li class="blueBorder-top blueBorder-left darkBackground">
	           					<a class="lightText bold"
	           						onclick='moreReaders("${anXbrc.xbrc.id}","${anXbrc.xbrc.ip}", ${anXbrc.xbrc.port}, ${locInfo.id}, "${locInfo.name}")'>More</a>
	           				</li>
	           			</ul>
	           			<!-- End of the grid -->
	           			<ul class="readers">
	           			<s:iterator var="readerInfo" value="readers" status="readerStat">
	           				<s:if test="#readerStat.index < 4">
	                		<li id="grid_${anXbrc.xbrc.id}_${locInfo.id}_${id}"
	                			class="item-small-grid">
	                			<a onclick="showReaderDetailInfo('${status}','${locInfo.name}','${name}','${type.description}',
	                									'${version}','${ipAddress}','${macAddress}','${deviceId}','${statusMessage}')"
	                				class="${status}" title="${name} -- ${macAddress}"
	                				id="grid-rstatus_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${type}</a>
	                		</li>
	                		</s:if>
	                	</s:iterator>
	                	</ul>
	                </div>
	                </s:if>
                </s:iterator>
                </s:if>
                <div class="clear"></div>
          	<li>
            </s:iterator>    
        	</s:if>
        </ul>

	</div>
	
	<!-- reader's detail info dialog -->
	<div class="reader-detail-info" id="reader-detail-info-dialog" style="display: none">
		<div id="rdi-status"></div>
		<ul>
		   	<li><label class="blueText bold">Location Name</label><span id="rdi-locname"></span></li>
	     	<li><label class="blueText bold">Reader Name</label><span id="rdi-rname"></span></li>
	       	<li><label class="blueText bold">Description</label><span id="rdi-rdesc"></span></li>
	      	<li><label class="blueText bold">Version</label><span id="rdi-rversion"></span></li>
	       	<li><label class="blueText bold">Ip Address</label><span id="rdi-rip"></span></li>
	      	<li><label class="blueText bold">Mac Address</label><span id="rdi-rmac"></span></li>
	      	<li><label class="blueText bold">Device ID</label><span id="rdi-rdeviceid"></span></li>
	      	<li><label class="blueText bold">Status Message</label><span id="rdi-rstatusMessage"></span></li>
		</ul>
	</div>
	<!-- end of reader's detail info dialog -->
	
	<!-- all readers for this location dialog box -->
	<div id="locationReaders" style="display: none;"></div>
	
	<a id="locationReaders_menu" style="background: none; border: none;display: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#"><span class="ui-icon ui-icon-triangle-1-s"></span>Execute</a>
	<div id="locationReaders_content" class="hidden">
		<ul>
			<li><a href="#" onclick="identifyReader(this, ${anXbrc.xbrc.id}, ${id})">
				<img class="fg-menu-icon" src="images/light-16px.png" title="Play an identification media sequence."/>Light Up</a>
			</li>
		</ul>
	</div>
	<!-- end of all readers for this location -->
	
	<s:form action="locationreaders" id="idLocationReadersForm" theme="simple">
		<s:hidden name="xbrcId"/>
		<s:hidden name="xbrcIp"/>
		<s:hidden name="xbrcPort"/> 
		<s:hidden name="locationId"/>
		<s:hidden name="locationName"/>
	</s:form>
	<sx:div 
		theme="ajax" 
		href="locationreaders" 
		executeScripts="true"
		showLoadingText="false"
		showErrorTransportText="false"
		listenTopics="handleLocationReaders"
		formId="idLocationReadersForm"
		preload="false"
		notifyTopics="ajaxLocationReaders">
	</sx:div>