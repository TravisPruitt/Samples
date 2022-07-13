<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle"><s:text name="page.title"/></h2>

<s:if test="!canAccessAsset('Editable Content')">
<h4 style="color:grey;"><s:text name="read.only.page"/></h4>
</s:if>

<div id="ajaxErrorDisplay" style="round lightBackground padding"></div>

<div id="xbrcEditContainer">

<div id="tabs" style="position: relative;">
	<ul>
		<s:iterator var="tabHeader" value="tabHeaders" status="status">
			<li>
				<a href="#<s:property/>"><s:property/></a>
			</li>
		</s:iterator>
	</ul>
	<% int index = 0; %>
	<s:iterator var="tab" value="tabs" status="tabIndex">
		
		<div style="position:absolute;top:9px;right:8px;" id="selectAllContainer_${tab.key}" class="selectAll" style="display:none;">
			<label class="checkboxLabel lightText bold" for="selectAll">Select All</label>
			<input id="selectAll_${tab.key}" type="checkbox" value="true" name="selectAll" onclick="selectAll('${tab.key}')">
		</div>
	
		<div id="${key}">
		
		<!-- use the first xbrc of this model type as a template for the update form -->
		<s:iterator var="xbrc" value='common' status="xbrcIndex">
			
			<s:if test="#xbrcIndex.index == #tabIndex.index">
			<div class="formContainer lightBlueBackground round blueText">
				<p class="instructions"><s:text name="instructions.0"/></p>
				<p class="instructions"><s:text name="instructions.1"/></p>
				<p class="instructions"><s:text name="instructions.3"/></p>
				<p class="instructions"><s:text name="instructions.4"/></p>
				
				<form action="updatexbrcconfig" id="idUpdateXbrcForm_${tab.key}" method="POST">
					<input type="hidden" name="updateXbrcs_${tab.key}" id="updateXbrcs_${tab.key}"/>
					<input type="hidden" name="tabSelected" id="tabSelected_${tab.key}" value="<%= index%>"/>
					<input type="hidden" name="model" id="model_${tab.key}" value="${tab.key}"/>
			
					<div style="margin-bottom: 0.5em;"><input type="button" value="Update Selected xBRC(s)" onkeydown="return false;" 
							onclick='updateXbrcs("${tab.key}");' class="blueText submitButton"/></div>

					
					<% int listItemIndex = 0; %>
					<div class="sectionAccordion">
					<s:iterator var="config" value="value" status="configIndex">
					
					<s:if test="#group2 != value.clazz">
						<% if (listItemIndex != 0) {%>
							</ul>
							</div>
						<%}%>
						<s:set name="group2" value="value.clazz"/>
						<h3>${value.section}</h3>
						<div class="accordionContent">
						<ul class="twoColumnList">
					</s:if>
							
					<s:if test="value.updatable != 'false'">
						<li class="bottomPadding" style="position: relative;">

						<div class="label">
							<img <s:if test="value.description == 'N/A'">title="<s:text name='title.no.description'/>"</s:if><s:else>title="${config.value.description}"</s:else> 
								src='images/info_16.png' class="rightMargin" onclick="openInfoDialog($(this))"
							/>
							<label for="${tab.key}_${config.value.uniqueId}">${config.value.name}</label>

						<!--  error messages -->
						<s:if test="value.min != 'N/A' || value.max != 'N/A'">
							<s:if test="value.type == 'java.lang.String'">
								&nbsp;<span class="fieldError" id="error_${tab.key}_${config.value.uniqueId}">
								<s:text name="error.string.out.of.range">
									<s:param>${config.value.min}</s:param>
									<s:param>${config.value.max}</s:param>
								</s:text>
							</span>
							</s:if>
							<s:elseif test="value.type == 'boolean' || value.type == 'java.lang.Boolean'">
								<!-- no error message, it is a select -->
							</s:elseif>
							<s:else>
								&nbsp;<span class="fieldError" id="error_${tab.key}_${config.value.uniqueId}">
								<s:text name="error.number.out.of.range">
									<s:param>${config.value.min}</s:param>
									<s:param>${config.value.max}</s:param>
								</s:text>
								</span>
							</s:else>
						</s:if>
						</div>
						
						<!-- input elements -->
						<s:if test="value.type == 'boolean' || value.type == 'java.lang.Boolean'">
							<select id="input_${tab.key}_${config.value.uniqueId}" 
									name="input_${tab.key}_${config.value.uniqueId}" 
									title="${config.value.description}" 
									 class="simple">
								<option value="-1" class="simple"
										title="<s:text name="title.empty.option">
													<s:param>${config.value.name}</s:param>
											   </s:text>"
										onclick="$(this).parent().addClass('userInput')"><s:text name='do.not.change'/></option>
								<option value="false" class="simple"
										title="<s:text name="title.option">
													<s:param>${config.value.name}</s:param>
													<s:param>false</s:param>
											   </s:text>"
										onclick="$(this).parent().addClass('userInput')">False</option>
								<option value="true" class="simple"
										title="<s:text name="title.option">
													<s:param>${config.value.name}</s:param>
													<s:param>true</s:param>
											   </s:text>"
										onclick="$(this).parent().addClass('userInput')">True</option>
							</select>
						</s:if>
						<s:else>	
							<select id="${tab.key}_${config.value.uniqueId}" 
									name="${tab.key}_${config.value.uniqueId}" 
									title="${config.value.description}" 
									style="width: 260px;"
									onchange="$('input#input_${tab.key}_${config.value.uniqueId}').val($(this).val());">
								<option selected="selected" id="${tab.key}_${config.value.uniqueId}_0"
										value="<s:text name='do.not.change'/>"
										onclick="optionSelected(this, 'input_${tab.key}_${config.value.uniqueId}', '${tab.key}');
												$('input#input_${tab.key}_${config.value.uniqueId}').removeClass('userInput')"
										title="<s:text name="title.do.not.change.option">
													<s:param>${config.value.name}</s:param>
											   </s:text>"><s:text name='do.not.change'/></option>
								<option value="<s:text name='null.out'/>" 
										onclick="optionSelected(this, 'input_${tab.key}_${config.value.uniqueId}', '${tab.key}')"
										title="<s:text name="title.null.out.option">
													<s:param>${config.value.name}</s:param>
													<s:param>en empty string</s:param>
											   </s:text>"><s:text name='null.out'/></option>
								<s:if test="value.defaultValue != null">
								<option value="${config.value.defaultValue}" 
										onclick="optionSelected(this, 'input_${tab.key}_${config.value.uniqueId}', '${tab.key}')"
										title="<s:text name="title.restore.default">
													<s:param>${config.value.defaultValue}</s:param>
											   </s:text>"><s:text name='restore.default'/></option>
								</s:if>
							</select>
							<input id="input_${tab.key}_${config.value.uniqueId}" 
									name="input_${tab.key}_${config.value.uniqueId}"
									<%
									String browser = request.getHeader("User-Agent");
									if(browser.indexOf("MSIE") > 0) {
									%>
									style="margin: 1px 0px 0px -262px; width: 239px; height: 1.1em; border: 0; position: absolute;"
									<%} else if (browser.indexOf("Chrome") > 0) {%>
									style="width: 240px; height: 1.1em; border: 0; 
											position: absolute; top: 20px; left: 11px;"
									<%} else {%>
									style="margin: 3px 0px 0px -257px; width: 240px; height: 1.2em; border: 0; position: absolute;"
									<%}%>
									class="selectInput"/>	
						</s:else>
						</li>
						<% listItemIndex++; %>
						</s:if>
					</s:iterator>
					<!-- work around EI not recognizing css type selector :last-child -->
					<% if (listItemIndex % 2 != 0){ %>
						<li class="bottomPadding" style="height: 40px;"></li>
					<%}%>
					</ul>
					</div>
					</div>
					<input type="button" value="Update Selected xBRC(s)" onkeydown="return false;" 
							onclick='updateXbrcs("${tab.key}");' class="blueText submitButton"/>
				</form> 
			</div>
			</s:if>
		</s:iterator>
		
		<!-- create accordion menu of all xbrcs for this model -->
		<div id="accordion_${tab.key}" class="accordion">
		<s:iterator var="xbrc" value="value" status="xbrcIndex">
			<h3 id="header_${xbrc.key.id}">
				<a href="#${xbrc.key.id}">${xbrc.key.name} (${xbrc.key.ip} ${xbrc.key.haStatus})<input id="${xbrc.key.id}" value="${xbrc.key.id}" type="checkbox" class="xbrcSelect xbrcSelect_${tab.key}"></a>
			</h3>
			<div id="content_${xbrc.key.id}">
				<table class="dark">
				<thead>
					<tr><th>Property</th><th>Value</th></tr>
				</thead>
				<tbody>
					<s:iterator var="config" value="value" status="configIndex">
						
						<s:if test="#group != clazz">
							<s:set name="group" value="clazz"/>
							<tr>
								<td colspan="2" class="sectionName boldText">
									${config.section}
								</td>
							</tr>
						</s:if>
						<tr>
							<td>
								<label class="boldText">${config.name}</label>
							</td>
							<td>${config.value}</td>
						</tr>
					</s:iterator>
				</tbody>
				<tfoot>
				</tfoot>
				</table>
			</div>
		</s:iterator>
		</div><!-- end of accordion -->
		
		<div class="clear"></div>
		
		<s:if test="key == 'ATTRACTION'">
		
			<sx:div 
				theme="ajax" 
				href="xbrccheckchange" 
				executeScripts="true"
				showLoadingText="false"
				showErrorTransportText="false"
				listenTopics="handleXbrcCheckChange_ATTRACTION"
				formId="idXbrcCheckChangeForm_ATTRACTION"
				preload="false"
				notifyTopics="ajaxXbrcCheckChange">
			</sx:div>
			<s:form theme="simple" id="idXbrcCheckChangeForm_ATTRACTION" action="xbrcCheckChange">
				<input type="hidden" name="updateXbrcs"/>
				<input type="hidden" name="tabSelected" value="<%=index++%>"/>
				<input type="hidden" name="model" value="${key}"/>
			</s:form>
		</s:if>
		<s:elseif test="key == 'PARKENTRY'">
			<sx:div 
				theme="ajax" 
				href="xbrccheckchange" 
				executeScripts="true"
				showLoadingText="false"
				showErrorTransportText="false"
				listenTopics="handleXbrcCheckChange_PARKENTRY"
				formId="idXbrcCheckChangeForm_PARKENTRY"
				preload="false"
				notifyTopics="ajaxXbrcCheckChange">
			</sx:div>
			<s:form theme="simple" id="idXbrcCheckChangeForm_PARKENTRY" action="xbrcCheckChange">
				<input type="hidden" name="updateXbrcs"/>
				<input type="hidden" name="tabSelected" value="<%= index++%>"/>
				<input type="hidden" name="model" value="${key}"/>
			</s:form>
		</s:elseif>
		<s:elseif test="key == 'SPACE'">
			<sx:div 
				theme="ajax" 
				href="xbrccheckchange" 
				executeScripts="true"
				showLoadingText="false"
				showErrorTransportText="false"
				listenTopics="handleXbrcCheckChange_SPACE"
				formId="idXbrcCheckChangeForm_SPACE"
				preload="false"
				notifyTopics="ajaxXbrcCheckChange">
			</sx:div>
			<s:form theme="simple" id="idXbrcCheckChangeForm_SPACE" action="xbrcCheckChange">
				<input type="hidden" name="updateXbrcs"/>
				<input type="hidden" name="tabSelected" value="<%= index++%>"/>
				<input type="hidden" name="model" value="${key}"/>
			</s:form>
		</s:elseif>
		
		</div><!-- tab content end -->
	</s:iterator>
	
</div>

</div>
</div>