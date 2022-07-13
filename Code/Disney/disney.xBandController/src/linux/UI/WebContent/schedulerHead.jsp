<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/new/legacy-jquery-custom-blue/js/jquery-ui-1.9.2.custom.min.js"></script>
<link type="text/css" href="script/new/legacy-jquery-custom-blue/css/legacy-custom-blue-theme/jquery-ui-1.9.2.custom.min.css" rel="stylesheet" />

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />

<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<style type="text/css">

.dataTables_filter label:AFTER {content: "" !important;}

.itemParameters ul { padding: 0px 10px 0px 10px; margin: 0px; list-style-type: decimal; }
.itemParameters li { font-size: 10px; }

.jslink { text-decoration:underline; cursor: pointer; }
 
</style>

<script type="text/javascript">
		
$(document).ready(function()
{	
});

function showSchedulerItemForm(itemKey) {
	document.forms.schedulerItemForm.jobClassName.value = "";
	document.forms.schedulerItemForm.itemKey.value = itemKey;
	document.forms.schedulerItemForm.submit();
}

function showAddNewItemDialog() {
	if ($("#addNewItemDlg").dialog('isOpen') == false)
		$("#addNewItemDlg").dialog('open');
}

function hideAddNewItemDialog() {
	$("#addNewItemDlg").dialog('close');
}

function addNewItem(jobClassName) {
	document.forms.schedulerItemForm.jobClassName.value = jobClassName;
	document.forms.schedulerItemForm.itemKey.value = "";
	document.forms.schedulerItemForm.submit();
}

</script>