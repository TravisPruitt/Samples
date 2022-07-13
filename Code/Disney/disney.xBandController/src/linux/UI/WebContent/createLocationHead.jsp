<link rel="stylesheet" type="text/css" href="css/form.css" />

<link rel="stylesheet" type="text/css" href="css/createLocation.css" />

<script type="text/javascript" src="script/validation_utils.js"></script>

<script type="text/javascript">
function saveLocation(form){
	
	var valid = true;
	if (form){
		var locationNameSpan = $("span#location_name");
		if (form.idLocationSaveForm_location_name.value == "" || form.idLocationSaveForm_location_name.value == "UNKNOWN"){
			locationNameSpan.show();
			valid = false;
		} else {
			locationNameSpan.hide();
		}

        // Do some checking on the input characters of the location name.
        // Only check if we're still valid.
        if ( valid && form.idLocationSaveForm_location_name.value !== null )
        {
            if ( form.idLocationSaveForm_location_name.value.indexOf('<') >= 0 ||
                    form.idLocationSaveForm_location_name.value.indexOf('>') >= 0 )
            {
                locationNameSpan.show();
                valid = false;
            }
        }
    }
	
	if (valid){
		form.submit();
	}
}

</script>