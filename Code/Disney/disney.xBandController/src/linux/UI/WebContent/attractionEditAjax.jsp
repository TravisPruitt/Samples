<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
				
<div id="attractionPlan">
	<s:if test="showSubwayMap">
	<s:iterator value="gridItems" var="gi">
		<div class="gridItem" style="background: url('images/grid/<s:property value='image'/>'); top:<s:property value='topPos'/>px; left:<s:property value='leftPos'/>px;" 
			draggable="true" ondragstart="onDragStartGridItem(event, <s:property value='gridItem.id'/>)">
			<span class="gridItemLabel"><s:property value='label'/></span>
			<s:a href="javascript:showGridItemForm('%{gridItem.id}')"><img class="zoomin" src="images/settings16.png"/></s:a>
			<s:if test="gridItem.itemType.toString() != 'Gate' && usingState == false"><span class="gridItemType">${locationName}(${gridItem.sequence})</span></s:if>
			<s:if test="gridItem.itemType.toString() != 'Gate' && usingState"><span class="gridItemType">${stateName}(${gridItem.sequence})</span></s:if>
		</div>
	</s:iterator>
	<s:iterator value="emptyCells" var="ec">
		<div class="emptyCell" style='top:<s:property value="topPos"/>px; left:<s:property value="leftPos"/>px;' 
			ondragover="return false;" ondragenter="return enterDragCell(this, 'dragover emptyCell');" 
			ondragleave="return leaveDragCell(this, 'emptyCell');" 
			ondrop='dropItemToCell(event, <s:property value="x"/>, <s:property value="y"/>)'></div>		
	</s:iterator>
	</s:if>
</div>

<script type="text/javascript">
hideProgress();
</script>