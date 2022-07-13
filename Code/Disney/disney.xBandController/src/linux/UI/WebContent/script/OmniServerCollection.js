function OmniServerCollection()
{
	// private
	var omnis;
	var numOmnis;

	var that = this;	// makes possible for 'this' to access its private variables
	
	this.addOmni = function(id,hostname,description,active,port,notActiveReason,priority) 
	{
		try {
			var omniServer = new OmniServer();
			omniServer.setId(id);
			omniServer.setHostname(hostname);
			omniServer.setDescription(description);
			omniServer.setActive(active);
			omniServer.setPort(port);
			omniServer.setNotActiveReason(notActiveReason);
			
			if (that.omnis === undefined){
				that.omnis = new Array();
				that.numOmnis = 0;
			} else {
				// don't insert duplicates
				for (var i = 0; i < that.omnis.length; i++){
					if (OmniServer.areEqual(omniServer, that.omnis[i])){
						return i;
					}
				}
			}
			
			// add
			if (priority === undefined){
				var index = that.omnis.length;
				
				// grab the first available index
				for (var i = 0; i < that.omnis.length; i++){
					if (that.omnis[i] === undefined){
						index = i;
						break;
					}
				}
				
				// add the omni server
				that.omnis[index] = omniServer;
				that.numOmnis += 1;
				return index;
			} else {
				priority = parseInt(priority);
				that.omnis[priority] = omniServer;
				that.numOmnis += 1;
				return priority;
			}
			
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.removeOmni = function(id)
	{
		try {
			if (that.omnis === undefined){
				that.omnis = new Array();
				that.numOmnis = 0;
				
				return null;
			}
			
			id = parseInt(id);
			
			for (var i = 0; i < that.omnis.length; i++){
				if (that.omnis[i] !== undefined && that.omnis[i].id === id){
					// remove
					var removed = that.omnis[i];
					if (removed === undefined){
						return null;
					}
					
					that.omnis[i] = undefined;
					
					that.numOmnis -= 1;
					return removed;
				}
			}
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.getOmni = function(id)
	{
		try {
			if (that.omnis === undefined){
				that.omnis = new Array();
				that.numOmnis = 0;
				
				return null;
			}
			
			id = parseInt(id);
			
			for (var i = 0; i < that.omnis.length; i++){
				if (that.omnis[i] !== undefined && that.omnis[i].id === id){
					return that.omnis[i];
				}
			}
			
			return null;
			
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.getOmniAtIndex = function(index)
	{
		try {
			if (that.omnis === undefined){
				that.omnis = new Array();
				that.numOmnis = 0;
				
				return undefined;
			}
			
			index = parseInt(index);
			
			if (index >= 0 && index < that.omnis.length){
				return that.omnis[index];
			}
			
			return undefined;
			
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.getPriority = function(id) {
		try {
			if (that.omnis === undefined){
				return null;
			}
			
			id = parseInt(id);
			
			for (var i = 0; i < that.omnis.length; i++){
				if (that.omnis[i] !== undefined && that.omnis[i].id === id){
					return (i + 1);		// most people start counting with 1, not 0
				}
			}
			
			return -1;
			
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.getIndex = function(id) {
		try {
			if (that.omnis === undefined){
				return null;
			}
			
			id = parseInt(id);
			
			for (var i = 0; i < that.omnis.length; i++){
				if (that.omnis[i] !== undefined && that.omnis[i].id === id){
					return i;
				}
			}
			
			return -1;
			
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.getLength = function()
	{
		return that.numOmnis;
	};
	
	this.getNextAvailableIndex = function(startIndex)
	{
		try {
			if (that.omnis === undefined){
				that.omnis = new Array();
				that.numOmnis = 0;
				
				return 0;
			}
			
			startIndex = parseInt(startIndex);
			
			if (startIndex > that.omnis.length){
				return startIndex + 1;
			}
			
			for (var i = startIndex; i < that.omnis.length; i++){
				if (that.omnis[i] === undefined){
					return i;	// middle
				}
			}
			
			return i;	// end
			
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	
	this.getCollection = function()
	{
		if (that.omnis === undefined){
			that.omnis = new Array();
			that.numOmnis = 0;
		}
		
		return that.omnis;
	};
}
