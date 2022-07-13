function OmniServer()
{
	// public
	this.invalid = Object.create(null);	// create a new object without the default keys added by the prototype
	this.invalid["id"] = false;;
	this.invalid["hostname"] = false;
	this.invalid["port"] = false;
	this.invalid["description"] = false;
	this.invalid["active"] = false;
	this.invalid["notActiveReason"] = false;
	
	// private
	var id;
	var hostname;
	var port;
	var active;
	var description;
	var notActiveReason;
	var that = this;
	
	// public methods
	this.setId = function(id){
		if (id === undefined){
			return;
		}
		
		try {
			if (typeof(id) === "string"){
				id = id.trim();
				if (id.length == 0){
					return;
				}
			}
			
			that.id = parseInt(id);
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	this.getId = function(){
		return that.id;
	};
	this.setHostname = function(hostname){
		that.hostname = hostname;
	};
	this.getHostname = function(){
		return that.hostname;
	};
	this.setPort = function(port){
		if (port === undefined){
			return;
		}
		
		try {
			if (typeof(port) === "string"){
				port = port.trim();
				if (port.length == 0){
					return;
				}
			}
			
			that.port = parseInt(port);
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	this.getPort = function(){
		return that.port;
	};
	this.setActive = function(active) {
		if (active === undefined){
			return;
		}
		
		try {
			if (typeof(active) === "boolean"){
				that.active = active;
				return;
			}
			
			if (typeof(active) === "string"){
				active = active.trim();
				if (active.length == 0){
					return;
				}
			}
			
			active = active.toLowerCase();
			if (active === "true"){
				that.active = true;
			} else {
				that.active = false;
			}
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	this.getActive = function(){
		return that.active;
	};
	this.setDescription = function(description){
		if (description === undefined){
			return;
		}
		
		that.description = TextInputSanitizer.sanitize(description,"");
	};
	this.getDescription = function(){
		return that.description;
	};
	this.setNotActiveReason = function(notActiveReason){
		if (notActiveReason === undefined){
			return;
		}
		
		that.notActiveReason = TextInputSanitizer.sanitize(notActiveReason,"");
	};
	this.getNotActiveReason = function(){
		return that.notActiveReason;
	};
}

// public method
OmniServer.prototype.isValid = function() {
	var valid = true;
	
	if (!NetworkAddress.isValidHostname(this.hostname))
	{
		valid = false;
		this.invalid["hostname"] = true;
	}
		
	if (!NetworkAddress.isValidPort(this.port)) {
		valid = false;
		this.invalid["port"] = true;
	}
	
	if (this.description !== undefined && this.description.trim().length > 0){
		if (this.description.trim().length > 1024){
			valid = false;
			this.invalid["description"] = true;
		}
	}
	
	if (this.notActiveReason !== undefined && this.notActiveReason.trim().length > 0){
		if (this.notActiveReason.trim().length > 1024){
			valid = false;
			this.invalid["notActiveReason"] = true;
		}
	}
	
	return valid;
};

//static method
OmniServer.areEqual = function(omni1, omni2) {
	if (omni1 === undefined || omni2 === undefined){
		return false;
	}
	
	if (omni1.id !== omni2.id){
		return false;
	}
	
	if (omni1.hostname !== omni2.hostname){
		return false;
	}
	
	if (omni1.port !== omni2.port){
		return false;
	}
	
	if (omni1.getDescription() !== omni2.getDescription()){
		return false;
	}
	
	if (omni1.active !== omni2.active){
		return false;
	}
	
	if (omni1.getNotActiveReason() !== omni2.getNotActiveReason()){
		return false;
	}
	
	return true;
};