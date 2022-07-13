<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/new/legacy-jquery-custom-blue/js/jquery-ui-1.9.2.custom.min.js"></script>
<link type="text/css" href="script/new/legacy-jquery-custom-blue/css/legacy-custom-blue-theme/jquery-ui-1.9.2.custom.min.css" rel="stylesheet" />

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />

<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<style type="text/css">

.infoImageContainer {
	position: relative;
}

.infoImage {
	position:absolute; top:0px; right:0px;
	cursor: pointer;
}

#schedulerItemPage {
}

#schedulerItem {
	width: 60%;
	float: left;
}

#schedulerItemHelp {
	width: 35%;
	float: right;
}

#schedulerItemHelp h1 {
	font-size: 12px;
}

.fieldDivider {
	border-bottom: 2px solid #7C7C7C;
	padding: 3px;
	color: #5C5C5C;
	font-size: 16px;
}
 
</style>

<script type="text/javascript">
		
$(document).ready(function()
{
});

function showSchedulerItems() {
	window.location.href = "scheduler";
}

function confirmDeleteSchedulerTask() {
	return confirm("Are you sure you want to delete this task?");
}

</script>