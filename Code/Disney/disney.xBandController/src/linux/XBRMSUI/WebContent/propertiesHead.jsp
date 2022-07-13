<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="script/NetworkAddress.js"></script>
<script type="text/javascript" src="script/InputSanitizer.js"></script>
<script type="text/javascript" src="script/validation_utils.js"></script>

<style>
#idBody {position: relative;}	
#xbrmsPropertiesForm dt {position: relative;}
.infoImage {
	position:absolute; top:0px; right:0px;
	cursor: pointer;
}
</style>

<script type="text/javascript">
$(document).ready(function(){
	<s:if test="!canAccessAsset('Editable Content')">
	$("input")
		.attr("readonly","readonly")
		.addClass("lightGreyBackground")
		.attr("title", "<s:text name='read.only.page'/>")
		.click(function(event){
			openInfoDialog($("#readOnlyInfoDialog"));
		});
	$("select").attr("disabled","disabled");
	$(":button").hide();
	</s:if>
});

function saveProperties()
{
	var valid = true;
	try {
		$form = $("form#idUpdatePropertiesForm");
		
		// sanitize input
		$form.find("input:text").each(function(){
			$(this).val(InputSanitizer.sanitizeText($(this).val()));
		});
		
		// positive integer within the upper Java int range
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_httpConnectionTimeout_msec");
		$errorSpan = $form.find("span#xbrmsConfig_httpConnectionTimeout_msec");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaInt($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_jmsMessageExpiration_sec");
		$errorSpan = $form.find("span#xbrmsConfig_jmsMessageExpiration_sec");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaLong($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer no larger than 99
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_statusThreadPoolCoreSize");
		$errorSpan = $form.find("span#xbrmsConfig_statusThreadPoolCoreSize");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidIntWithinRange($field.val(),0,999)){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer no larger than 99
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_statusThreadPoolMaximumSize");
		$errorSpan = $form.find("span#xbrmsConfig_statusThreadPoolMaximumSize");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidIntWithinRange($field.val(),0,999)){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer no larger than 9
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_statusThreadPoolKeepAliveTime");
		$errorSpan = $form.find("span#xbrmsConfig_statusThreadPoolKeepAliveTime");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidIntWithinRange($field.val(),0,9)){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer no larger than 999999
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_masterPronouncedDeadAfter_sec");
		$errorSpan = $form.find("span#xbrmsConfig_masterPronouncedDeadAfter_sec");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidIntWithinRange($field.val(),0,999999)){
				valid = false;
				$errorSpan.show();
			}
		}
	
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_auditConfig_keepInCacheEventsMax");
		$errorSpan = $form.find("span#auditConfig_keepInCacheEventsMax");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaLong($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer no larger than 999999
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_assignedReaderCacheRefresh_sec");
		$errorSpan = $form.find("span#xbrmsConfig_assignedReaderCacheRefresh_sec");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidIntWithinRange($field.val(),0,999999)){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_auditConfig_keepInGlobalDbDaysMax");
		$errorSpan = $form.find("span#auditConfig_keepInGlobalDbDaysMax");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaInt($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_auditConfig_pullIntervalSecs");
		$errorSpan = $form.find("span#auditConfig_pullIntervalSecs");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaLong($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_auditConfig_level");
		$errorSpan = $form.find("span#auditConfig_level");
		var validInput = new Array("AUDIT_FAILURE", "AUDIT_SUCCESS", "FATAL", "ERROR", "WARN", "INFO");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (validInput.indexOf($field.val()) < 0){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_ksExpireLogonDataAfterDays");
		$errorSpan = $form.find("span#xbrmsConfig_ksExpireLogonDataAfterDays");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaInt($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer within the upper Java long range
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_ksConnectionToSecs");
		$errorSpan = $form.find("span#xbrmsConfig_ksConnectionToSecs");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidPositiveJavaInt($field.val())){
				valid = false;
				$errorSpan.show();
			}
		}
		
		// positive integer no larger than 999999
		$field = $form.find("input#idUpdatePropertiesForm_xbrmsConfig_unassignedRreaderCacheCleanup_sec");
		$errorSpan = $form.find("span#xbrmsConfig_unassignedRreaderCacheCleanup_sec");
		if ($field.length > 0 && $errorSpan.length > 0){
			if (!isValidIntWithinRange($field.val(),0,999999)){
				valid = false;
				$errorSpan.show();
			}
		}
		
		if (valid){
			$form.submit();
		}
	} catch (exception){
		Debugger.log(exception);
	} finally {
		$form = null;
		$errorSpan = null;
		$field = null;
	}
}
</script>
