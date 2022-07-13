xConnect.utilities.strategies.Failover = {
	saveVenueToSettings : function() {
		if( xConnect.utilities.isPhoneGap() ) {
			xConnect.cordovaPlugins.Settings.setVenue(xConnect.data.venue);
		}
	},
	setDeviceId : function(callback) {
		// if we have access to the device, get the mac .. otherwise let it stay as default
		if( xConnect.utilities.isPhoneGap() ) {
			xConnect.utilities.strategies.Normal.setDeviceId(callback);
		} else {
			callback();
		}
	},
	saveXbrcsToSettings : function() {
		
	},
	setUserRegistryFromCache : function(callback) {
		var queryObject = xConnect.utilities.getQueryObject();
		
		// if we have access to the device, get the user registry from settings .. otherwise see if it's in the URL
		if( xConnect.utilities.isPhoneGap() ) {
			xConnect.utilities.strategies.Normal.setUserRegistryFromCache(callback);
		} else {
			if( queryObject.userRegistry ) { // if it was passed in ... use it
				xConnect.data.userRegistry = queryObject.userRegistry;
			} // otherwise leave it empty
			callback();
		}
	},
	initializeAppData : function() {
		// Failover mode implementation
				
		// set device Id
		xConnect.utilities.setDeviceId( function() {
			// URL Sample : http://localhost:8080/JQM/index.html?mode=Failover&xbrc=http%3A%2F%2F10.1.52.173%3A8080
			// load xbrc's from cache (settings)
			var queryObject = xConnect.utilities.getQueryObject(),
				decoded = "",
				venue = "";
			
			if( queryObject.useSSLForAuth == "true") {
				xConnect.data.useSSLForAuth = true;
			}
			
			if( queryObject.sslAuthenticationPort ) {
				xConnect.data.sslAuthenticationPort = parseInt(queryObject.sslAuthenticationPort);
			}
			
			if( queryObject.venueCode ) {
				xConnect.data.venueCode = queryObject.venueCode;
				venue = xConnect.utilities.venueFromCode(queryObject.venueCode);
				xConnect.data.setVenue(venue);
			} else {
				xConnect.utilities.setVenueFromCache(); // don't bother waiting for it to finish ... 
			}

			xConnect.utilities.setUserRegistryFromCache( function() {
				// ok, now go and load up all the xbrms, xbrc, and location information
				if( queryObject.xbrmsHost ) {
					xConnect.data.xbrmslist[0] = {host:queryObject.xbrmsHost, port:queryObject.xbrmsPort};
					xConnect.utilities.locateXBRCs();
				} else {
					if( queryObject.xbrc ) { // if an xbrc was specified in the URL, use it
						decoded = decodeURIComponent(queryObject.xbrc);
						xConnect.data.xbrclist = [{url:decoded}];
	//xConnect.data.xbrclist = [{url:"http://10.93.0.86:8080"},{url:"http://10.93.0.83:8080"},{url:"http://10.93.0.88:8080"}];
						xConnect.utilities.subscribeToEvents();
					} else { // get it from settings
						xConnect.utilities.setXbrcFromCache(function() {
							xConnect.utilities.subscribeToEvents();
						});
					}
				}
			})

			
		});
	}
};
