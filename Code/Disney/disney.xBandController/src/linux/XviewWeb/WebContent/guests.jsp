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
<title>Guest Administration</title>
<script type="text/javascript">

//var XviewHostURL = "http://10.75.3.106:8080/";
var XviewHostURL = "/Xview/";
var cr = "ursula";

var guests = null;
var guestIndex = 0;

var request = CreateRequest();

var JSON = JSON || {};  

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

function refreshGuests()
{
	GetGuests();
}


// Get the xbands created by user cr'
function GetGuests() {

	var guestSearch = XviewHostURL + "guests/";
	
	if (request == null)
	request = CreateRequest();

	if (request != false) {
		request.open("GET", guestSearch, true);
		request.onreadystatechange = displayGuests;
		request.send(null);
	}
	else
		{
			alert('sorry, request not created..')
		}
}


function displayGuests()
{
	if (request.readyState = 4) {
		if (request.status == 200) {
			guests = eval('(' + request.responseText + ')');
		}
	}

	var guestList = document.getElementById("guestList");

	guestList.innerHTML = buildGuestDisplayTable(guests, guestList);
}

function buildGuestDisplayTable(g, displayDiv)
{
	var table = "<div class='displayTable' id='guestTable'>";
	 table += "<div class='tableHeader'>";
	 table += "<div class='columnHeader'>Name</div>";
	 table += "<div class='columnHeader'>Address</div>";
	 table += "</div>";

	 table += "<div class='tableRows'>";
	 for(var i = 0; i <= g.length -1; i++)
		 {
		 	table += buildGuestDisplayDiv(g[i], i);
		 }	 
	 
	 table += "</div>";
	 table += "</div>";

	 return table;
}

function buildGuestDisplayDiv(guest, i)
{	
	var guestName = "";
	var address1 = "";
	var address2 = "";
	var city = "";
	var state = "";
	var zip = "";
	var countryCode = "";
	

	if (guest.firstName != null)
		{
			guestName += guest.firstName + " ";
		}

	if (guest.lastName != null)
		{
			guestName += guest.lastName;
		}


	if (guest.address1 != null)
		{
			address1 = guest.address1;
		}

	if (guest.address2 != null)
		{
			address2 = guest.address2;
		}

	if (guest.city != null)
		{
			city = guest.city;
		}

	if (guest.state != null)
		{
			state = guest.state;
		}

	if (guest.zip != null)
		{
			zip = guest.zip;
		}

	if (guest.countryCode != null)
		{
			countryCode = guest.countryCode;
		}
	
	
	var row = "<div class='tableRow'>";
	row += "<div class='tableCell'><span>" + guestName + "</span></div>";
	row += "<div class='tableCell'>" + address1 + '</br>' + address2 + '</br>' + city + ' ' + state + ' ' + zip + ' ' + countryCode + "</div>";
	 

	row += "</div>";

	return row;
}

function saveButtonClick()
{
}

function cancelButtonClick()
{
}

function showDiv(id)
{
	var div = document.getElementById(id);
	if (div != null)
		{
			div.style.display = "block";
			div.style.visibility = "visible";
		}
}

function hideDiv(id)
{
	var div = document.getElementById(id);
	if (div != null)
		{
			div.style.display = "none";
			div.style.visibility = "hidden";
		}
}

function newButtonClick()
{
	showDiv('guestEditOverlay');
}

function saveButtonClick()
{
	hideDiv('guestEditOverlay');
	var jsonGuest = saveGuestObject();

	sendGuestObject(jsonGuest);
	
	refreshGuests();
}



function cancelButtonClick()
{
	hideDiv('guestEditOverlay');
}


function saveGuestObject()
{

	var guest = {
	firstName : document.getElementById("txtFirstName").value,
	lastName : document.getElementById("txtLastName").value,
	city : document.getElementById("txtCity").value,
	state : document.getElementById("txtState").value,
	zip : document.getElementById("txtZip").value,
	countryCode : document.getElementById("txtCountryCode").value,
	XBMSId : 0,
	createdBy : "DEMO2"
	};
	
	var toSend = JSON.stringify(guest);
	return toSend;

}

function sendGuestObject(guest)
{
	try
	{
	if (request == null)
		{
			request = createRequest();
		}

	var guestPost = XviewHostURL + "guests/";
	
	request.open("POST", guestPost, true);
	request.setRequestHeader("content-type", "application/json");
	//request.setRequestHeader("content-length", guest.length);
	//request.setRequestHeader("connection", "close");

	request.onreadystatechange = sendGuestComplete;
alert('sending request');
	request.send(guest);
	}
	catch (exception)
	{
		alert(exception);
	}


}

function sendGuestComplete()
{
	//alert('something came back...')
	if (request.readyState = 4) {
		alert(request.status);
		if (request.status == 200) {
		}

			alert ("saved.");
	}
}



// implement JSON.stringify serialization  
JSON.stringify = JSON.stringify || function (obj) {  
    var t = typeof (obj);  
    if (t != "object" || obj === null) {  
        // simple data type  
        if (t == "string") obj = '"'+obj+'"';  
        return String(obj);  
    }  
    else {  
        // recurse array or object  
        var n, v, json = [], arr = (obj && obj.constructor == Array);  
        for (n in obj) {  
            v = obj[n]; t = typeof(v);  
            if (t == "string") v = '"'+v+'"';  
            else if (t == "object" && v !== null) v = JSON.stringify(v);  
            json.push((arr ? "" : '"' + n + '":') + String(v));  
        }  
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");  
    }  
};  

</script>

</head>
<body onload="refreshGuests()">
	<div id="body">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="menu" />
		
		<div class="inputArea" id="guestSelector">
		<input type="submit" value="New" class="button blue" onclick="newButtonClick();" id="btnNew" />
			<div class="guestList" id="guestList"></div>
		</div>
		


		<tiles:insertAttribute name="footer" />
	</div>
	
		<div class="overlay" id="guestEditOverlay">
		<div id="guestEditPanel">
		<div><span class="boldLabel">Guest</span></div>
		<div><span class="label">First Name:</span> <input type="text" id="txtFirstName" class="searchTextBox" /></div>
		<div><span class="label">Last Name:</span> <input type="text" id="txtLastName" class="searchTextBox" /></div>
		<div><span class="label">Address:</span> <input type="text" id="txtAddress1" class="searchTextBox" /></div>
		<div><span class="label"></span> <input type="text" id="txtAddress2" class="searchTextBox" /></div>
		<div><span class="label">City:</span> <input type="text" id="txtCity" class="searchTextBox" /></div>
		<div><span class="label">State:</span> <input type="text" id="txtState" class="searchTextBox" /></div>
		<div><span class="label">Postal Code:</span> <input type="text" id="txtZip" class="searchTextBox" /></div>
		<div><span class="label">Country Code:</span> <input type="text" id="txtCountryCode" class="searchTextBox" /></div>
		
		<div><span class="label">Band Printed Name:</span> <input type="text" id="txtPrintedName" class="searchTextBox" /></div>
		
		<div class="buttonBar">
			<input type="submit" class="button blue" value="Save" onclick="saveButtonClick();" />
			<input type="submit" class="button blue" value="Cancel" onclick="cancelButtonClick();" />
		</div>
		</div>
		</div>
	
</body>
</html>