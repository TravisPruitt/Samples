<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@taglib prefix="s" uri="/struts-tags" %>

    <div id="idBody" class="round lightBackground shadow padding">
        <div id="xbrmsSetupForm" class="container round" style="width: 900px;">

            <br> <br> <br> <br> <br> <br> <br>

            <div align="center">
                <div style="color: #14335d; "/>
                   <h3>You are logged out of xConnect.</h3>
               </div>
           </div>
           <div id="idLogoutLinks" align="center" style="margin-top: 40px; width: 900px; font-size: 120%;">
              <s:if test="acHomeUrl != null">
                 <s:a id="acHomeUrl" href="%{acHomeUrl}">Home</s:a>
              </s:if>
              <s:if test="acReturnUrl != null">
                 <s:a id="acReturnUrl" href="%{acReturnUrl}">Back</s:a>
              </s:if>
           </div>
       </div>

    <% session.invalidate(); %>

   </div>
