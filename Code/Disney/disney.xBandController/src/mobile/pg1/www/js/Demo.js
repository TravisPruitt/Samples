xConnect.utilities.strategies.Demo =
{
	colors : [ "#ff3333", "#ffcc00","#00cc66", "#0066cc", "#cc33cc" ],
	currentColor : 0,
	saveVenueToSettings : function() {
		
	},
	validateCredentials : function(userName, password, callback) {
		var authenticationSucceeded = (userName.length != 0);
		if( authenticationSucceeded ) {
			xConnect.data.user.role = xConnect.data.greeterRoleString;

			xConnect.data.user.id.portalId = userName;
			if( userName.charAt(0) == 'c' || userName.charAt(0) == 'C' ) {
				xConnect.data.user.role = xConnect.data.coordinatorRoleString;
			}
		}
		
		callback( authenticationSucceeded );
	},
	initializeAppData : function() {
		// Demo mode implementation
		xConnect.data.xbrclist = [{url:"localhost"}];
		xConnect.data.setVenue("Demo");
//		xConnect.utilities.loadLocations();
		xConnect.utilities.subscribeToEvents();
		
		// change the login to capture omni info
		$("#idlabel").text('Demo ID');
		$('#pwlabel').text('Demo Password');
	},
	ajax : function(urlString, request, success, error) {
		xConnect.utilities.ajaxHelpers[request.type](urlString, request, success, error);
	},
	ajaxHelpers : {
		locationStatus : function( urlString, request, success, error ) {
			urlString = "Demo/locationStatus.json";
			xConnect.utilities.ajaxHelpers.fetch(urlString, success);
		},
		bumpLocation : function( urlString, request, success, error ) {
			var result = {
				    "version": "1.0",
				    "type": "bumpLocationResponse",
				    "clientReference": "77777777",
				    "location": {
				        "name": request.locationName,
				        "response": "success",
				        "cast": request.cast,
				        "readers": []
				    }
				},
				location = xConnect.data.locations.actions.findLocationByName(request.locationName);

			result.location.readers = location.readers.slice(0);
			
			// trigger a location state change to reset the icon
			setTimeout((function(){
				return function(){
					var data = {"version":"1.0","type":"locationStateChange","location":{"name":request.locationName,"response":"success","byWhom":{"portalId":request.cast.portalId},"readers":[]}};
					
					data.location.readers = result.location.readers;
					
					xConnect.utilities.dispatch([data]);
				};
			}()),10);
			
			success(result);
		},
		closeLocation : function( urlString, request, success, error ) {
			var result = {
				    "version": "1.0",
				    "type": "closeLocationResponse",
				    "clientReference": "77777777",
				    "location": {
				        "name": request.locationName,
				        "response": "success",
				        "byWhom": request.cast,
				        "readers": []
				    }
				},
				location = xConnect.data.locations.actions.findLocationByName(request.locationName);
			
			result.location.readers = location.readers.slice(0);
			result.location.readers.forEach(function(reader){
				if(reader.state == "open") {
					reader.state = "closed";
				}
			});

			// trigger a location state change to reset the icon
			setTimeout((function(){
				return function(){
					var data = {"version":"1.0","type":"locationStateChange","location":{"name":request.locationName,"response":"success","byWhom":{"portalId":request.cast.portalId},"readers":[]}};
					
					data.location.readers = result.location.readers;
					
					xConnect.utilities.dispatch([data]);
				};
			}()),10);
			
			
			success(result);
		},
		openLocation : function( urlString, request, success, error ) {
			var result = {
				    "version": "1.0",
				    "type": "openLocationResponse",
				    "clientReference": "77777777",
				    "location": {
				        "name": request.locationName,
				        "response": "success",
				        "cast": request.cast,
				        "readers": []
				    }
				},
				location = xConnect.data.locations.actions.findLocationByName(request.locationName);
			
			result.location.readers = location.readers.slice(0);
			result.location.readers.forEach(function(reader){
				if(reader.state == "closed") {
					reader.state = "open";
				}
			});
			
			// trigger a location state change to reset the icon
			setTimeout((function(){
				return function(){
					var data = {"version":"1.0","type":"locationStateChange","location":{"name":request.locationName,"response":"success","byWhom":{"portalId":request.cast.portalId},"readers":[]}};
					
					data.location.readers = result.location.readers;
					
					xConnect.utilities.dispatch([data]);
				};
			}()),10);
			
			success(result);
		},
		flashLocation : function( urlString, request, success, error ) {
			var result = {
				    "version": "1.0",
				    "type": "flashLocationResponse",
				    "clientReference": "77777777",
				    "location": {
				        "name": request.locationName,
				        "response": "success",
				        "readers": [],
				        "flashColor": xConnect.utilities.colors[xConnect.utilities.currentColor]
				    }
				},
				location = xConnect.data.locations.actions.findLocationByName(request.locationName);

			// increment and wrap if necessary, our color index
			xConnect.utilities.currentColor = (xConnect.utilities.currentColor+1)%xConnect.utilities.colors.length;
			
			result.location.readers = location.readers.slice(0);
			
			success(result);
		},
		readerRedirect : function( urlString, request, success, error ) {
			var result = {
				    "version": "1.0",
				    "type": "readerRedirectResponse",
				    "clientReference": "77777777",
				    "reader": {
				        "state": "open",
				        "lights": "off",
				        "response": "success",
				        "name": request.reader.name
				    }
			};

			// trigger a reader state change to reset the icon
			setTimeout((function(){
				return function(){
					var data = {"version":"1.0","type":"readerEvent","reader":{"state":"open","lights":"off","name":request.reader.name}};
					
					xConnect.utilities.dispatch([data]);
				};
			}()),10);
			
			success(result);
		},
		readerRetry : function( urlString, request, success, error ) {
			var result = {
				    "version": "1.0",
				    "type": "readerRetryResponse",
				    "clientReference": "77777777",
				    "reader": {
				        "state": "open",
				        "lights": "off",
				        "response": "success",
				        "name": request.reader.name
				    }
			};
			
			// trigger a reader state change to reset the icon
			setTimeout((function(){
				return function(){
					var data = {"version":"1.0","type":"readerEvent","reader":{"state":"open","lights":"off","name":request.reader.name}};
					
					xConnect.utilities.dispatch([data]);
				};
			}()),10);
			
			success(result);
		},
		scheduleMaintenance : function( urlString, request, success, error ) {
			var result = { reader : { response : "success"}};
			
			success(result);
		},
		scheduleBioMaintenance : function( urlString, request, success, error ) {
			var result = { reader : { response : "success"}};
			
			success(result);
		},
		shutdownReader : function( urlString, request, success, error ) {
			
			/// {"version":"1.0","type":"shutdownReaderResponse","clientReference":"77777777",
			// "reader":{"state":"shutdown","response":"success","name":"entry-1","errorCode":"readerIsDisabled",
			// "errorDescription":"Reader has been shutdown."}

			var result = {
				    "version": "1.0",
				    "type": "shutdownReaderResponse",
				    "clientReference": "77777777",
				    "reader": {
				        "name": request.reader.name,
				        "state": "shutdown",
				        "response": "success"
				    }
				};
						
			// trigger a reader state change to reset the icon
			setTimeout((function(){
				return function(){
					var data = {"version":"1.0","type":"readerEvent","reader":{"state":"shutdown","lights":"off","name":request.reader.name}};
					
					xConnect.utilities.dispatch([data]);
				};
			}()),10);
			
			success(result);
		},
		fetch : function(url, success) {
			$.ajax( url , {
				timeout : xConnect.data.ajaxTimeout,
				dataType : "json",
				type : "GET",
				success : success,
				error : function(jqXHR, textStatus, errorThrown) {
					console.log("error:"+textStatus+"\n");
					console.log("status:"+jqXHR.status+"\n");
					console.log("statusText:"+jqXHR.statusText+"\n");
				}
			});
		}
	},
	subscribeToEvents : function() {
		setTimeout(function() {
			xConnect.utilities.ajaxHelpers.fetch("Demo/events.json", function(data){
				xConnect.utilities.dispatch(data);
			});
			xConnect.utilities.subscribeToEvents();
		}, 10000);
	}
};