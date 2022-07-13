<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="bc" uri="/struts-arianna-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                  
<ul class="jq-menu" style="display: none;">
	<s:if test="!isGlobalXbrms()">
		<li class="top" id="home"><a href="home">Home</a></li>
		<s:if test="canAccessAsset('Deny maintenance role')">
       		<li class="top" id="health"><a href="#" style="cursor:text;">Monitor</a>
   			<ul class="shadow">
    			<li><a href="systemhealth" style="font-weight: bold;">Systems Health</a></li>
                <li><a href="readerhealth" style="font-weight: bold;">Readers Health</a></li>
   			</ul>
   			</li>
   			<li class="top" id="configuration"><a href="#" style="cursor:text;">Configure</a>
   			<ul class="shadow">
   				<li><a href="#" style="cursor:text;">xBRC</a>
   				<ul class="shadow">
    				<li><a href="editxbrc" style="font-weight: bold;">Properties</a></li>
                	<li><a href="storedconfig" style="font-weight: bold;">Stored Configurations</a></li>
    			</ul>
    			</li>
    			<li><a href="#" style="cursor:text;">Reader</a>
   				<ul class="shadow">
    				<li><a href="powermanagement" style="font-weight: bold;">Power Schedule</a></li>
    			</ul>
    			</li>
    			<li><a href="#" style="cursor:text;">xBRMS</a>
   				<ul class="shadow">
    				<li><a href="properties" style="font-weight: bold;">Properties</a></li>
    				<s:if test="canAccessAsset('Deny maintenance role')">
	       				<li><a href="parkssetup" style="font-weight: bold;">Topology</a></li>
	       				<li><a href="scheduler" style="font-weight: bold;">Scheduled Tasks</a>
						<ul>
							<li><a href="schedulerlogs" style="font-weight: bold;">Scheduler Logs</a></li>				
						</ul>
						</li>
	    			</s:if>
    			</ul>
    			</li>
   			</ul>
   			</li>
    	</s:if>
	</s:if>
	<s:elseif test="isGlobalXbrms()">
		<li class="top" id="home"><a href="home">Home</a></li>
   		<s:if test="canAccessAsset('Deny maintenance role')">
   			<li class="top" id="health"><a href="#" style="cursor:text;">Monitor</a>
   			<ul class="shadow">
    			<li><a href="systemhealth" style="font-weight: bold;">Systems Health</a></li>
   			</ul>
   			</li>
   			<li class="top" id="configuration"><a href="#" style="cursor:text;">Configure</a>
	   			<ul class="shadow">
	    			<li><a href="#" style="cursor:text;">Reader</a>
	   				<ul class="shadow">
	    				<li><a href="unassignedreaders" style="font-weight: bold;">Assign</a></li>
   						<li><a href="replaceReader" style="font-weight: bold;">Replace</a></li>
	    			</ul>
	    			</li>
	    			<li><a href="#" style="cursor:text;">xBRMS</a>
	   				<ul class="shadow">
	    				<s:if test="canAccessAsset('Deny maintenance role')">
		       				<li><a href="properties" style="font-weight: bold;">Properties</a></li>
		       				<li><a href="parkssetup" style="font-weight: bold;">Topology</a></li>
		       				<li><a href="scheduler" style="font-weight: bold;">Scheduled Tasks</a>
							<ul>
								<li><a href="schedulerlogs" style="font-weight: bold;">Scheduler Logs</a></li>				
							</ul>
							</li>
		    			</s:if>
	    			</ul>
	    			</li>
	   			</ul>
   			</li>
   		</s:if>
   		<s:else>
   			<li class="top" id="configuration"><a href="#" style="cursor:text;">Reader</a>
	   			<ul class="shadow">
	    			<li><a href="unassignedreaders" style="font-weight: bold;">Assign</a></li>
   					<li><a href="replaceReader" style="font-weight: bold;">Replace</a></li>
	    		</ul>
   			</li>
   		</s:else>
	</s:elseif>
	<s:else>
		<li class="top" id="home"><a href="home">Home</a></li>
	</s:else>
	<s:if test="canAccessAsset('Deny maintenance role')">
		<li class="top" id="park"><a href="#" style="cursor:text;">Select Park</a>
			<ul class="shadow">
			<s:if test="isMultiParkConfig()">
	     		<s:iterator var="link" value="servers.entrySet()">
	                <li><a href="${link.key}" style="font-weight: bold;">${link.value}</a></li>
	            </s:iterator>
	   		</s:if>
	     	</ul>
		</li>
	</s:if>
 </ul>