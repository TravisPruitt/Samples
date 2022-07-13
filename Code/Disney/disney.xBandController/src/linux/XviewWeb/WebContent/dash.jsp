<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="user-scalable=no, width=device-width" />
<link rel="stylesheet" type="text/css" href="css/global.css" />
<link rel="stylesheet" type="text/css" 
      href="iphone.css" media="only screen and (max-width: 480px)" />
<title>Registration</title>
<script type="text/javascript">
	// Function to submit to the server.

	//var XviewHostURL = "http://10.75.3.106:8080/";
	var XviewHostURL = "/Xview/";
	var cr = "ursula";

	var xbands = null;
	var xbandIndex = 0;

	var request = CreateRequest();


	function refreshXBands()
	{
		GetXBands();
	
		setTimeout(refreshXBands, 10000);
	}

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

		var hourglass = document.getElementById('waitDialog');
		
		hourglass.style.display = 'none';
		hourglass.style.visibility = 'hidden';
	}

	

	function DisplayGuestNotFound() {
		var searchGuest = document.getElementById('searchGuest');
		var guestNotFound = document.getElementById('guestNotFound');
		var createName = document.getElementById('createName');
		var txtName = document.getElementById('txtName');
		var searchControl = document.getElementById('searchControl');
		createName.innerHTML = txtName.value;

		searchGuest.style.display = 'none';
		guestNotFound.style.display = 'block';

		searchControl.style.display="none";
		searchControl.style.visibility = "hidden";

	}

	function BuildXBandDisplayDiv(xband, index) {
		var div = "<div class='InfoBar'><div class='infobarHeader'>";

		if (xband.active) {
			div += "<div><img class='StatusIcon' src='images/active.png' /></div>";
		} else {
			div += "<div><img class='StatusIcon' src='images/inactive.png' /></div>";
		}

		div += "<span class='labelHeader'>" + xband.printedName + "</span></div>";

		if (xband.guests.length > 0) {
			// display a div for each guest (there should be only one.)
			for ( var y = 0; y <= xband.guests.length - 1; y++) {
				var guest = xband.guests[y];
				if (guest != null) {
					div += "<div class='subBand'>Assigned to: "
							+ buildGuestDisplayDiv(guest, y, xband.xbandId)
							+ "</div>";
				} else {
					// display an "Add guest" when selected.

					div += "<div class='subBand'>Not assigned.<div class='guestInfoBar'><span class='subBandText'></span></div></div>";
				}
			}

		}

		div += "<div class='bandIds' id='bandIds'>";
		div +=	"<div><span class='label'>XBandID : " + xband.xbandId + "</span></div>";
		div += "<div><span class='label'>BandID : " + xband.bandId + "</span></div>";
		div += "<div><span class='label'>LRID : " + xband.lrid + "</span></div>";
		div += "<div><span class='label'>TapID : " + xband.tapId + "</span></div>";
		div += "</div>";
		
		

		div += "<div class='clearBar'></div>";
		div += "</div>";

		return div;
	}

	function buildGuestDisplayDiv(guest, index, xbandId) {
		var guestName = "";
		if (guest.firstName != null) {
			guestName += guest.firstName + " ";
		}
		if (guest.lastName != null) {
			guestName += guest.lastName;
		}

		var removeCommand = "";
		// var removeCommand = "onclick=\"RemoveGuestXBand(\'guests/id/"
		//		+ guest.guestId + "/xbands/" + xbandId + "\');\"";
		var div = "<div class='guestInfoBar'" + removeCommand + ">";
		//div += "<div class='removeButton'" + removeCommand + "'><img class='removeButtonImg' src='images/remove.png' /></a></div>";
		div += "<span class='subBandText'>" + guestName + "</span>";
		div += "</div>";

		return div;
	}

	function buildGuestSearchDisplayDiv(guest, swap) {
		var guestName = "";
		if (guest.firstName != null) {
			guestName += guest.firstName + " ";
		}
		if (guest.lastName != null) {
			guestName += guest.lastName;
		}

		var addCommand = ""
		//var addCommand = "onclick=\"AddGuestXBand(\'guests/id/" + guest.guestId
		//		+ "/xbands/" + xbands[xbandIndex].xbandId + "\');\"";
		var div;
		if (swap == 0)
			{
				div = "<div class='guestInfoBarSearch' " + addCommand + ">";
			}
		else
			{
				div = "<div class='guestInfoBarSearchAlt' " + addCommand + ">";
			}

		div += "<span class='subBandText'>" + guestName + "</span>";
		div += "</div>";
		return div;
	}

	function msgBox(message) {
		// Going to show a div as a message box.
		alert(message);
	}

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

	function refreshXBandList() {
		refreshXBands();
	}

	function resetXBandList() {
		if (request.readyState = 4) {
			if (request.status == 200) {

			}
		}

		resetSlideGuestDialog();
		refreshXBandList();
	}
</script>

</head>
<body onload="refreshXBands()">
	<div id="body">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="menu" />


		<div class="inputArea" id="xbandSelector">

			<div class="xBandList" id="xBandList"></div>

		</div>
			
			<div id="overlay" class="overlay">
			<div class="findGuestDialog" id="findGuestDialog">
				<div class="dialogContents">
					<div class="dialogHeader" style="display: block;" id="searchGuest">
						<span class="label" id="txtBandId"></span> <span class="label">Please
							enter the name of the guest you are searching for.</span> <span
							class="label">Partial name searching is supported.</span>
					</div>
				</div>
			</div>
			</div>
			
<div class="overlay" id="waitDialog" style="display: none;">

<div class="findGuestDialog" id="hourglass" style="display:block;">
	<img src="images/animated_progress_bar.gif" />
</div>

</div>

		<tiles:insertAttribute name="footer" />
	</div>


</body>
</html>