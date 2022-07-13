<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
    <h2 class="pageTitle"><s:text name="page.title"/></h2>
    
	<s:if test="!canAccessAsset('Editable Content')">
	<h4 style="color:grey;"><s:text name='read.only.page'/></h4>
	</s:if>

	<h3 class="blueText">1. Select found reader you would like to assign:</h3>
    <div class="radio-section">
            <table id="idFoundReadersTable" class="light round rlInfo">
                <thead>
                <tr>
                    <th></th>
                    <th>Type</th>
                    <th>Version</th>
                    <th>Address</th>
                    <th>MAC Address</th>
                    <th>Last Hello</th>
                    <th>Light Up</th>
                </tr>
                </thead>
                <tbody>
                <s:iterator var="readerInfo" value="inventory" status="readerStat">
                  		<tr id="readerInfo_${id}">
                			<td><input id="ck_${id}" type="checkbox"
                             		class="xbrcSelect" name="readers_chk" value="${macAddress}"></td>
                         	<td id="rdescription_${id}">${type.description}</td>
                      		<td id="rversion_${id}">${version}</td>
                      		<td id="rip_${id}">${ipAddress}</td>
                         	<td id="rmac_${id}">${macAddress}</td>
                      		<td id="rhello_${id}"><s:property
                            		value='getFormattedTimeLastHello("MMM d yyyy HH:mm:ss")'/></td>
                        	<td id="identify_${id}">
								<s:if test="tapReader">
                              		<img src="images/light-16px.png"
                                    	title="Play an identification media sequence."
                                    	onclick="identifyReader(this, '${macAddress}')"
                                   		style="cursor:pointer;"/>
                               	</s:if>
                              	<s:else>
                               		<span class="lightText">Not Available</span>
                             	</s:else>
                     		</td>
                 		</tr>
    			</s:iterator>
                </tbody>
            </table>
            <a href="#" style="float:right;clear:right;font-size:110%;margin: 5px 0 0 0;" class="blueText" onclick="refreshStatus();">Refresh Table</a>
    </div>

	<s:if test="numReaders > 0">
	<h3 class="blueText">2. Specify the xBRC to assign the selected readers to:</h3>
    <div style="height: 80px">
            <s:select id="xbrcsListId" name="xbrc" list="xbrcs" headerKey="-1" labelSeparator=""
                      headerValue="VIP"/>
            <s:textfield id="idXbrcVipVal" cssStyle="width: 160px;"/>
            <button type="button" class="blueText" onclick="assignXbrc();">Assign</button>

			<div id="responseFromServer" style="display:none;">
            <div>
                <s:if test="execStatus == 0">
                    <dl style="color: green; float:left; clear: left;">
                        <dd><s:property value="execResult"/></dd>
                    </dl>
                </s:if>
                <s:elseif test="execStatus == 1">
                    <dl style="color: blue; float:left; clear: left;">
                        <dd><s:property value="execResult"/></dd>
                    </dl>
                </s:elseif>
            </div>
            <s:else>
                <dl style="color: red; float:left; clear: left;">
                    <dd><s:property value="execResult"/></dd>
                </dl>
            </s:else>
            </div>
    </div>
	</s:if>
</div>

<s:form action="identifyreader-unasignedreaders" id="idReaderIdentifyForm" theme="simple">
    <s:hidden name="macAddress"/>
</s:form>
<sx:div
        theme="ajax"
        href="identifyreader-unasignedreaders"
        executeScripts="false"
        showLoadingText="false"
        showErrorTransportText="false"
        listenTopics="handleIdentifyReader"
        formId="idReaderIdentifyForm"
        preload="false"
        notifyTopics="ajaxIdentifyReader-unasignedReaders">
</sx:div>
