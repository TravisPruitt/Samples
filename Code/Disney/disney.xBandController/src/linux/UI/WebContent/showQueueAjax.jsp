<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="guestGraph">
<s:iterator value="guestList" var="guest">
	<img class="guest" src='images/<s:property value="image"/>' style='top:<s:property value="topPos"/>px; left:<s:property value="leftPos"/>px;' />
</s:iterator>
</div>

<script type="text/javascript">
showtime();
</script>