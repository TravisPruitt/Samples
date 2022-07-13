/*
 * Before you can use the ErrorDialog object, do ErrorDialog.init() on load of your page.
 * Once loaded use the ErrorDialog.open() method.
 */
var ErrorDialog = function() {};
ErrorDialog.init = function(element) {
	if (element === undefined)
	{
		$errorDialog = $("#errorDialog");
		if ($errorDialog.length == 0)
		{
			var errorDialogUniqueId = "errorDialog_12341234";
			$("body").append('<div id="' + errorDialogUniqueId + '"></div>');
			$errorDialog = $("#" + errorDialogUniqueId);
		}
	}
	else
	{
		$errorDialog = $(element);
	}

	if ($errorDialog.length > 0)
	{
		$errorDialog.dialog({
			minHeight: 100,
			minWidth: 200,
			autoOpen: false,
			draggable: false,
			closeOnEscape: true,
			modal: true,
			show: "medium",
			title: "Error"
		});
	}
};
ErrorDialog.open = function(errorMessage) {
	if ($errorDialog.length == 0)
		return;
	
	if ($errorDialog.dialog("isOpen"))
		$errorDialog.dialog("close");
	
	if (errorMessage === undefined)
		errorMessage = "Oops! Unknown error has occured.";
	
	errorMessage += "<p style='color: Silver'>Use the 'esc' key or the 'x' icon to close.</p>";
		
	$errorDialog.html(errorMessage).dialog("open");
};
ErrorDialog.close = function(){
	if ($errorDialog.length == 0)
		return;
	
	if ($errorDialog.dialog("isOpen"))
		$errorDialog.dialog("close");
};