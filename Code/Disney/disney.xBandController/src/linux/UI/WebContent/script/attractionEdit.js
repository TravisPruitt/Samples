
function onDragStartToolboxItem(event, itemType, locationType, image)
{
	var data = "itemType:" + itemType + "|locationType:" + locationType + "|image:" + image;	
	if (navigator.userAgent.indexOf("Firefox")!=-1)
		event.dataTransfer.setData('brokenfirefox', data);
	else
		event.dataTransfer.setData('text/plain', data);
}

function onDragStartGridItem(event, itemId)
{
	var data = "itemId:" + itemId;	
	if (navigator.userAgent.indexOf("Firefox")!=-1)
		event.dataTransfer.setData('brokenfirefox', data);
	else
		event.dataTransfer.setData('text/plain', data);
}

function enterDragCell(obj, className)
{
	obj.setAttribute('class', className);
	return false;
}

function leaveDragCell(obj, className)
{
	obj.setAttribute('class', className);
	return false;
}

function dropItemToCell(event, x, y)
{	
	var data;
	if (navigator.userAgent.indexOf("Firefox")!=-1)
		data = event.dataTransfer.getData("brokenfirefox");
	else
		data = event.dataTransfer.getData("text/plain");
	
	var itemType = "";
	var locationType = "";
	var itemId = "";
	var image = "";
	  	
	var dataArray = data.split("|");
	var key, value;
	
	for (var i = 0; i < dataArray.length; i++)
	{
		key = getKey(dataArray[i]);
		value = getValue(dataArray[i]);
		
		if (key === "itemType") {
			itemType = value;
		} else if (key === "locationType") {
			locationType = value;
		} else if (key === "itemId") {
			itemId = value;
		} else if (key === "image") {
			image = value;
		}
	}
	
	if (itemType == "" && itemId == "")
		return;
	
	if (itemId != "") {
		document.forms.idAttractionEditForm.action.value = "move";
		document.forms.idAttractionEditForm.itemId.value = itemId;
		document.forms.idAttractionEditForm.xgrid.value = x;
		document.forms.idAttractionEditForm.ygrid.value = y;
		dojo.event.topic.publish('handleAttractionEdit');
		return;
	}
	
	document.forms.idAttractionEditForm.action.value = "insert";
	document.forms.idAttractionEditForm.itemType.value = itemType;
	document.forms.idAttractionEditForm.locationType.value = locationType;
	document.forms.idAttractionEditForm.xgrid.value = x;
	document.forms.idAttractionEditForm.ygrid.value = y;
	document.forms.idAttractionEditForm.image.value = image;
	dojo.event.topic.publish('handleAttractionEdit');
}

function dropItemToGarbage(event)
{
	var data;
	if (navigator.userAgent.indexOf("Firefox")!=-1)
		data = event.dataTransfer.getData("brokenfirefox");
	else
		data = event.dataTransfer.getData("text/plain");
	
	var itemId = "";
	  	
	var dataArray = data.split("|");
	var key, value;
	
	for (var i = 0; i < dataArray.length; i++)
	{
		key = getKey(dataArray[i]);
		value = getValue(dataArray[i]);
		
		if (key === "itemId") {
			itemId = value;
		}
	}
	
	if (itemId == "")
		return;
	
	document.forms.idAttractionEditForm.action.value = "delete";
	document.forms.idAttractionEditForm.itemId.value = itemId;
	
	dojo.event.topic.publish('handleAttractionEdit');
}

function setShowSubwayMap(showSubwayMap) {
	document.forms.idAttractionEditForm.action.value = "setshowsubwaymap";
	document.forms.idAttractionEditForm.showSubwayMap.value = showSubwayMap;
	
	dojo.event.topic.publish('handleAttractionEdit');
}

function refreshGridItems() {
	document.forms.idAttractionEditForm.action.value = "";
	dojo.event.topic.publish('handleAttractionEdit');
}

function showGridItemForm(gridItemId) {
	$("#gridItemEditDialog").dialog('option', 'title', 'Properties ');
	$("#gridItemEditDialog").dialog('open');
	document.forms.gridItemForm.gridItemId.value = gridItemId;
	dojo.event.topic.publish('handleShowGridItemForm');
}

function showBackgroundDlg() {
	$("#backgroundDlg").dialog('open');
	/*
	document.forms.gridItemForm.gridItemId.value = gridItemId;
	dojo.event.topic.publish('handleShowGridItemForm');
	*/
}