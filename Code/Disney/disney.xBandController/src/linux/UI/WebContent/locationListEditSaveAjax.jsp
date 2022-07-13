<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<span id="locationListEditAjaxDiv">
<s:if test="hasActionErrors()">
	<div class="lightBackground round shadow orangeBorder orangeText padding">
   		<s:actionerror />
   </div>
   <br>
</s:if>
<s:if test="hasActionMessages()">
	<div class="lightBackground round shadow blueBorder blueText padding">
		<s:actionmessage />
	</div>
	<br>
</s:if>
</span>

<script type="text/javascript">
updateLocationName(${location.id},"${location.name}");
</script>