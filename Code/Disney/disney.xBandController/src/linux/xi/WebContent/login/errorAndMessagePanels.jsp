<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<s:if test="hasActionErrors()">
	<div class="lightBackground round shadow orangeBorder orangeText padding topMargin">
   		<s:actionerror />
   </div>
</s:if>
<s:if test="hasActionMessages()">
	<div class="lightBackground round shadow blueBorder blueText padding topMargin">
		<s:actionmessage />
	</div>
</s:if>