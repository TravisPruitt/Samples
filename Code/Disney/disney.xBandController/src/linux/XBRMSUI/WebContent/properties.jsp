<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

<!--<img src="images/restart_16.png" onclick="refresh()" title="Refresh the content of this page without reloading it." class="clickable" style="position: absolute; top:1em; right:1em; z-index: 1000;"/>-->
<h2 class="pageTitle"><s:text name="page.title"/></h2>

<s:if test="!canAccessAsset('Editable Content')">
<h4 style="color:grey;"><s:text name='read.only.page'/></h4>
<div id="readOnlyInfoDialog" title="<s:text name='read.only.page'/>" style="display:none;"></div>
</s:if>

<s:form action="updateProperties" theme="simple" id="idUpdatePropertiesForm">
<div id="xbrmsPropertiesForm" class="container round" style="width: 100%;">

<fieldset class="blueBorder padding round topMargin">
<legend class="blueText" style="font-size: 1.2em;">General</legend>
<div>
	<dl class="leftJustified firstColumn">
		<dt><label><s:text name="xbrmsConfig.name"/></label>
			<img class="infoImage"
				 title="<s:text name='info.xbrmsConfig.name'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.name" maxlength="1024"/></dd>
	</dl>
	
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="xbrmsConfig.id"/></label>
			<img class="infoImage"
				 title="<s:text name='info.xbrmsConfig.id'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.id" maxlength="1024"/></dd>
	</dl>
</div>
</fieldset>

<fieldset class="blueBorder padding round topMargin">
<legend class="blueText" style="font-size: 1.2em;">Networking</legend>
<div>
	<dl class="leftJustified firstColumn">
		<dt><label><s:text name="xbrmsConfig.ownIpPrefix"/></label>
			<img  class="infoImage"
				 title="<s:text name='info.xbrmsConfig.ownIpPrefix'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.ownIpPrefix" maxlength="4"/></dd>
	</dl>
	
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="xbrmsConfig.httpConnectionTimeout_msec">
						<s:param>2000</s:param>
						<s:param>5000</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_httpConnectionTimeout_msec" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.xbrmsConfig.httpConnectionTimeout_msec'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.httpConnectionTimeout_msec" maxlength="10"/></dd>
	</dl>
</div>
</fieldset>

<fieldset class="blueBorder padding round topMargin">
<legend class="blueText" style="font-size: 1.2em;">JMS</legend>
<div>
	<dl class="leftJustified firstColumn">
		<dt><label><s:text name="xbrmsConfig.parkId"/></label>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.parkId'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield cssClass="lightGreyBackground" readonly="true" name='parkId' maxlength="1024"/></dd>
				
		<dt><label><s:text name="xbrmsConfig.jmsBroker"/></label>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.jmsBroker'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="jmsBroker" maxlength="1024" cssClass="lightGreyBackground" readonly="true" title="This field must be changed by the modifying the environment.properties file and may not be changed using this page"/></dd>
		
		<dt><label><s:text name="xbrmsConfig.jmsUser"/></label>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.jmsUser'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="jmsUser" maxlength="1024" cssClass="lightGreyBackground" readonly="true" title="This field must be changed by the modifying the environment.properties file and may not be changed using this page"/></dd>
	</dl>
	
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="xbrmsConfig.jmsXbrcDiscoveryTopic"/></label>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.jmsXbrcDiscoveryTopic'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.jmsXbrcDiscoveryTopic" maxlength="1024"/></dd>
		
		<dt><label><s:text name="xbrmsConfig.jmsMessageExpiration_sec">
						<s:param>10</s:param>
						<s:param>86400</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_jmsMessageExpiration_sec" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.jmsMessageExpiration_sec'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.jmsMessageExpiration_sec" maxlength="19"/></dd>
	</dl>
</div>
</fieldset>

<fieldset class="blueBorder padding round topMargin">
<legend class="blueText" style="font-size: 1.2em;">System Health</legend>
<div>
	<dl class="leftJustified firstColumn">
		<dt><label><s:text name="xbrmsConfig.statusThreadPoolCoreSize">
						<s:param>10</s:param>
						<s:param>100</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_statusThreadPoolCoreSize" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.statusThreadPoolCoreSize'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt>
		<dd><s:textfield name="xbrmsConfig.statusThreadPoolCoreSize" maxlength="3"/></dd>
		
		<dt><label><s:text name="xbrmsConfig.statusThreadPoolMaximumSize">
						<s:param>20</s:param>
						<s:param>200</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_statusThreadPoolMaximumSize" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.statusThreadPoolMaximumSize'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt>
		<dd><s:textfield name="xbrmsConfig.statusThreadPoolMaximumSize" maxlength="3"/></dd>
		
		<dt><label><s:text name="xbrmsConfig.statusThreadPoolKeepAliveTime">
						<s:param>2</s:param>
						<s:param>4</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_statusThreadPoolKeepAliveTime" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.statusThreadPoolKeepAliveTime'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt>
		<dd><s:textfield name="xbrmsConfig.statusThreadPoolKeepAliveTime" maxlength="1"/></dd>
		
		<dt><label><s:text name="xbrmsConfig.masterPronouncedDeadAfter_sec">
						<s:param>3</s:param>
						<s:param>6</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_masterPronouncedDeadAfter_sec" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.masterPronouncedDeadAfter_sec'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/></dt> 
		<dd><s:textfield name="xbrmsConfig.masterPronouncedDeadAfter_sec" maxlength="6"/></dd>
	</dl>
	
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="xbrmsConfig.assignedReaderCacheRefresh_sec">
						<s:param>20</s:param>
						<s:param>120</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_assignedReaderCacheRefresh_sec" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.xbrmsConfig.assignedReaderCacheRefresh'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd><s:textfield name="xbrmsConfig.assignedReaderCacheRefresh_sec" maxlength="6"/></dd>
		
		<dt><label><s:text name="xbrmsConfig.unassignedRreaderCacheCleanup_sec">
						<s:param>30000</s:param>
						<s:param>60000</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_unassignedRreaderCacheCleanup_sec" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.xbrmsConfig.unassignedReaderCacheRefresh'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt>
		<dd><s:textfield name="xbrmsConfig.unassignedRreaderCacheCleanup_sec" maxlength="6"/></dd>
	</dl>
</div>
</fieldset>

<fieldset class="blueBorder padding round topMargin">
<legend class="blueText" style="font-size: 1.2em;">Audit</legend>
<div>
	<dl class="leftJustified firstColumn">		
		<dt><label><s:text name="auditConfig.isEnabled"/></label></dt> 
		<dd><select id="idUpdatePropertiesForm_auditConfig_isEnabled" name="auditConfigEnabled">
			<option value="false" <s:if test="!auditConfigEnabled">selected</s:if>>false</option>
			<option value="true" <s:if test="auditConfigEnabled">selected</s:if>>true</option>
		</select></dd>
		
		<dt><label><s:text name="auditConfig.keepInCacheEventsMax">
						<s:param>5000</s:param>
						<s:param>20000</s:param>
				   </s:text>
			</label> <span class="fieldError" id="auditConfig_keepInCacheEventsMax" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.auditConfig.keepInCacheEventsMax'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd><s:textfield name="auditConfig.keepInCacheEventsMax" maxlength="19"/></dd>
		
		<dt><label><s:text name="auditConfig.keepInGlobalDbDaysMax">
						<s:param>7</s:param>
						<s:param>21</s:param>
				   </s:text>
			</label> <span class="fieldError" id="auditConfig_keepInGlobalDbDaysMax" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.auditConfig.keepInGlobalDbDaysMax'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd><s:textfield name="auditConfig.keepInGlobalDbDaysMax" maxlength="10"/></dd>
	</dl>
	
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="auditConfig.pullIntervalSecs">
						<s:param>160</s:param>
						<s:param>60000</s:param>
				   </s:text>
			</label> <span class="fieldError" id="auditConfig_pullIntervalSecs" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.auditConfig.pullIntervalSecs'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd><s:textfield name="auditConfig.pullIntervalSecs" maxlength="19"/></dd>
		
		<dt>
			<label><s:text name="auditConfig.level"/></label> <span class="fieldError" id="auditConfig_level" style="display:none">invalid</span>
			<img class="infoImage"
				 title="<s:text name='info.auditConfig.level'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt>
		<dd>
			<s:select key="auditConfig.level.ordinal()" list="auditConfigLevels" listKey="key" listValue="value" name="auditConfigLevel"></s:select>
		</dd> 
		<!--<dd><s:textfield name="auditConfig.level" maxlength="13"/></dd>-->
	</dl>
</div>
</fieldset>

<fieldset class="blueBorder padding round topMargin">
<legend class="blueText" style="font-size: 1.2em;">Security</legend>
<div>
	<dl class="leftJustified firstColumn">
		<dt><label><s:text name="xbrmsConfig.ksConnectionToSecs">
						<s:param>10</s:param>
						<s:param>20</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_ksConnectionToSecs" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.ksConnectionToSecs'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd><s:textfield name="xbrmsConfig.ksConnectionToSecs" maxlength="10"/></dd>
	</dl>
	
	<dl class="leftJustified secondColumn">
		<dt><label><s:text name="xbrmsConfig.ksExpireLogonDataAfterDays">
						<s:param>10</s:param>
						<s:param>20</s:param>
				   </s:text>
			</label> <span class="fieldError" id="xbrmsConfig_ksExpireLogonDataAfterDays" style="display:none">invalid</span>
			<img class="infoImage" 
				 title="<s:text name='info.xbrmsConfig.isExpireLogonDataAfterDays'/>" 
				 src='images/info_16.png'
				 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd><s:textfield name="xbrmsConfig.ksExpireLogonDataAfterDays" maxlength="10"/></dd>
	</dl>
</div>
</fieldset>

</div>
<div class="clear"></div>
<input type="button" onclick="saveProperties()" value="Save Changes" class="topMargin"/>
</s:form>
</div>