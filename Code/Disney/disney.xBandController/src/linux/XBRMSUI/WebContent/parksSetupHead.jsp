<link rel="stylesheet" type="text/css" href="css/home.css"/>
<link rel="stylesheet" type="text/css" href="css/xbrcHealth.css"/>

<script type="text/javascript">
    $(document).ready(function() {
        $("#addConfigDialog").dialog({height:300,width:360,autoOpen:false,modal:true});
//        $("li.last a:first-child").prop("href", "/XBRMSUI/parkssetup.action");
    });

    function showAddConfigDialog() {
        $("#addConfigDialog").dialog('option', 'title', 'Add Park xBRMS');
        $("#addConfigDialog").dialog('open');
        $("span#addConfigStatus").html("");
        var val = $("#idUiHost").prop("value");
        $("#idAddUiHost").prop("value", val);
    }

    function toggleGlobal(checkbox2) {
        var checkbox = $("#_checkbox_");

        if (checkbox2.checked)
        {
            checkbox.prop("value", "true");
        }
        else {
            checkbox.prop("value", "false");
        }
        // alert(checkbox.attr("value"));
    }

    function removeConfigItem(id, url) {
        var val = $("#idUiHost").prop("value");
        $("#idRemoveUiHost").prop("value", val);

        var message = "You are about to delete xBRMS service \"" + url + "\"";
        var userResponse = confirm(message);

        if (userResponse === true) {
            $("div#idProgress").toggle();
            document.forms.removeConfigForm.id.value = id;
            document.forms.removeConfigForm.submit();
        }
    }

    function validateAddConfigForm() {
        var ids = ["idAddUrl", "idAddFqdnHostAlias", "idAddDesc"];
        var action = document.forms.addConfigForm.action;

        for (var i = 0; i < ids.length; i++) {
            id = ids[i];
            var field = document.getElementById(id);

            if (field.value == "") {
                alert("Please provide values for all the fields.");
                return false;
            }
        }

        var iresult = 0;
        var msg = "";
        var result = true;

        $.ajax({
            url: "parkssetupvalidate.action",
            data: {"url" : document.getElementById(ids[0]).value},
            async:   false,
            cache: false,
            success: function(data) {
                iresult = 4;

                if (data.valid == false) {
                    iresult = 1;
                    msg = data.msg;
                }
            },
            error: function(data) {
                iresult = 2;
            },
            dataType: "json",
            type: "GET"
        });

        if (iresult == 1) {
            result = confirm(msg + " Click \"OK\" if you want to accept the settings anyway.");
        }

        return result;
    }

    function saveConfig() {
        $("#saveConfigForm").trigger('submit');
    }

</script>
