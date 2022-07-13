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

	
	function FindGuest() {
		var txtName = document.getElementById("txtName");

		var guestName = txtName.value;

		var guestSearch = XviewHostURL + "guests/search/" + guestName;

		if (request == null)
			request = CreateRequest();

		if (request != false) {
			request.open("GET", guestSearch, true);
			request.onreadystatechange = displayGuestList;
			request.send(null);
		} else {
			msgBox("Unable to create Request object!");
		}

	}

	function GetXBands() {

		//var hourglass = document.getElementById('waitDialog');

		//hourglass.style.display = 'block';
		//hourglass.style.visibility = 'visible';
		
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
		
	}

	function displayGuestList() {
		if (request.readyState == 4) {
			if (request.status == 200) {

				var guests = eval('({"guests":' + request.responseText + '})');
				var guestList = document.getElementById("guestList");
				guestList.innerHTML = "";

				var swap = 0;
				
				if (guests.guests.length > 0) {
					for ( var d = 0; d <= guests.guests.length - 1; d++) {

						if (swap == 0)
							swap = 1;
						else
							swap = 0;
						
						if (guests.guests[d] != null) {
							guestList.innerHTML += buildGuestSearchDisplayDiv(guests.guests[d], swap);
						} else {

						}
					}
				} else {

				}
			} else {
				if (request.status == 404) {
					DisplayGuestNotFound();
				}
			}
		}

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
		

		div += "<span class='labelHeader'>" + xband.printedName + "</span>";
		div += "</div>";

		if (xband.guests.length > 0) {
			// display a div for each guest (there should be only one.)
			for ( var y = 0; y <= xband.guests.length - 1; y++) {
				var guest = xband.guests[y];
				if (guest != null) {
					div += "<div class='subBand'>"
							+ buildGuestDisplayDiv(guest, y, xband.xbandId)
							+ "</div>";
				} else {
					// display an "Add guest" when selected.

					div += "<div class='subBand' onclick=\"displayFindGuest('"
							+ index
							+ "');\"><div class='guestInfoBar'><img class='addButtonImg' src='images/add.png' /><span class='subBandText'>Add a guest</span></div></div>";
				}
			}

		}

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

		var removeCommand = "onclick=\"RemoveGuestXBand(\'guests/id/"
				+ guest.guestId + "/xbands/" + xbandId + "\');\"";
		var div = "<div class='guestInfoBar'" + removeCommand + ">";
		div += "<div class='removeButton'" + removeCommand + "'><img class='removeButtonImg' src='images/remove.png' /></a></div>";
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

		var addCommand = "onclick=\"AddGuestXBand(\'guests/id/" + guest.guestId
				+ "/xbands/" + xbands[xbandIndex].xbandId + "\');\"";
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

	function RemoveGuestXBand(path) {

		if (request == null)
			request = CreateRequest();
		
		var delCommand = XviewHostURL + path;
		//msgBox(delCommand);

		request.open("DELETE", delCommand, true);
		request.onreadystatechange = refreshXBandList;
		request.send(null);

	}

	function AddGuestXBand(path) {
		if (request == null)
			request = CreateRequest();
		
		var delCommand = XviewHostURL + path;
		//msgBox(delCommand);

		request.open("PUT", delCommand, true);
		request.onreadystatechange = refreshXBandList;
		request.send(null);

		resetSlideGuestDialog();

	}

	function refreshXBandList() {
		refreshXBands();
	}

	function resetTextBox(id) {
		try {
			var hasInnerText = (document.getElementsByTagName("body")[0].innerText != undefined) ? true
					: false;
			var elem = document.getElementById(id);
			elem.value = "";
			if (!hasInnerText) {
				elem.textContent = "";
			} else {
				elem.innerText = "";
			}
		} catch (whatever) {
			// There's nothing to do here.  It resets or it doesnt.
		}

	}

	function displayFindGuest(xbandId) {
		xbandIndex = xbandId;

		var slidingDiv = document.getElementById("overlay");
		var guestList = document.getElementById('guestList');
		var searchGuest = document.getElementById('searchGuest');
		var guestNotFound = document.getElementById('guestNotFound');

		searchGuest.style.display = 'block';
		guestNotFound.style.display = 'none';
		slidingDiv.style.display = 'block';
		slidingDiv.style.visibility='visible';

		resetTextBox('txtName');

		var txtBandId = document.getElementById('txtBandId');

		txtBandId.innerHTML = "You are linking a guest to XBand: "
				+ xbands[xbandIndex].printedName + ".";
		guestList.innerHTML = "";


		slidingDiv.style.bottom = "inherit";

	}

	function resetSlideGuestDialog() {
		slidingDiv = document.getElementById("overlay");
		searchControl = document.getElementById("searchControl");

		searchControl.style.display="block";
		searchControl.style.visibility = "visible";

		
		slidingDiv.style.display = "none";
		slidingDiv.style.visibility ="hidden";
		resetTextBox('txtName');

		
	}

	function searchGuest() {

		var txtName = document.getElementById("txtName");

		var guestName = txtName.value;

		if (guestName == '' || guestName== null)
			return;

		searchControl = document.getElementById("searchControl");



		
		var guestSearch = XviewHostURL + "guests/search/" + guestName;

		if (request == null)
			request = CreateRequest();

		if (request != false) {
			request.open("GET", guestSearch, true);
			request.onreadystatechange = displayGuestList;
			request.send(null);
		}

	}

	function createDemoGuest() {

		var txtName = document.getElementById("txtName");
		var txtPhoneSelect = document.getElementById('txtPhoneSelect');
		//alert(xbandIndex)
		//alert(xbands[xbandIndex].xbandId);

		var guestName = txtName.value;

		var guestSearch = XviewHostURL + "guests/" + guestName + "/xbands/"
				+ xbands[xbandIndex].xbandId + "/phone/" + txtPhoneSelect.value;

		//alert(guestSearch);

				if (request == null)
		request = CreateRequest();

		if (request != false) {
			//alert('putting the request');
			request.open("PUT", guestSearch, true);
			request.onreadystatechange = resetXBandList;
			request.send(null);
		}

		resetSlideGuestDialog();

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

			<!-- Guest Dialog search -->
			

		</div>
			
			<div id="overlay" class="overlay">
			<div class="findGuestDialog" id="findGuestDialog">
				<div class="dialogContents">
					<div class="dialogHeader" style="display: block;" id="searchGuest">
						<span class="label" id="txtBandId"></span> <span class="label">Please
							enter the name of the guest you are searching for.</span> <span
							class="label">Partial name searching is supported.</span>
						

					</div>
					<div class="dialogHeader" id="guestNotFound" style="display: none;">
						<span class="label">I didn't find a guest with that name.
							You can create the guest by selecting the Create button or return
							to the search by selecting the Back button.</span>
						<div>
							<span class="label">Name : <span class="boldLabel"
								id="createName"></span>
							</span> <span class="label">Please select a demo phone (or None)</span>
							<select id="txtPhoneSelect" class="listBox">
								<option value="None">None</option>
								<option value="555-1111">555-1111</option>
								<option value="666-1111">666-1111</option>
								<option value="777-1111">777-1111</option>
								<option value="888-1111">888-1111</option>
							</select>

							<div class="buttonBar">
								<input class="button blue" type="submit" value="Create"
									onclick="createDemoGuest();" id="createButton" /> <input class="button blue"
									type="submit" value="Cancel" onclick="resetSlideGuestDialog();" id="cancelCreateButton" />
							</div>

						</div>

					</div>
					
					<div class="searchControl" id="searchControl">
					<div class="guestSearchResults">
						
					<span class="label">Please select the guest:</span>
					<div class="guestList" id="guestList"></div>
					</div>

							<span class="boldLabel">Guest Name:</span><input type="text"
								id="txtName" class="searchTextBox" />
								<div class="buttonBar">
								 <input class="button blue"
								id="searchGuestButton" type="submit" value="Search"
								onclick="searchGuest()" /> <input class="button blue"
								id="cancelButton" type="submit" value="Cancel"
								onclick="resetSlideGuestDialog();" />
								</div>
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