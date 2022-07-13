<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="idBody" class="round lightBackground shadow padding">
    <h2 class="pageTitle">xBRMS Status Messages</h2>

    <div>
        <s:if test="problems.size() > 0">
            <ul style="list-style: none; padding-left: 0; margin-left: 0">
                <s:iterator var="problem" value="problems">
                    <li>
                        ${problem.timestamp}: <span class="blueText">${problem.message}</span>
                    </li>
                    <br>
                </s:iterator>
            </ul>
            <div class="clear"></div>
            <s:form action="xbrmsstatusclear">
                <s:submit type="button" class="submit">Clear</s:submit>
            </s:form>
        </s:if>
        <s:else>
            There are no new status messages.
        </s:else>
    </div>
</div>