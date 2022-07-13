<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/new/legacy-jquery-custom-blue/js/jquery-ui-1.9.2.custom.min.js"></script>
<link type="text/css" href="script/new/legacy-jquery-custom-blue/css/legacy-custom-blue-theme/jquery-ui-1.9.2.custom.min.css" rel="stylesheet" />

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />

<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<style type="text/css">

.dataTables_filter label:AFTER {content: "" !important;}

.jslink { text-decoration:underline; cursor: pointer; }

#logsFilterDiv { border: 1px solid #BBBBBB; width: 100%; margin: 0 0 10px 0; padding: 7px 0 7px 0px; background-color: white; }
#logsFilterDiv select { margin-left: 5px; }
#logsFilterDiv input { margin-right: 5px; }

#idProgress { position: absolute; top: 300px; left: 50%; }
 
#schedulerLogsList_info { display: none; }

</style>

<script type="text/javascript">
		
$(document).ready(function()
{	
});

function showSchedulerLogDetail(itemKey) {
	alert("Not implemented yet. TODO");
}

function applyLogsFilter() {
	showProgress();
	dojo.event.topic.publish('refreshschedulerlogs');	
}

</script>