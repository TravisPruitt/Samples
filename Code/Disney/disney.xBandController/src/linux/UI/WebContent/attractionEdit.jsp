<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.disney.xband.xbrc.ui.UIProperties" %>
<%@ page import="com.disney.xband.xbrc.ui.AttractionViewConfig" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">
<h2 class="pageTitle"><s:text name="page.title"/></h2>
<div id="attractionEditContainer" <s:if test="hasBackground">style="background: url('${backgroundImageUrl}') no-repeat"</s:if> >
<div id="attractionGrid">
	<%
	/* This draws the grid lines */
	AttractionViewConfig avConfig = UIProperties.getInstance().getAttractionViewConfig();
	int gridSize = avConfig.getGridSize();
	for (int x=0; x < avConfig.getGridWidth() && x < 19; x++) {
		for (int y=0; y < avConfig.getGridHeight(); y++) {
			int leftPos = x * avConfig.getGridSize();
			int topPos = y * avConfig.getGridSize();
	%>
	<div class="gridItemTarget" style='top:<%=topPos%>px; left:<%=leftPos%>px; width:<%=gridSize%>px; height:<%=gridSize%>px;'></div>
	<%}}%>
	
	<!-- The following div is a hack to draw the rightmost and bottom lines of the grid. -->
	<div id="borderBottom" style="border-bottom: 1px solid #CCCCCC"></div>
	<div id="borderRight" style="border-right: 1px solid #CCCCCC"></div>
</div>

<div id="toolbox" title ="Toolbox">
	<s:iterator value="toolboxLocations" var="tl">
		<div style="background: url('images/grid/<s:property value='image'/>');" 
			class="toolboxItem" draggable="true" 
			ondragstart="onDragStartToolboxItem(event, '<s:property value='itemTypeName'/>', '<s:property value='locationType'/>', '<s:property value='image'/>')">
			<span class="gridItemLabel"><s:property value="label"/></span>
		</div>
	</s:iterator>
	<s:iterator value="toolboxItems" var="ti">
		<div class="toolboxItem round path" draggable="true" ondragstart="onDragStartToolboxItem(event, '<s:property value='itemTypeName'/>', '', '<s:property value='image'/>')">
			<div class="pathimage" style="background: url('images/grid/<s:property value='image'/>');"></div>
		</div>
	</s:iterator>
	<div class="toolboxItem garbage"
		ondragover="return false;" ondragenter="return enterDragCell(this, 'dragover toolboxItem');" 
		ondragleave="return leaveDragCell(this, 'toolboxItem garbage');" 
		ondrop='dropItemToGarbage(event)'>
		<img src='images/garbage.jpeg'/>
	</div>
</div>

<div id="editmenu" class="contextmenu" title="Menu">
	<ul>
		<li><s:checkbox name="showSubwayMap" fieldValue="true" label="Show Subway Map" onclick="setShowSubwayMap(this.checked)"/></li>
		<li><img src="images/menuarrow.png"/><a href="#" onclick="showBackgroundDlg()">Change Background</a></li>
	</ul>
</div>

<div id="backgroundDlg" title="Change Background">
	<s:form id="imgForm" action="attractionedit" name="imageForm" method="post" enctype="multipart/form-data" theme="simple"><div>
		<s:if test="hasBackground">
			<span class="nobr">
			<img src="${backgroundImageUrl}" width="180" alt="Background Image" />				
			<s:checkbox label="Remove" name="removeImage" id="removeid" cssStyle="vertical-align: top" onclick="submitImageForm()"></s:checkbox><span style="vertical-align: top">Remove</span>
			</span>			
		</s:if>
		<p>Select a background image to upload. The image file must be 600 pixels high and 950 pixels wide.</p>
		<s:file name="image" id="imageid" accept="image/bmp,image/gif,image/jpeg,image/png" onchange="submitImageForm(this)"/>		
	</div></s:form>
</div>

<div class="legend round">
	<h2><s:text name="legend"/></h2>
	<img src="images/guest.png"/><h3><s:text name="guest"/></h3>
	<img src="images/xguest.png"/><h3><s:text name="guest.fastpass"/></h3>
</div>

<sx:div 
	theme="ajax" 
	href="attractioneditajax" 
	executeScripts="true"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleAttractionEdit" 
	formId="idAttractionEditForm" >
</sx:div>

</div>
</div>

<s:form theme="simple" id="idAttractionEditForm" action="attractioneditajax">
	<s:hidden name="action" />
	<s:hidden name="itemType"/>
	<s:hidden name="locationType" />
	<s:hidden name="itemId" />
	<s:hidden name="xgrid" />
	<s:hidden name="ygrid" />
	<s:hidden name="image" />
	<s:hidden name="showSubwayMap" />
</s:form>

<s:form theme="simple" id="gridItemForm" action="griditemedit">
	<s:hidden name="gridItemId"/>
</s:form>

<sx:div
	id="gridItemEditDialog"
	theme="ajax"
	href="griditemedit"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="true"
	listenTopics="handleShowGridItemForm" 
	formId="gridItemForm" >
</sx:div>

<sx:div
	id="gridItemSaveDiv"
	theme="ajax"
	href="griditemsave"
	executeScripts="true"
	preload="false"
	showLoadingText="false"
	showErrorTransportText="false"
	listenTopics="handleSaveGridItemForm" 
	formId="gridItemAjaxForm" >
</sx:div>


