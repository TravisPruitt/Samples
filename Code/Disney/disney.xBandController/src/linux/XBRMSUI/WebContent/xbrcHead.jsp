<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/home.css" />
<link rel="stylesheet" type="text/css" href="script/jquery-jqplot/jquery.jqplot.css" />
<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />

<!--[if lt IE 9]><script language="javascript" type="text/javascript" src="script/jquery-jqplot/excanvas.js"></script><![endif]-->
<script type="text/javascript" src="script/jquery-jqplot/jquery.jqplot.js"></script>
<script type="text/javascript" src="script/jquery-jqplot/plugins/jqplot.canvasTextRenderer.js"></script>
<script type="text/javascript" src="script/jquery-jqplot/plugins/jqplot.dateAxisRenderer.js"></script>
<script type="text/javascript" src="script/jquery-jqplot/plugins/jqplot.ohlcRenderer.js"></script>
<script type="text/javascript" src="script/jquery-jqplot/plugins/jqplot.highlighter.js"></script>
<script type="text/javascript" src="script/jquery-jqplot/plugins/jqplot.canvasAxisLabelRenderer.js"></script>
<script type="text/javascript" src="script/jquery.timer.js"></script>
<script type="text/javascript" src="script/fg.menu.js"></script>

<link rel="stylesheet" type="text/css" href="css/xbrc.css" />

<script type="text/javascript">
$.jqplot.config.enablePlugins = true;

var tabSelected = 0;

var Graphs = new Array();
var GraphMeta = new Array();
var GraphData = new Array();
//the jqplot graphs fail when empty array is passed
GraphData[0] = 'initial';
GraphData['initial'] = [['06/15/2009 16:00:00', 1, 0, 0.0401]];

var LargeGraph;
var LargeGraphData = new Array();
LargeGraphData[0] = 'initial';
LargeGraphData['initial'] = [['06/15/2009 16:00:00', 1, 0, 0.0401]];
                                                
var startDate, endDate;
var seriesColor = "#14335d";

var largeGraphName = "";
var largeGraphVersion = "";

var perfMetricsTimer;
var readerHealthStatusTimer;
var refreshingPerfMetrics = false;

dojo.event.topic.subscribe("ajaxRefreshReadersStatus", function onAjax(data, type, request)
{
	// the "before" and "load" types are handled someplace elsewhere
	if (type === "before")
		$("form#" + $("div#" + data).attr("formId")).append('<input type="hidden" name="NoAcRedirect" value="1"/>');
	else if (type === "error")
		ErrorDialog.open("<p>Readers health status refresh failed.</p><p><b>Reason:</b> " + request.getResponseHeader("errMsg") + "</p>");
});

function refreshPerfMetricTab(drawGraphs)
{
	if (refreshingPerfMetrics)
		return;
	
	refreshingPerfMetrics = true;
	
	$.ajax({
		url: "perfmetrics.action",
		data:{
			"healthItemId":"${xbrc.item.id}",
			"healthItemIp":"${xbrc.item.ip}",
			"healthItemPort":"${xbrc.item.port}",
			"NoAcRedirect":"NoAcRedirect"
		},
		success: function(response){
			buildDataArray(response.metrics);
			$("#tabs").tabs("option","disabled",false);
			
			if (drawGraphs === undefined || drawGraphs === true)
				drawPerfMetrics();
		},
		error: function(response){
			var status = 500;
			var responseText = "";
			var reason = "unknown";
			
			if (response !== undefined)
			{
				status = response.status;
				responseText = response.responseText;
				reason = response.getResponseHeader("errMsg");
				if ($.trim(reason).length === 0)
					reason = "unknown";
			}
			
			if (status === 401 || responseText.indexOf("Authentication") != -1) {
				location.href = "xbrc.action?id=${xbrc.item.id}";
			}
			else {
				ErrorDialog.open("<p>Failed to refresh performance metrics data.</p><p><b>Reason:</b> " + reason);
			}
		},
		dataType:"json",
		type:"GET",
	});
	
	refreshingPerfMetrics = false;
}

function refreshHealthPage(xbrcId) {
	// only refresh when the Readers Health page is active. Don't refresh when Performance Metrics tab is active.
	var activeTabIndex = $("#tabs").tabs('option','selected');
	
	if (activeTabIndex === 0 && xbrcId !== undefined){
		location.href = "xbrc?id=" + xbrcId + "&tabSelected=" + activeTabIndex;
	}
}

function buildDataArray(metric) {	
	
	if (metric === undefined)
		return;
	
	// rendering multiple small graphs
	//GraphData = new Array();
	GraphData = jQuery.parseJSON(metric);
		
	//start and end date are the same for all graphs
	var graph = GraphData[GraphMeta[0][0]];
	startDate = graph[0][0];
	endDate = graph[graph.length - 1][0];
}

function updateGraphs() {
	for (var i = 0; i < Graphs.length; i++){
		var graphName = GraphMeta[i][0];
		var units;
		Graphs[i].replot({
			clear:true, 
			resetAxes:true, 
			data:[GraphData[graphName]]
		});
			
		if (graphName === largeGraphName)
			units = GraphMeta[i][3];
	}
			
	if (LargeGraph !== undefined && largeGraphName !== undefined && largeGraphName !== "")
	{
		LargeGraph.replot({
			clear:true, 
			resetAxes:true, 
			data:[GraphData[largeGraphName]], 
			title: largeGraphName,
			highlighter: {
				formatString:'<table class="jqplot-highlighter"> \
					<tr><td>date:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
					<tr><td>max:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
					<tr><td>min:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
				    <tr><td>mean:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
				    <tr><td>units:</td><td style=\'color: ' + seriesColor + '\'>' + units + '</td></tr></table>'
			}
		});
	}
}

function showDates(){
	$("#status #statusMenu h3 span:first").html(startDate + " UTC");
	$("#status #statusMenu h3 span:last").html(endDate + " UTC");
}

function drawPerfMetrics()
{
	updateGraphs();
	
	showDates();
}

function initializeGraphsMetadata() {
	
	<s:iterator var="metadata" value="metricsMeta" status="row">
	GraphMeta[<s:property value="#row.index"/>] = new Array();
	GraphMeta[<s:property value="#row.index"/>][0] = "${value.name}";
	GraphMeta[<s:property value="#row.index"/>][1] = "${value.displayName}";
	GraphMeta[<s:property value="#row.index"/>][2] = "${value.version}";
	GraphMeta[<s:property value="#row.index"/>][3] = "${value.type}";
	GraphMeta[<s:property value="#row.index"/>][4] = "${value.description}";
	</s:iterator>
}

function initializeGraphs() {
	
	// prepare all the small graphs
	for (var i = 0; i < GraphMeta.length; i++)
	{
		var graphName = GraphMeta[i][0];
		var graphDisplayName = GraphMeta[i][1];
		
		var graphUnitLabel = GraphMeta[i][3];
		if (graphUnitLabel === undefined || graphUnitLabel === "")
			graphUnitLabel = "no data";
		
		if (GraphMeta[i][3] === "seconds") {
			var graphTickInterval = "1 minute";
		} else if (GraphMeta[i][3] === "milliseconds") {
			graphTickInterval = "1 second";
		} else {
			graphTickInterval = "";
		}
	
		Graphs[i] = $.jqplot('GraphContainer_' + graphName,[GraphData['initial']],{
	    // use the y2 axis on the right of the plot
	    //rather than the y axis on the left
	    title: {
	    	text:graphDisplayName,
	    	show: true,
	    	color: "#14335d"
	    },
	    seriesDefaults:{yaxis:'y2axis'},
	    series: [{
	    	renderer:$.jqplot.OHLCRenderer, 
	    	redererOptions:{hlc:true},
	    	color: seriesColor,
	    	shadow: false,
	    }],
	    axes: {
			xaxis: {
		        renderer:$.jqplot.DateAxisRenderer,
		        tickOptions:{formatString:'%b %e %H:%M'}, 
		        // For date axes, we can specify ticks options as human 
		        // readable dates.  You should be as specific as possible, 
		        // however, and include a date and time since some 
		        // browser treat dates without a time as UTC and some 
		        // treat dates without time as local time.
		        // Generally, if  a time is specified without a time zone,
		        // the browser assumes the time zone of the client.
		        min: startDate,
		        max: endDate,
		        tickInterval: graphTickInterval,
		        padMin: 1.03,
		        padMax: 1.03,
		      },
		    y2axis: {
		  	  	tickOptions:{formatString:'%4.1f'},	//controls how formatString in highlighter shows these numbers
				label: graphUnitLabel,
		  	  	padMin: 0,
				labelRenderer: $.jqplot.CanvasAxisLabelRenderer
			},
		},
		highlighter: {
		      show: true,
		      showMarker:false,
		      tooltipAxes: 'xy',
		      yvalues: 3,
		      // You can customize the tooltip format string of the highlighter
		      // to include any arbitrary text or html and use format string
		      // placeholders (%s here) to represent x and y values.
		      tooltipLocation: "nw",
		      fadeTooltip: true,
		      tooltipFadeSpeed: "slow",
		      formatString:'<table class="jqplot-highlighter"> \
		      <tr><td>date:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>max:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>min:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>mean:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>units:</td><td style=\'color: ' + seriesColor + '\'>' + GraphMeta[i][3] + '</td></tr></table>'
		},
		grid: {
			drawBorder: false,
			gridLineColor: "#DDDDDD",
		}
	  });
	}
	
	// prepare the large graph
	LargeGraph = $.jqplot('largeGraphContainer',[LargeGraphData['initial']],{
	    // use the y2 axis on the right of the plot
	    //rather than the y axis on the left
	    title: {
	    	text:"initializing...",
	    	show: true,
	    	color: "#14335d"
	    },
	    seriesDefaults:{
	    	yaxis:'y2axis',
	    },
	    series: [{
	    	renderer:$.jqplot.OHLCRenderer, 
	    	redererOptions:{hlc:true},
	    	color: seriesColor,
	    }],
	    axes: {
			xaxis: {
		        renderer:$.jqplot.DateAxisRenderer,
		        tickOptions:{formatString:'%b %e %H:%M'}, 
		        // For date axes, we can specify ticks options as human 
		        // readable dates.  You should be as specific as possible, 
		        // however, and include a date and time since some 
		        // browser treat dates without a time as UTC and some 
		        // treat dates without time as local time.
		        // Generally, if  a time is specified without a time zone,
		        // the browser assumes the time zone of the client.
		        min: startDate,
		        max: endDate,
		        tickInterval: "1 minute",
		        padMin: 1.03,
		        padMax: 1.03,
		      },
		    y2axis: {
		  	  	tickOptions:{formatString:'%5.1f'},	//controls how formatString in highlighter shows these numbers
		  	  	padMin: 0,
		    },
		},
		highlighter: {
		      show: true,
		      showMarker:false,
		      tooltipAxes: 'xy',
		      yvalues: 3,
		      // You can customize the tooltip format string of the highlighter
		      // to include any arbitrary text or html and use format string
		      // placeholders (%s here) to represent x and y values.
		      tooltipLocation: "s",
		      fadeTooltip: true,
		      tooltipFadeSpeed: "slow",
		      formatString:'<table class="jqplot-highlighter"> \
		      <tr><td>date:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>max:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>min:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>mean:</td><td style=\'color: ' + seriesColor + '\'>%s</td></tr> \
		      <tr><td>units:</td><td style=\'color: ' + seriesColor + '\'>' + "" + '</td></tr></table>'
		},
		grid: {
			drawBorder: false,
			gridLineColor: "#DDDDDD",
			
		}
	  });
}

function identifyReader(element, readerId) {
	if (readerId !== undefined){
		document.forms.idReaderIdentifyForm.readerId.value = readerId;
		dojo.event.topic.publish('handleIdentifyReader');
	}
}
function restartReader(element, readerId) {
	if (readerId !== undefined){
		document.forms.idReaderRestartForm.readerId.value = readerId;
		dojo.event.topic.publish('handleRestartReader');
	}
}
function rebootReader(element, readerId) {
	if (readerId !== undefined){
		document.forms.idReaderRebootForm.readerId.value = readerId;
		dojo.event.topic.publish('handleRebootReader');
	}
}

function refreshPage() {
    location.href = "systemhealth.action";
}
      
$(document).ready(function()
{
    dojo.event.topic.subscribe("/accessDenied", refreshPage);
    
    // timer that refreshes the per metrics tab
    perfMetricsTimer = $.timer(function(){
    	refreshPerfMetricTab();
    });
    perfMetricsTimer.set({time:30000,autostart:false});
    
    // timer that refreshes the readers' health status tab
    readerHealthStatusTimer = $.timer(function(){
    	dojo.event.topic.publish('refreshreadersstatus');
    });
    readerHealthStatusTimer.set({time:5000,autostart:false});
    
	$("#tabs").tabs({
		show: function(event, ui) {
			if (ui.index === 1) {
				//pull performance data when that tab is selected
				drawPerfMetrics();
				
				readerHealthStatusTimer.pause();
				
				if (!perfMetricsTimer.isActive)
					perfMetricsTimer.play();
			} else {
				//pull readers' health status when that tab is selected
				dojo.event.topic.publish('refreshreadersstatus');
				
				perfMetricsTimer.pause();
				
				if (!readerHealthStatusTimer.isActive)
					readerHealthStatusTimer.play();
			}
		},
		disabled: [1],
	});
	
	<s:iterator var="locInfo" value="rlInfo.readerlocationinfo" status="statLoc">
		<s:iterator var="readerInfo" value="readers"> 
		$('#menu-${locInfo.id}_${id}').menu({
			content: $('#content-${locInfo.id}_${id}').html(),		
			maxHeight: 180
		});
		</s:iterator>
	</s:iterator>
	
	initializeGraphsMetadata();
	
	initializeGraphs();
	
	// a popup dialog for displaying a large graph
	$("#largeGraph").dialog({
		height:500,
		width:800,
		autoOpen:false,
		hide: "slide",
		modal: true,
		resizable: true,
		open: function(event, ui) {
			// set the title
			var dialogTitle = largeGraphName;
			for (var i = 0; i < GraphMeta.length; i++) {
				if (GraphMeta[i][0] === largeGraphName) {
					dialogTitle = GraphMeta[i][1];
				}
			}
			$(this).dialog( "option", "title", dialogTitle );
			
			updateGraphs();
		},
		close: function(event, ui) {
			largeGraphName = "";
		}
	});
	
	tabSelected = ${tabSelected};
	
	// improve user experience by pre-populating the perf metrics tab
	var drawGraphs = false;
	refreshPerfMetricTab(drawGraphs);
});

function largeGraph(perfMetricName, perfMetricVersion) {
	if (perfMetricName === undefined || perfMetricName === "" ||
			perfMetricVersion === undefined || perfMetricVersion === "")
	{
		return;
	}
	
	largeGraphName = perfMetricName;
	largeGraphVersion = perfMetricVersion;
	
	$largeGraphDialog = $("#largeGraph");
	if ($largeGraphDialog.dialog("isOpen") == false) {
		$largeGraphDialog.dialog("open");
	}
}
</script>