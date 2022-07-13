<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
    
    <h2 class="pageTitle"><s:text name="page.title"/></h2>

    <div style="font-weight: bold; width: 90%; font-style: italic; color: #205195;">
    <span style="text-decoration: blink;">
    ATTENTION!
    </span>
    Multiple instances of the xBRMSUI application accessible through the same VIP
    must be configured individually and identically. Please make sure you are
    accessing this page using a DIP address in the browser address bar. After
    saving the configuration repeat the setup on all the other xBRMSUI instances.
    </div>

    <s:form id="removeConfigForm" action="parkssetup" theme="simple">
        <input type="hidden" name="id"/>
        <input type="hidden" name="action" value="delete"/>
        <input type="hidden" name="uiHost" id="idRemoveUiHost" />
    </s:form>

    <s:form id="saveConfigForm" action="parkssetup" theme="simple">
        <input type="hidden" name="action" value="save"/>

        <div style="margin-bottom:25px; margin-top: 20px;">
            <label>UI Host Name for Parks Chooser page</label>
            <s:textfield id="idUiHost" required="true" name="uiHost"/>
        </div>
    </s:form>

    <table id="configList" class="light round">
        <thead>
        <tr style="text-align:center;">
            <th style="text-align:center;">xBRMS URL</th>
            <th style="text-align:center;">UI Host Alias</th>
            <th style="text-align:center;">Description</th>
            <th style="text-align:center;">Global</th>
            <th style="text-align:center;">Remove</th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="xbrmsServers" var="val">
            <tr id="inv_tr_${id}">
                <td>${url}</td>
                <td>${fqdnHostAlias}</td>
                <td>${desc}</td>
                <td>${global}</td>
                <td>
                    <a href="#" onclick='removeConfigItem("${id}", "${url}");'><img src="images/delete.png"/></a>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>

    <div style="height: 50px">
        <button type="button" class="submit" style="float: right; margin: 1em 5px 0 0" onclick="showAddConfigDialog();">
            Add New
        </button>

        <s:if test="changed">
            <button type="button" class="submit" style="float: right; margin: 1em 5px 0 0" onclick="saveConfig();">
                Save
            </button>
        </s:if>
        <s:else>
            <button type="button" class="submit" disabled="true" style="float: right; margin: 1em 5px 0 0" onclick="saveConfig();">
                Save
            </button>
        </s:else>

    </div>

    <!-- dialog to add a new configuration -->
    <div id="addConfigDialog">
        <s:form id="addConfigForm" action="parkssetup" method="post" enctype="multipart/form-data" theme="simple">
            <input type="hidden" name="action" value="add"/>
            <input type="hidden" name="uiHost" id="idAddUiHost" />

            <div>
                <p>Configure a new park xBRMS service.</p>

                <dl class="leftJustified">
                    <dt><label>*<s:text name="conf.url"/></label></dt>
                    <dd><s:textfield id="idAddUrl" required="true" name="url"/><s:fielderror fieldName="url"/></dd>

                    <dt><label>*<s:text name="conf.fqdnHostAlias"/></label></dt>
                    <dd><s:textfield id="idAddFqdnHostAlias" required="true" name="fqdnHostAlias"/><s:fielderror
                            fieldName="fqdnHostAlias"/></dd>

                    <dt><label>*<s:text name="conf.desc"/></label></dt>
                    <dd><s:textfield id="idAddDesc" required="true" name="desc"/><s:fielderror
                            fieldName="desc"/></dd>
                    Global Service<input type="checkbox" name="global2" onclick="toggleGlobal(this)"
                                     id="addConfigForm_global"/>
                    <input type="hidden" id="_checkbox_" name="global" value="false"/>
                </dl>

                <div class="centered" style="clear: both; margin-top: 20px;">
                    <s:submit id="idSubmit" onclick="return validateAddConfigForm()"></s:submit>
                </div>

                <br>
            </div>
        </s:form>
    </div>
</div>
