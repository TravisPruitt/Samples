<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
    <div id="xbrmsSetupForm" class="container round" style="width: 900px;">

        <br>
        <br>
        <br>
        <br>
        <br>
        <br>

        <div align="center">
            <s:form action="login" method="post">
                <s:select labelSeparator="" label="Directory" list="directories" name="directory"/>
                <s:textfield id="idUserName" labelSeparator="" label="User name" name="username"></s:textfield>
                <s:password labelSeparator="" label="Password" name="password"></s:password>
                <s:submit value="Login"></s:submit>
            </s:form>
            <br>

            <div style="color: red">
                <s:property value="errorMsg"/>
            </div>
        </div>
    </div>
</div>

<script
    type="text/javascript"
    language="javascript">
    var uName = document.getElementById("idUserName");
    uName.focus();
    uName.select();
</script>