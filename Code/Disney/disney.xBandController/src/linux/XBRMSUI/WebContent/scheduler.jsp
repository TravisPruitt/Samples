<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle">Scheduled Tasks</h2>
<img src="images/help.png" class="clickable" style="position: absolute; top:1em; right:1em; z-index: 1000;" onclick="openHelpDialog();" id="helpLink" title="click to show the help dialog"/>

	<sx:div 
		theme="ajax" 
		href="scheduleritems" 
		executeScripts="true"
		showLoadingText="false"
		showErrorTransportText="true"
		listenTopics="/refreshscheduleritems">
	</sx:div>
	
</div>

<s:form theme="simple" id="schedulerItemForm" action="scheduleritemedit">
	<s:hidden name="itemKey" />
	<s:hidden name="jobClassName" />
</s:form>


<div id="helpDialog">
<h1>Scheduled Tasks</h1> 
<h2>Overview</h2>
<p>
The xBRMS has a built-in scheduler. The scheduler is capable of executing various pre-defined tasks. The Scheduled Tasks page
allows for creating, deleting tasks and modifying their parameters. This includes modifying the task schedule which is expressed 
as a crontab expression.
</p>
<h3>Create New</h3>
Use the "Create New" button to create a new task from a list of pre-defined tasks.
<h3>View Logs</h3>
Use the "View Logs" button to show the page with the task execution logs.
<h3>Task Links</h3>
Click on the task description link to edit the task parameters or to delete the task.
</div>