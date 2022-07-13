<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div id="idFooter" class="round">
    <div id="idXbrmsLink">
    	<s:if test="xbrcName != null">
    	<div style="float: left;">
          <h3 class="lightText">${xbrcName}&nbsp;</h3>
       </div>
       </s:if>
       <div style="float: left;">
          <h3 class="lightText">${uiVersion}</h3>
       </div>
       <div style="float: left; margin-left:15px">
          <h3 class="lightText">VIP: ${vip}</h3>
       </div>
        <s:if test="xbrmsUrl != null">
           <div style="float: left; clear: left; margin-top: 5px;">
              <s:a href="%{xbrmsUrl}">Go to xBRMS</s:a>
           </div>
        </s:if>
    </div>
</div>
