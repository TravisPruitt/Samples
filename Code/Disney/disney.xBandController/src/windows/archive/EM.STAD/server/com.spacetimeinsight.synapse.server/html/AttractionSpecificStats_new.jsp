<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=us-ascii">
<style>
<!--
body
	{text-align:center;
	background-color:#14335D}
.viewTable
	{background-color:#14335D;
	color:white;
	font-size:100%;
	margin-left:auto;
	margin-right:auto}
.viewTitle
	{font-family:"Lucida Console","Times New Roman",Times,serif;
	font-size:2em;
	text-align:center}
.viewTitle1
	{font-family:"Lucida Console","Times New Roman",Times,serif;
	font-size:2.5em;
	text-align:center;
	font-weight:bold}
.viewTextBig
	{font-family:"Lucida Console","Times New Roman",Times,serif;
	font-size:3.5em;
	text-align:center;
	font-weight:bold}
.viewTextBig1
	{font-family:"Lucida Console","Times New Roman",Times,serif;
	font-size:2.5em;
	text-align:center;
	font-weight:bold}
.viewTextSmall
	{font-family:"Lucida Console","Times New Roman",Times,serif;
	font-size:1.5em;
	text-align:center}
.viewSpace
	{width:1px}
-->
</style>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<% String attractionName = request.getParameter("attractionName"); %>
	<script type="text/javascript" src="jquery-1.6.2.min.js"></script>
     <script>
     
    function returnURL()
    {
    	//needs to be externalised and fetched from STAS dynamically
	var server = "em-esb.synapsedev.com:8080";
    	var rendererURL = "http://"+server+"/magma/XMLDataRenderer?xmldatatype=com.swedishchef.esm.stas.latest.data.HTMLCountRenderer&attractionName="+"<%=attractionName%>";
    	return rendererURL;
    }
	function getData() 
      {
   			$.ajaxSetup({ cache: false });
			$.ajax({
				type: "GET",
				url: returnURL(),
				dataType: "xml",
				
				success: function(xml) 
				{
					var counter = 0;					
					var childnodes = xml.documentElement.childNodes;
					var  values = [];
					for(var i=0; i<childnodes.length; i++)
					{
						temp_name = xml.documentElement.childNodes[i].getAttribute("name");
						temp_value = xml.documentElement.childNodes[i].getAttribute("value");
												
						values[i] = temp_value;
					}
					
					
					

					var STANDBY_ARRIVAL_RATE = 0;
					var STANDBY_XPASS_TOTAL_COUNT = 1;
					var STANDBY_GUESTS_SERVED_COUNT = 2; 
					var STANDBY_AVERAGE_WAIT_COUNT = 3; 
					var XPASS_COUNT = 4; 
					var XPASS_AVERAGE_WAIT_COUNT = 5; 
					var XPASS_ARRIVAL_RATE= 6;
					var XPASS_ESTIMATED_WAIT=7;
					var XPASS_GUESTS_SERVED_COUNT= 8;
					var STANDBY_ESTIMATED_WAIT=9;
					var STANDBY_COUNT= 10;

					var average_wait_xpass = Math.round(values[XPASS_AVERAGE_WAIT_COUNT]);
					var average_wait_standby = Math.round(values[STANDBY_AVERAGE_WAIT_COUNT]);

					document.getElementById('guestCount').innerHTML = values[STANDBY_XPASS_TOTAL_COUNT];
					document.getElementById('xpCount').innerHTML = values[XPASS_COUNT];
					document.getElementById('xpWait').innerHTML = average_wait_xpass;
					document.getElementById('sbCount').innerHTML = values[STANDBY_COUNT];					
					document.getElementById('sbWait').innerHTML = average_wait_standby;
					document.getElementById('sbEstWait').innerHTML = values[STANDBY_ESTIMATED_WAIT];
					document.getElementById('xpEstWait').innerHTML = values[XPASS_ESTIMATED_WAIT];

					

				}
			});
		}
      setInterval("getData()",5000);
     </script>
</head>
<body>



<table class="viewTable">
<tbody>
<tr>
<td class="viewTitle1" colspan="2">Guests Served Today:</td>
<td class="viewTextBig">
<div id="guestCount">...</div>
</td>
</tr>
<tr>
<td colspan="3">&nbsp;</td>
</tr>
<tr>
</tr>
<tr>
<td class="viewTitle1">Queue Metrics</td>
<td class="viewTitle">xPASS</td>
<td class="viewTitle">StandBy</td>
</tr>
<tr>
<td colspan="3">&nbsp;</td>
</tr>
<tr>
<td class="viewTextSmall">Guest Count</td>
<td class="viewTextBig">
<div id="xpCount">...</div>
</td>
<td class="viewTextBig">
<div id="sbCount">...</div>
</td>
</tr>
<tr>
<td colspan="3">&nbsp;</td>
</tr>
<tr>
<td class="viewTextSmall">Recent Wait<br>
(minutes)</td>
<td class="viewTextBig">
<div id="xpWait">...</div>
</td>
<td class="viewTextBig">
<div id="sbWait">...</div>
</td>
</tr>
<tr>
<td colspan="3">&nbsp;</td>
</tr>
<tr>
<td class="viewTextSmall">Current Wait<br>
Estimate<br>
(minutes)</td>
<td class="viewTextBig">
<div id="xpEstWait">...</div>
</td>
<td class="viewTextBig">
<div id="sbEstWait">...</div>
</td>
</tr>
</tbody>
</table>
</body>
</html>
