<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

    <div id="readers">
        <div class="innerTab lightBlueBackground round lightGreyBorder">
            <table class="light round rlInfo" id="xbrcs">
                <thead>
                <!-- Learn about the table before displaying it -->
                <s:set name="hasBattery" value="false"/>
                <s:iterator var="anXbrc" value="inventory" status="xbrcStat">
                    <s:iterator var="locInfo" value="loc.readerlocationinfo" status="locStat">
                        <s:iterator var="readerInfo" value="readers" status="readerStat">
                            <s:if test="batteryLevel != null || hardwareType == 'xBR4' ">
                                <s:set name="hasBattery" value="true"/>
                            </s:if>
                        </s:iterator>
                    </s:iterator>
                </s:iterator>
                <tr>
                    <th>Location</th>
                    <th>Name</th>
                    <th style="width: 40px;">Type</th>
                    <th>Version</th>
                    <th>Address</th>
                    <th>MAC Address</th>
                    <th style="width: 40px;">Device ID</th>
                    <th>Last Hello</th>
                    <s:if test="#hasBattery == true">
                        <th style="width: 40px;">Battery</th>
                    </s:if>
                    <th style="width: 40px;">Status</th>
                    <th>Status Message</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <s:iterator var="anXbrc" value="inventory" status="xbrcStat">
                    <s:if test="inventory.size() > 0">
                        <tr class="captionRow">
                            <td <s:if test="#hasBattery == true">colspan="12"</s:if><s:else>colspan="11"</s:else>><h2 style="display: inline;"><em id="xname_${anXbrc.xbrc.id}">${anXbrc.xbrc.name}</em></h2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at: 
                                <em id="xhostname_${anXbrc.xbrc.id}">${anXbrc.xbrc.hostname}</em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Facility ID: <em id="xfacid_${anXbrc.xbrc.id}">${anXbrc.xbrc.facilityId}</em></td>
                        </tr>

                        <s:iterator var="locInfo" value="loc.readerlocationinfo" status="locStat">
                            <s:if test="loc.readerlocationinfo.size() > 0">
                                <s:iterator var="readerInfo" value="readers" status="readerStat">
                                    <s:if test="readers.size() > 0">
                                        <tr id="readerInfo_${anXbrc.xbrc.id}_${locInfo.id}_${id}">
                                            <td style="max-width: 100px; word-wrap: break-word;" id="lname_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${locInfo.name}</td>
                                            <td id="rname_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${name}</td>
                                            <td id="rdescription_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${type.description}</td>
                                            <td id="rversion_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${version}</td>
                                            <td id="rip_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${ipAddress}</td>
                                            <td id="rmac_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${macAddress}</td>
                                            <td id="rdeviceid_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${deviceId}</td>
                                            <td id="rhello_${anXbrc.xbrc.id}_${locInfo.id}_${id}"><s:property
                                                    value='getFormattedTimeLastHello("MMM d yyyy HH:mm:ss")'/></td>
                        <s:if test="#hasBattery == true">
                            <s:if test="batteryLevel == null">
                                <td id="rbattery_${anXbrc.xbrc.id}_${locInfo.id}_${id}" title="No battery detected">-</td>
                            </s:if>
                            <s:else>
                                <td id="rbattery_${anXbrc.xbrc.id}_${locInfo.id}_${id}" title="${batteryLevel}% charged">
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
                                            <td id="rstatus_${anXbrc.xbrc.id}_${locInfo.id}_${id}" class="centered" style="width:40px;">
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
                                            <td id="rstatusmessage_${anXbrc.xbrc.id}_${locInfo.id}_${id}">${statusMessage}</td>
                                            <td id="raction_${anXbrc.xbrc.id}_${locInfo.id}_${id}">
                                                <s:if test="timeSinceLastHello > (2 * (60*1000))">
                                                    <span title='The xbrc is not able to communicate with this reader.'>Unreachable</span>
                                                </s:if>
                                                <s:else>
                                                    <a id="menu-${anXbrc.xbrc.id}_${locInfo.id}_${id}" style="background: none; border: none;" class="fg-button fg-button-icon-right ui-state-default ui-corner-all" href="#"><span class="ui-icon ui-icon-triangle-1-s"></span>Execute</a>
                                                    <div id="content-${anXbrc.xbrc.id}_${locInfo.id}_${id}" class="hidden">
                                                    <ul>
                                                        <s:if test="type.hasLight() == true">
                                                        <li><a href="#raction_${anXbrc.xbrc.id}_${locInfo.id}_${id}" 
                                                                onclick="identifyReader(this, ${anXbrc.xbrc.id}, ${id})">
                                                            <img class="fg-menu-icon" src="images/light-16px.png" title="Play an identification media sequence."/>Light Up</a>
                                                        </li>
                                                        </s:if>
                                                        <li><a href="#raction_${anXbrc.xbrc.id}_${locInfo.id}_${id}" 
                                                                onclick="restartReader(this, ${anXbrc.xbrc.id}, ${id})">
                                                            <img class="fg-menu-icon" src="images/restart_16.png" title="Restart the reader application."/>Restart</a>
                                                        </li>
                                                        <li><a href="#raction_${anXbrc.xbrc.id}_${locInfo.id}_${id}" 
                                                                onclick="rebootReader(this, ${anXbrc.xbrc.id}, ${id})">
                                                            <img class="fg-menu-icon" src="images/reboot_16.png" title="Reboot the reader."/>Reboot</a>
                                                        </li>
                                                    </ul>
                                                    </div>
                                                </s:else>
                                            </td>
                                        </tr>
                                    </s:if>
                                </s:iterator>
                            </s:if>
                        </s:iterator>
                    </s:if>
                </s:iterator>
                </tbody>
            </table>
        </div>
    </div>
