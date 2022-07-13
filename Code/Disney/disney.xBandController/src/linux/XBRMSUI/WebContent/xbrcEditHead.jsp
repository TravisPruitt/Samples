<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/validation_utils.js"></script>
<link rel="stylesheet" type="text/css" href="css/xbrcEdit.css" />

<script type="text/javascript">

var ACTIVE_COLOR = '#000';
var INACTIVE_COLOR = '#aaa';
var TOGGLE_BY_SELECT_ALL_CLASS = "toggledBySelectAll";
var model = "ATTRACTION";

var selected = new Array();

dojo.event.topic.subscribe("ajaxXbrcCheckChange", function onAjax(data, type, request){
	// the "before" and "load" types are handled someplace elsewhere
	if (type === "before")
	{
		$("form#" + $("div#" + data).attr("formId")).append('<input type="hidden" name="NoAcRedirect" value="1"/>');
	}
	else if (type === "error")
	{
		$("div#idProgress").toggle();
		
		var reason = request.getResponseHeader("errMsg");
		if (reason === null)
		{
			reason = data.message;
			if (reason === undefined || reason === null)
			{
				reason = request.responseText;
			}
		}
		
		if (reason !== undefined && (reason.indexOf("nauthorized") != -1 || reason.indexOf("uthentication")))
		{
			location.href = "editxbrc.action";
		}
			
		if (reason === undefined || $.trim(reason).length == 0)
		{
			reason = "unknown";
		}
		
		ErrorDialog.open("<p>Failed to retrieve configuration properties for the xBRC you just selected.</p><p><b>Reason:</b> " + request.getResponseHeader("errMsg") + "</p>");
	}
});

$(function() {
	
	// active tab/model
	model = "${model}";
	
	$("input#selectAll_" + model).show();
	
	// info/help dialog
	$("#infoDialog").dialog({
		height:200,
		width:200,
		autoOpen: false
	});
	
	<s:iterator var="tabHeader" value="tabHeaders" status="status">
		$("#accordion_<s:property/>").accordion({
			autoHeight: false,
			event: false,
			collapsible: true,
			active: false
		});
	</s:iterator>

	$(".sectionAccordion").accordion({
		autoHeight: false,
		collapsible: true,
		active: false
	});

	// all accordions must be created before the tabs, otherwise they refuse to play nice with each other
	$("#tabs").tabs({
		selected: ${tabSelected},
		show: function(event, ui) {
			
			model = ui.panel.id;
			
			// get all input fields
			var inputFields = $("input:text");
			
			// define on focus behaviour
		  	var default_values = new Array();
			inputFields.focus(function() {	
		    	if (!default_values[this.id]) {
		      		default_values[this.id] = this.value;
		    	}
		    	if (this.value == default_values[this.id]) {
		      		this.value = '';
		      		this.style.color = ACTIVE_COLOR;
		      		$(this).addClass("userInput");
		    	}
		    	$(this).blur(function() {
		      		if (this.value == '') {
		        		this.style.color = INACTIVE_COLOR;
		        		this.value = default_values[this.id];
		        		$(this).removeClass("userInput");
		      		}
		    	});
		  	});
			
			// show the selectAll checkbox for this model ony
			$("div.selectAll").hide();
			$("div#selectAllContainer_" + model).show();
		}
	});
	
	// define on click behavior
	var clicked, $form, xbrcId, selected, selBeforeArray, selAfterArray, content, header;
	
	<s:iterator var="tabHeader" value="tabHeaders" status="status">
		$("#accordion_<s:property/>").click(function(event){
			clicked = event.target;
			
			if (clicked.type === "checkbox") 
			{
				/*
				* If this checkbox has been checked/unchecked by the Select All,
				* don't notify the server individually about each change.
				* The selectAll() function does the bulk notification.
				*/
				if ($(clicked).hasClass(TOGGLE_BY_SELECT_ALL_CLASS))
					return false;
				
				$form = $('form#idXbrcCheckChangeForm_<s:property/>');
				xbrcId = clicked.value;
				
				if ($form.length != 0){
					// show progress
					$("div#idProgress").toggle();
					
					// costruct a comma separated list of selected xBRCs' names
					selected = $form.children('input')[0];
					if (clicked.checked){
						if (selected.value === ""){
							selected.value = xbrcId;
						} else {
							selected.value = selected.value + "," + xbrcId;
						}
					} else {
						selBeforeArray = selected.value.split(",");
						selAfterArray = new Array();
						for (var i = 0; i < selBeforeArray.length; i++){
							if (selBeforeArray[i] !== xbrcId){
								selAfterArray[selAfterArray.length] = selBeforeArray[i];
						}
							}
						selected.value = selAfterArray.toString();
						
						// all checkboxes that belong to the current model are no longer selected
						$("input#selectAll_" + model).prop("checked", false);
					}
					
					// append form data to this ajax post
					$userInputFields = $("form#idUpdateXbrcForm_<s:property/> input.userInput");
					var userInput = "";
					$userInputFields.each(function(i){
						$this = $(this);
						userInput = userInput + 
								"<input type='hidden' name='" + $this.attr("name") + 
									"' value='" + $this.val() + 
									"' id='" + $this.attr("id") + "'/>";
					});
					$form.append(userInput);
					
					dojo.event.topic.publish('handleXbrcCheckChange_<s:property/>');
				}
				return true;
			} else if (clicked.localName !== "td" && clicked.localName !== "tr" &&
					clicked.localName !== "label" && clicked.localName !== "th" &&
					clicked.localName !== "table"){
				
				if (clicked.localName === "span"){
					xbrcId = clicked.nextElementSibling.attributes.href.value.substr(1);	// lose the #
				} else if (clicked.localName === "a"){
					xbrcId = clicked.attributes.href.value.substr(1);	// lose the #
				} else {
					return;
				}
				
				$("span.ui-icon").toggleClass("ui-icon-triangle-1-s").toggleClass("ui-icon-triangle-1-e");
					
				content = $(this).children("div#content_" + xbrcId);
				header = $(this).children("h3#header_" + xbrcId);
				
				if (content.is(".ui-accordion-content-active")){
					content.slideUp('slow');
					content.removeClass("ui-accordion-content-active");
					
					header.removeClass("ui-state-active ui-corner-top");
					header.addClass("ui-corner-all");
					return false;
				} else {
					content.slideDown('slow');
					content.addClass("ui-accordion-content-active");
					
					header.removeClass("ui-corner-all");
					header.addClass("ui-state-active ui-corner-top");
					return false;
				}
			} // else ignore clicks on the div portion of the accordion
		});
	</s:iterator>
	
	// make certain functionality not accessible to non-admin users
	<s:if test="!canAccessAsset('Editable Content')">
	$("input:text")
		.attr("readonly","readonly")
		.addClass("lightBlueBackground")
		.attr("title", "<s:text name="read.only.page"/>")
		.click(function(event){
			openInfoDialog($(this));
		});
	$("select").attr("disabled","disabled");
	$(":button").hide();
	</s:if>
});

function refreshPropertiesPage() 
{
	location.href = "editxbrc.action";
}

function userInput(element)
{
	$this = element;
	if ($this[0].value.length == 0){
		$this.removeClass('userInput');
	}
}

function optionSelected(element, inputName, currentModel)
{
	model = currentModel;
	
	var inputName = 'input#' + inputName;
	
	$(inputName).val(element.value);
	$(inputName).removeClass('userInput');
	
	$form = $("form#idXbrcCheckChangeForm_" + model);
	if ($form.length > 0){
		$form.children(inputName).each(function(i){
			// remove listeners (prevents memory leaks)
			$(this).unbind();
			// remove the element
			$(this).remove();
		});
	}
}

function setCommon(currentModel, common, updateXbrcs, tabSelected, userInput)
{
	model = currentModel;
	
	$("input.xbrcSelect_" + model).removeClass(TOGGLE_BY_SELECT_ALL_CLASS);
	
	if (model !== "undefined" && common !== "undefined" && userInput !== "undefined")
	{	
		var fieldId, fieldValue, fieldType, value;
		
		<s:iterator var="tabHeader" value="tabHeaders" status="tabIndex">
		if (model === "<s:property/>"){
			
			// mark xbrcs for update
			$("form#idUpdateXbrcForm_<s:property/> input:hidden").each(function(i)
			{
				if ($(this).attr("id") === "updateXbrcs_<s:property/>"){
					$(this).attr("value", updateXbrcs);
				}
			});
			
			// clear previous default field values while preserving user's input
			if (updateXbrcs === ""){
				$("form#idUpdateXbrcForm_<s:property/> input:text").each(function(i)
				{
					if (!$(this).is(".userInput")){
						$(this).val("");
					}
				});
			} else {
				$("form#idUpdateXbrcForm_<s:property/> input:text").each(function(i)
				{
					if (!$(this).is(".userInput")){
						$(this).val("<s:text name='do.not.change'/>");
					}
				});
			}
			$("form#idUpdateXbrcForm_<s:property/> select.simple").each(function(i)
			{
				if (!$(this).is(".userInput")){
					$options = $(this).children("option");
					if ($options.length != 0)
					{
						$options.first().attr("selected", "selected");
					}
				}
			});
			
			// populate the form with common values
			if (updateXbrcs !== ""){
			for (var i = 0; i < common.length; i++)
			{
				fieldId = "<s:property/>_" + common[i][0];
				fieldValue = common[i][1];
				fieldType = common[i][2];
				
				if (fieldType === "boolean" || fieldType === "java.util.Boolean"){
					$select = $('form#idUpdateXbrcForm_<s:property/> select#input_' + fieldId);
					if ($select.length != 0){
						// regular select box for true/false values
						$select.children("option.simple").each(function(){
							value = $(this).attr("value");
							if (fieldValue === "" && value === "-1"){
								$(this).attr("selected", "selected");
							} if (fieldValue === "false" && value == "false"){
								$(this).attr("selected", "selected");
							} else if (fieldValue === "true" && value === "true"){
								$(this).attr("selected", "selected");
							}
						});
					}
				} else {
					$input = $('form#idUpdateXbrcForm_<s:property/> input#input_' + fieldId);
					$input.val(fieldValue);
					$input.css("color", INACTIVE_COLOR);
					
					$select = $('form#idUpdateXbrcForm_<s:property/> select#' + fieldId);
					if ($select.length != 0){
						// editable select box
						$optionOne = $select.children("option").first();
						$optionOne.attr("selected","");
					}
				}
			}
			}
			
			for (var j = 0; j < userInput.length; j++){
				$("input#input_<s:property/>_" + userInput[j]).css("color", ACTIVE_COLOR);
			}
		}
		
		</s:iterator>
	}
	
	$("div#idProgress").toggle();
}

function updateXbrcs(currentModel)
{
	model = currentModel;
	
	var valid = true;
	if (model !== "undefined"){
		var form , errorSpan, testedValue, valueType;
		
		<s:iterator var="tab" value="tabs" status="tabIndex">
		
		if (model === "${tab.key}") {
			form = $("form#idUpdateXbrcForm_${tab.key}");	
			<s:iterator var="xbrc" value="value" status="xbrcIndex">
				<s:if test="#xbrcIndex.index == 0">
				<s:iterator var="config" value="value" status="configIndex">
					<s:if test="#configIndex.index == 0">
						form.children().each(function(i){
							if ($(this).attr("type") === "text"){
								errorSpan = $("span#error_${tab.key}_" + $(this).attr('id'));
								testedValue = $(this).attr("value");
								if (errorSpan){
									valueType = $(this).attr("type");
									if (valueType === "boolean" || valueType === "java.lang.Boolean"){
										// select dropdown, value always valid
									} else if (valueType === "java.lang.String"){
										validateString(testedValue, errorSpan, valid, "${config.min}", "${config.max}");
									} else {
										validateNumber(testedValue, errorSpan, valid, "${config.min}", "${config.max}");
									}
								}
							}
						});
						
						if (valid){
							form.submit();
						}
					</s:if>
				</s:iterator>
				</s:if>
			</s:iterator>
		}
		</s:iterator>
	}
	
	$("div#idProgress").toggle();
}

function selectAll(currentModel)
{
	model = currentModel;
	
	$checkboxes = $("input.xbrcSelect_" + model);
	if ($checkboxes.length == 0)
		return false;
	
	$form = $('form#idXbrcCheckChangeForm_' + model);
	if ($form.length == 0)
		return false;
	
	var selectingAll = $("input#selectAll_" + model).prop("checked");
			
	// toggle all the checkboxes and mared them as changed by the selectAll instead of individually
	var selected;
		
	if (selectingAll) {
		$checkboxes.prop('checked', true);
	} else {
		$checkboxes.prop('checked', false);
	}
	$checkboxes.addClass(TOGGLE_BY_SELECT_ALL_CLASS);
		
	// costruct a comma separated list of all xBRCs' names
	selected = $form.children('input')[0];
	
	if (selectingAll)
	{
		// add to the list of selected checkboxes to be sent to the server
		$checkboxes.each(function(){
			$this = $(this);
					
			if (selected === undefined || selected.value === ""){
				selected.value = $this.val();
			} else {
				if (selected.value.indexOf($this.val()) < 0){
					selected.value = selected.value + "," + $this.val();
				}
			}
		});
	}
	else
	{
		// empty the list of selected checkboxes to be sent to the server
		selected.value = "";
	}
			
	// append form data to this ajax post
	$userInputFields = $("form#idUpdateXbrcForm_" + model + " input.userInput");
	var userInput = "";
	$userInputFields.each(function(i){
		$this = $(this);
		userInput = userInput + 
				"<input type='hidden' name='" + $this.attr("name") + 
					"' value='" + $this.val() + 
					"' id='" + $this.prop("id") + "'/>";
	});
	$form.append(userInput);
	
	// show progress
	$("div#idProgress").toggle();
			
	dojo.event.topic.publish('handleXbrcCheckChange_' + model);
	
	return true;
}

function validatePositiveInteger(testedValue, errorSpan, valid){
	if (testedValue.length == 0) {
		errorSpan.hide();
	} else if (isNaN(parseInt(testedValue, 10))) {
		errorSpan.show();
		valid = false;
	} else if (testedValue < 0) {
		errorSpan.show();
		valid = false;
	} else {
		errorSpan.hide();
	}
}

function validateUrl(testedValue, errorSpan, valid){
	if (testedValue.length == 0) {
		errorSpan.hide();
	} else if (!isUrl(testedValue)){
		errorSpan.show();
		valid = false;
	} else {
		errorSpan.hide();
	}
}

function validateIp(testedValue, errorSpan, valid){
	if (testedValue.length == 0){
		errorSpan.hide();
	} else if (testedValue === "localhost"){
		errorSpan.hide();
	} else if (!fnValidateIPAddressV4(testedValue)){
		errorSpan.show();
		valid = false;
	} else {
		errorSpan.hide();
	}
}

function validatePassword(testedValue, errorSpan, valid){
	if (testedValue.length == 0){
		errorSpan.hide();
	} else if (containsSpaces(testedValue)) {
		errorSpan.show();
		valid = false;
	} else {
		errorSpan.hide();
	}
}

function validateNumber(testedValue, errorSpan, valid, min, max){
	if (testedValue.length == 0) {
		errorSpan.hide();
	} else if (isNaN(parseInt(testedValue, 10))) {
		errorSpan.show();
		valid = false;
	} else if (min !== "N/A" && testedValue < min) {
		errorSpan.show();
		valid = false;
	} else if (max !== "N/A" && testedValue > max) {
		errorSpan.show();
		valid = false;
	} else {
		errorSpan.hide();
	}
}

function validateString(testedValue, errorSpan, valid, min, max){
	if (min !== "N/A" && testedValue.length < min) {
		errorSpan.show();
		valid = false;
	} else if (max !== "N/A" && testedValue.length > max) {
		errorSpan.show();
		valid = false;
	} else {
		errorSpan.hide();
	}
}
</script>