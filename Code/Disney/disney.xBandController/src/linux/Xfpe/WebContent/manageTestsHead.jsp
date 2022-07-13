<link rel="stylesheet" type="text/css" href="css/manageTests.css" />

<script type="text/javascript">

function deleteTestSuite(suiteId, name) {
	var message = "You are about to delete test suite " + name;
	
	var userResponse = confirm(message);
	if (userResponse === true){
		document.forms.deleteSuiteForm.suiteId.value = suiteId;
		document.forms.deleteSuiteForm.submit();
	}
}

</script>