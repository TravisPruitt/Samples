function NetworkAddress(){}

// static method
NetworkAddress.isValidHostname = function(hostname) 
{	
	if (hostname === undefined){
		return false;
	}
	
	if (typeof(hostname) === "string"){
        if(hostname != null) {
            hostname = hostname.replace( /^\s+/, "" ).replace( /\s+$/, "" )
        }
	}
	
	if (hostname.length > 255){
		return false;
	}
	
	var matches = hostname.match(/^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])(\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]{0,61}[a-zA-Z0-9]))*$/gi);
	if (matches == null || matches.length > 1)
	{
		return false;
	}
	
	return true;
};

//static method
NetworkAddress.isValidPort = function(port)
{
	if (port === undefined){
		return false;
	}
	
	if (isNaN(parseInt(port, 10)) || port > 65535 || port < 1){
		return false;
	}
	
	return true;
};

//static method
NetworkAddress.isValidMacAddress = function(macAddress) 
{
	if (macAddress === undefined){
		return false;
	}
	
	if (typeof(macAddress) === "string"){
        if(macAddress != null) {
            macAddress = macAddress.replace( /^\s+/, "" ).replace( /\s+$/, "" )
        }
	}
	
	if (macAddress.length > 17){
		return false;
	}
	
	var matches = macAddress.match(/[a-fA-F0-9][a-fA-F0-9](:[a-fA-F0-9][a-fA-F0-9]){5}/gi);
	if (matches == null || matches.length > 1)
	{
		return false;
	}
	
	return true;
};
