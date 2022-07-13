<link rel="stylesheet" type="text/css" href="css/attractionEdit.css" />
<link rel="stylesheet" type="text/css" href="css/form.css" />
<link type="text/css" href="script/jquery-ui-1.8.16.custom.darkblue/css/custom-theme/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />

<script type="text/javascript" src="script/attractionEdit.js"></script>
<script type="text/javascript" src="script/jquery-ui-1.8.16.custom.darkblue/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="script/validation_utils.js"></script>

<script type="text/javascript">
$(document).ready(function()
{
	$("#toolbox").dialog({
		height:408,
		width:338,
		position:'right',
		closeOnEscape: false,
	});
	$("#backgroundDlg").dialog({height:300,width:320,autoOpen:false});	
	$("#editmenu").dialog({height:150,width:204,position:'left'});
	$("#gridItemEditDialog").dialog({height:430,width:430,autoOpen:false});
    
	var width = "${avConfig.containerWidth}px";
	var height = "${avConfig.containerHeight}px";
	var borderWidth = "" + (${avConfig.containerWidth} - 50) + "px";
	var borderHeight = "" + (${avConfig.containerHeight} - 100) + "px";
	
	$body = $("div#idBody");
	$body.width(width);
	$body.height(height).css("height", "+=20px");
	
	$("div#attractionGrid #borderBottom").css({
		position: 'absolute',
		top: borderHeight,
		left: "0px",
		width: borderWidth,
		height: "1px",
	});
	$("div#attractionGrid #borderRight").css({
		position: 'absolute',
		top: "0px",
		left: borderWidth,
		height: borderHeight,
		width: "1px",
	});
});

function submitImageForm( image ) {
			
	if (image != null && image != "undefined"){
		var removecb = document.getElementById("removeid");
		if (removecb != null) {
			removecb.disabled = true;	
		}
		//image.disabled = true;
	}
	document.imageForm.submit();
}

function saveGridItem(form) {
	
	var valid = true;
	if (form){
		var errorSpan = $("span#gridItem_sequence");
		testedValue = form.gridItemAjaxForm_gridItem_sequence.value;
		var numValue = parseFloat(testedValue);
		if (!isLegalJavaInteger(numValue)){
			errorSpan.show();
			valid = false;
		} else {
			errorSpan.hide();
		}
	}
	
	if (valid){
		showProgress();
		
		dojo.event.topic.publish('handleSaveGridItemForm');
	}
}
</script>
