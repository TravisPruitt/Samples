<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="location != null">
	<s:include value="locationEdit.jsp"/>
</s:if>
<s:elseif test="reader != null">
	<s:include value="readerEdit.jsp"/>	
</s:elseif>
<s:else>
	There are no locations or readers configured for this xBRC.
</s:else>
