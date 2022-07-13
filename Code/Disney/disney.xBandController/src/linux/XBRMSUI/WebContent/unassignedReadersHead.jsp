<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/home.css"/>
<link rel="stylesheet" type="text/css" href="css/unreaders.css"/>

<script type="text/javascript" src="script/new/DataTables/media/js/jquery.dataTables.js"></script>
<link type="text/css" href="script/new/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />

<style type="text/css">
.dataTables_info {display:none;}
</style>
<script type="text/javascript">

dojo.event.topic.subscribe("ajaxIdentifyReader-unasignedReaders", function onAjax(data, type, request){
	// the "before" and "load" types are handled someplace elsewhere
	if (type === "before")
	{
		$("form#" + $("div#" + data).attr("formId")).append('<input type="hidden" name="NoAcRedirect" value="1"/>');
	}
	else if (type === "error")
	{
		ErrorDialog.open("<p>The light up command failed.</p><p><b>Reason:</b> " + request.getResponseHeader("errMsg") + "</p>");
	}
});

    $(document).ready(function() 
    {
    	$("#idFoundReadersTable").dataTable({
    		"aoColumnDefs":[
    			{"bSortable":false,"aTargets":[0,6]},
    		],
    		"bPaginate": false,
    		"bFilter": false,
    		"oLanguage":{
    			"sEmptyTable":"There are no readers reporting to this xBRMS at this time.",
    		},
    	});
    	
        setState();
        $('#xbrcsListId').change(function () {
            setState();
        });
        $("input.xbrcSelect").change(function(){
        	$("#responseFromServer").hide();
        });
        $("#responseFromServer").show();
        
        <s:if test="!canAccessAsset('Editable Content')">
        $("input").filter(":checkbox")
        	.attr("readonly","readonly")
        	.addClass("lightGreyBackground")
        	.attr("title","<s:text name='read.only.page'/>")
        	.click(function(event){
        		openInfoDialog($(this));
        	});
        $("input#idXbrcVipVal").hide();
        $(":button").hide();
        </s:if>
    });

    function setState() {
        if ($('#xbrcsListId').prop("selectedIndex") > 0) {
            $('#idXbrcVipVal').hide();
        }
        else {
            $('#idXbrcVipVal').show();
        }
    }

    function refreshStatus() {
        location.href = "unassignedreaders.action";
    }

    function assignXbrc() 
    {
    	// validate user's selections
    	if ($("input:checked").length === 0)
    	{
    		ErrorDialog.open("Please select at least one reader to assign.");
    		return false;
    	}
    	
    	$selectedXbrc = $("select#xbrcsListId").find(":selected");
    	if ($selectedXbrc.length === 0 || $selectedXbrc.val() === "-1")
    	{
    		// neither vip or dip provided for assignment
    		if ($("input#idXbrcVipVal").val() === "")
    		{
    			ErrorDialog.open("Missing xBRC address. Please select either an xBRC to assign this reader to or type in the ip[:port] into the space provided.");
        		return false;
    		}
    	}
    	else
    	{
    		// both vip and dip provided for assignment, don't use the dip
    		$("input#idXbrcVipVal").val("");
    	}
    		
    	// make the request
        var xbrc = document.getElementById("xbrcsListId");
        var xbrms = document.getElementById("xbrmsListId");
        var list = document.getElementsByName("readers_chk");
        var xbrcVip = document.getElementById("idXbrcVipVal");

        if (list.length == 0) {
            location.href = "assignreaders.action?xbrcId=" + xbrc.value;
        }
        else {
            var slist = "";

            for (var i = 0; i < list.length; i++) {
                if (list[i].checked) {
                    if (slist == "") {
                        slist += list[i].value;
                    }
                    else {
                        slist += ("," + list[i].value);
                    }
                }
            }

            if (slist == "") {
                if (xbrms == null) {
                    location.href = "assignreaders.action?xbrcId=" + xbrc.value + "&xbrcVip=" + xbrcVip.value;
                }
                else {
                    location.href = "assignreaders.action?xbrcId=" + xbrc.value + "&xbrmsId=" + xbrms.value + "&xbrcVip=" + xbrcVip.value;
                }
            }
            else {
                if (xbrms == null) {
                    location.href = "assignreaders.action?xbrcId=" + xbrc.value + "&readerMacs=" + slist + "&xbrcVip=" + xbrcVip.value;
                }
                else {
                    location.href = "assignreaders.action?xbrcId=" + xbrc.value + "&readerMacs=" + slist + "&xbrmsId=" + xbrms.value + "&xbrcVip=" + xbrcVip.value;
                }
            }
        }
    }
    function identifyReader(element, macAddress) {
        if (macAddress !== undefined) {
            document.forms.idReaderIdentifyForm.macAddress.value = macAddress;
            dojo.event.topic.publish('handleIdentifyReader');
        }
    }
</script>
