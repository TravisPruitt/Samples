<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<div id="idBody">

<div id="schedulerItemPage" class="round lightBlueBackground shadow padding">

<h2 class="pageTitle">Scheduler Task - ${item.metadata.name}</h2>

<table>
<tr><td id="schedulerItem">
<div class="fieldDivider">Task Definition</div>
<s:form id="schedulerItemAjaxForm" action="scheduleritemedit" theme="simple" method="post"><div>
	<input type="hidden" name="itemKey" value="${item.itemKey}"/>
	<s:hidden name="jobClassName"/>
	
	<dl>	
		<dt><s:checkbox name="item.enabled" fieldValue="true" label="Enable this task (task will not run if unchecked)"></s:checkbox>Enabled (task will not run if unchecked)</dt>
		<dd>			
		</dd>
			
		<dt><label><s:text name="item.description"/></label></dt>
			<s:fielderror>
				<s:param>description</s:param>
			</s:fielderror>
		<dd>
			<span class="fieldError" id="itemDescriptionError"><s:text name="item.description.error"/></span>
			<s:textfield name="item.description"/>
		</dd>

		<dt class="infoImageContainer">
			<label><s:text name="item.schedulingExpression"/></label>
			<s:fielderror>
				<s:param>shcedulingExpression</s:param>
			</s:fielderror>
			<img class="infoImage"
				 title="
Crontab expression that controls the task schedule in the following format.
* * * * * * *
|  |  |  |  |  |  |_ year
|  |  |  |  |  |__ day of week (1 - 7) (1 to 7 are Sunday to Saturday, or use names)
|  |  |  |  |___ month (1 - 12)
|  |  |  |_____ day of month (1 - 31)
|  |  |______ hour (0 - 23)
|  |________ min (0 - 59)
|__________ sec (0-59)"
				 src='images/info_16.png'
				 onclick="openInfoDialogMsg('schedulingExpressionHelp')"/> 
		</dt>
		<dd>
			<s:textfield name="item.schedulingExpression"/>
		</dd>
	</dl>
		
	<div class="fieldDivider">Task Parameters</div>				
	<dl>		
		<s:iterator var="param" value="item.parameters">
		<dt class="infoImageContainer">
		<label><s:text name="metadata.displayName"/><s:if test="metadata.type.toString() == 'ENVPROPVALUE'"> (env prop key)</s:if></label>
		<s:fielderror>
			<s:param>${htmlSafeName}</s:param>
		</s:fielderror>
		<img class="infoImage"
			 title="<s:text name='metadata.description'/>"
			 src='images/info_16.png'
			 onclick="openInfoDialog($(this))"/>
		</dt> 
		<dd id="itemParameters">
			<s:if test="metadata.type.toString() == 'CHOICE'">
				<span class="fieldError">You must provide value for this parameter</span>					
				<select name="${htmlSafeName}" id="${htmlSafeName}">
					<s:iterator var="choice" value="metadata.choices">
						<option value="${choice}" <s:if test="value == #choice">selected</s:if>>${choice}</option>
					</s:iterator>
				</select>
			</s:if>
			<s:else>
				<span class="fieldError">You must provide value for this parameter</span>
				<input type="text" name="${htmlSafeName}" id="${htmlSafeName}" value="${value}"/>
			</s:else>
		</dd>
		</s:iterator>
	</dl>
	
	<div class="right" style="width:100%;"> 
		<input type="reset" onclick="showSchedulerItems();" style="float: right" value="Cancel"/>
		<s:submit method="saveItem" style="float: right" value="Save"/>		
		<s:if test="jobClassName == ''"><s:submit method="deleteItem" onclick="return confirmDeleteSchedulerTask();" style="float: left; color: red" value="Delete"/></s:if>			
	</div>
</div>
</s:form>
</td>

<td id="schedulerItemHelp">
	<div class="fieldDivider">Task Help</div>
	<h1>${item.metadata.name}</h1>
	<p>
		<label>Class Name</label> ${itemDisp.shortJobClassName}
	</p>
	<p>
		${item.metadata.longHtmlDescription}
	</p>

	<div class="fieldDivider">Crontab Expression Help</div>
	<br>
	The Crontab Expression field controls the task schedule. It is of the following format. 
<pre>
* * * * * * *
| | | | | | |_ year
| | | | | |___ day of week (1 - 7) (Sun-Sat)
| | | | |_____ month (1 - 12)
| | | |_______ day of month (1 - 31)
| | |_________ hour (0 - 23)
| |___________ min (0 - 59)
|_____________ sec (0 - 59)
</pre>

<a href="crontabhelp.html" target="crontabHelp">more...</a>

</td>
</tr>
</table>

</div>
</div>

<div style="display: none" id="schedulingExpressionHelp" >
<pre>
Crontab expression that controls the task schedule in the following format.
*  *  *  *  *  * *
|  |  |  |  |  | |_ year
|  |  |  |  |  |___ day of week (1 - 7) (1 to 7 are Sunday to Saturday, or use names)
|  |  |  |  |______ month (1 - 12)
|  |  |  |_________ day of month (1 - 31)
|  |  |____________ hour (0 - 23)
|  |_______________ min (0 - 59)
|__________________ sec (0 - 59)
</pre>
</div>

<div id="helpDialog">
<h1>Scheduler Task Help</h1> 
<h2>Overview</h2>
<p>
A Scheduler Task defines a job that is executed by the xBRC scheduler according to the schedule defined by the crontab expression.
Coming up with an appropriate crontab expression may be a bit challenging for those not already familiar with crontab. The Scheduler Task
page shows some crontab help as well as a link for more help on defining crontab expressions including many examples.  
</p>
<p>
Each scheduler task type is pre-defined by the xBRC. It consists of a Java class that runs the task and a set of parameters that control the task execution.
Multiple tasks of the same type may be created, but care must be taken to ensure that these tasks will not run concurrently, unless a task does allow
for concurrent execution. For example, any database cleanup tasks should not be scheduled to run concurrently. 
</p>
<p>
When the "Save Changes" button is pressed the modifications on the Scheduler Task page take immediate effect. If a scheduler task is currently 
running while changes are made then the task will be aborted. It is best to make task modifications when the task is not running.
</p>
<h3> Enabled</h3>
A task can be enabled or disabled by checking the "Enabled" checkbox. When disabled the task will not run.  
</div>

