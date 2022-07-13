<link rel="stylesheet" type="text/css" href="css/attractionView.css" />
<link type="text/css" href="script/jquery-ui-1.8.16.custom.darkblue/css/custom-theme/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />

<script type="text/javascript" src="script/attractionView.js"></script>
<script type="text/javascript" src="script/jquery-ui-1.8.16.custom.darkblue/js/jquery-ui-1.8.16.custom.min.js"></script>

<script type="text/javascript">
$(function()
{
	$("#showGuestsDialog").dialog({height:400,width:550,autoOpen:false});

	var width = "${avConfig.containerWidth}px";
	var height = "${avConfig.containerHeight}px";
	
	$("div#idBody").width(width);
	$("div#idBody").height(height);
});
</script>