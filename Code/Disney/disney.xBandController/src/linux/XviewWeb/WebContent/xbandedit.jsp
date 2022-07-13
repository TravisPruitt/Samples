<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/global.css" />
<title>XBand Administration</title>
<script type="text/javascript">

//var XviewHostURL = "http://10.75.3.106:8080/";
var XviewHostURL = "/Xview/";
var cr = "ursula";

var xbands = null;
var xbandIndex = 0;

var request = CreateRequest();

// Create a request object for running our AJAX requests through.
function CreateRequest() {
	try {
		request = new XMLHttpRequest();
	} catch (Failed) {
		try {
			request = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (failed) {
			try {
				request = new ActiveXObject("Microsoft.XMLHTTP");

			} catch (failed) {
				request = false;
			}
		}
	}
	return request;
}


// Get the xbands created by user cr'
function GetXBands() {

	var xBandSearch = XviewHostURL + "xbands?createdBy=" + cr;
	
	if (request == null)
	request = CreateRequest();

	if (request != false) {
		request.open("GET", xBandSearch, true);
		request.onreadystatechange = displayXBands;
		request.send(null);
	}
}

// Get the xBands and display them on the page in the div.
function displayXBands() {
	if (request.readyState = 4) {
		if (request.status == 200) {
			xbands = eval('(' + request.responseText + ')');

			var xBandList = document.getElementById("xBandList");

			xBandList.innerHTML = "";

			if (xbands.length > 0) {
				for ( var i = 0; i <= xbands.length - 1; i++) {
					xBandList.innerHTML += BuildXBandDisplayDiv(xbands[i],
							i);
				}

			}
		}
	}
}

function buildDisplayTable()
{
	
}


function buildXBandDisplayDiv(xband, i)
{
	
}

function saveButtonClick()
{
}

function cancelButtonClick()
{
}

function newButtonClick()
{
}

</script>

</head>
<body onload="refreshXBands()">
	<div id="body">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="menu" />
		
		<div class="inputArea" id="xbandSelector">
		<input type="submit" value="New" class="button blue" />
		
		<div class="xBandList" id="xBandList"></div>
		</div>
		
		<div style="display:none; visibility:hidden;">
		<div><span class="boldLabel">XBand</span></div>
		<div><span class="label">Band ID:</span> <input type="text" id="txtBandId" class="searchTextBox" /></div>
		<div><span class="label">Long Range ID:</span> <input type="text" id="txtLRId" class="searchTextBox" /></div>
		<div><span class="label">Tap ID:</span> <input type="text" id="txtTapId" class="searchTextBox" /></div>
		<div><span class="label">Band Printed Name:</span> <input type="text" id="txtPrintedName" class="searchTextBox" /></div>
		
		<div class="buttonBar">
			<input type="submit" class="button blue" value="Save" onclick="saveButtonClick();" />
			<input type="submit" class="button blue" value="Cancel" onclick="cancelButtonClick();" />
		</div>
		</div>

		<tiles:insertAttribute name="footer" />
	</div>
</body>
</html>