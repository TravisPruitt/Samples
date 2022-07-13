<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@taglib prefix="s" uri="/struts-tags" %>

    <div id="idBody" class="round lightBackground shadow padding">
        <div id="xbrmsSetupForm" class="container round" style="width: 930px;">

            <br> <br> <br> <br>

            <div align="center">
                <div style="color: #14335d; width: 400px;"/>
                   <h3>You have successfully authenticated to xConnect!</h3>
                   <p>
                       Unfortunately, the gateway cannot take you to the application
                       you are trying to access. The problem is likely caused by the use
                       of the "Back" button in your web browser.
                   </p>
                   <h3>To navigate to the xConnect application enter its URL in the address field of the browser.</h3>
               </div>
           </div>
           <div id="idLogoutLinks" align="center" style="margin-top: 40px; width: 930px; font-size: 120%;">
              <s:if test="acHomeUrl != null">
                 <s:a id="acHomeUrl" href="%{acHomeUrl}">Home</s:a>
              </s:if>
           </div>
       </div>
   </div>
