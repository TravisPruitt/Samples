function Reader(defaultSsThresholdMin, defaultSsThresholdMax, defaultGainMin, defaultGainMax)
{
	// public
	this.defaultSsThresholdMin = defaultSsThresholdMin;
	this.defaultSsThresholdMax = defaultSsThresholdMax;
	this.defaultGainMin = defaultGainMin;
	this.defaultGainMax = defaultGainMax;
	
	this.invalid = Object.create(null);	// create a new object without the default keys added by the prototype
	this.invalid["id"] = false;;
	this.invalid["name"] = false;
	this.invalid["ipAddress"] = false;
	this.invalid["port"] = false;
	this.invalid["macAddress"] = false;
	this.invalid["ssThreshold"] = false;
	this.invalid["gain"] = false;
	this.invalid["lane"] = false;
	this.invalid["deviceId"] = false;
	this.invalid["type"] = false;
	this.invalid["modelData"] = false;
	this.invalid["bioDeviceType"] = false;
	
	// private variables
	var id;
	var name;
	var ipAddress;
	var port;
	var macAddress;
	var ssThreshold;	//signal strength threshold
	var gain;
	var lane;
	var deviceId;
	var type;
	var enabled;
	var modelData;
	var bioDeviceType;
	
	/*
	 * 'that' is needed so that public methods defined inside the
	 * constructor have access to the private variables
	 */ 
	var that = this;	
	
	// private methods (visible only from methods defined inside the constructor)
	function trim(original){
		if (typeof(original) === "string"){
			return original.trim();
		} else {
			return original;
		}
	}
	
	// public methods
	this.getThat = function(){
		return that;
	};
	
	this.setId = function(id){
		if (id === undefined){
			return;
		}
		
		try {
			id = trim(id);
			if (id.length == 0){
				return;
			}
			
			that.id = parseInt(id);
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	this.getId = function(){
		return that.id;
	};
	this.setName = function(name){
		if (name === undefined){
			return name;
		}
		
		name = trim(name);
		if (name.length == 0){
			return;
		}
		
		that.name = name;
	};
	this.getName = function(){
		return that.name;
	};
	this.setIpAddress = function(ipAddress){
		if (ipAddress === undefined){
			return;
		}
		
		ipAddress = trim(ipAddress);
		if (ipAddress.length == 0){
			return;
		}
		
		that.ipAddress = ipAddress;
	};
	this.getIpAddress = function(){
		return that.ipAddress;
	};
	this.setPort = function(port){
		if (port === undefined){
			return;
		}
		
		try {
			port = trim(port);
			if (port.length == 0){
				return;
			}
			
			that.port = parseInt(port);
			
			if (!isNumber(port)) {
				that.port = NaN;
			}
		} catch (exception) {
			Debugger.log(exception);
		}
	};
	this.getPort = function(){
		return that.port;
	};
	this.setMacAddress = function(macAddress){
		if (macAddress === undefined){
			return;
		}
		
		macAddress = trim(macAddress);
		if (macAddress.length == 0){
			return;
		}
		
		that.macAddress = macAddress;
	};
	this.getMacAddress = function(){
		return that.macAddress;
	};
	this.setSsThreshold = function(ssThreshold){
		if (ssThreshold === undefined){
			return;
		}
		
		try {
			ssThreshold = trim(ssThreshold);
			if (ssThreshold.length == 0){
				that.ssThreshold = this.defaultSsThresholdMin;
			} else {
				that.ssThreshold = parseInt(ssThreshold);
				
				if (!isNumber(ssThreshold)) {
					that.ssThreshold = NaN;
				}
			}
						
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.getSsThreshold = function(){
		return that.ssThreshold;
	};
	this.setGain = function(gain){
		if (gain === undefined){
			return;
		}
		
		try{
			gain = trim(gain);
			if (gain.length == 0){
				that.gain = 0;
			} else {
				that.gain = parseInt(gain);
						
				if (!isNumber(gain)) {
					that.gain = NaN;
				}
			}
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.getGain = function(gain){
		return that.gain;
	};
	this.setLane = function(lane){
		if (lane === undefined){
			return;
		}
		
		try{
			lane = trim(lane);
			if (lane.length == 0){
				return;
			}
			
			that.lane = parseInt(lane);
			
			if (!isNumber(lane)) {
				that.lane = NaN;
			}
				
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.getLane = function(){
		return that.lane;
	};
	this.setDeviceId = function(deviceId){
		if (deviceId === undefined){
			return;
		}
		
		try{
			deviceId = trim(deviceId);
			if (deviceId.length == 0){
				return;
			}
			
			that.deviceId = parseInt(deviceId);
			
			if (!isNumber(deviceId)) {
				that.deviceId = NaN;
			}
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.getDeviceId = function(){
		return that.deviceId;
	};
	this.setType = function(type){
		if (type === undefined){
			return;
		}
		
		try {
			type = trim(type);
			if (type.length == 0){
				return;
			}
			
			that.type = type;
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.getType = function(){
		return that.type;
	};
	this.setEnabled = function(enabled){
		if (enabled === undefined){
			return;
		}
		
		try {
			if (typeof(enabled) === "boolean"){
				that.enabled = enabled;
				return;
			}
			
			enabled = trim(enabled);
			if (enabled.toLowerCase() === "true"){
				that.enabled = true;
			} else if (enabled.toLowerCase() === "false"){
				that.enabled = false;
			} else {
				that.enabled = true;
			}
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.setModelData = function(modelData){
		if (modelData === undefined){
			return;
		}
		
		that.modelData = TextInputSanitizer.sanitize(modelData,"");
	};
	this.getModelData = function(){
		return that.modelData;
	};
	
	this.setBioDeviceType = function(bioDevType){
		if (bioDevType === undefined){
			return;
		}
		
		try{
			bioDevType = trim(bioDevType);
			if (bioDevType.length == 0){
				return;
			}
			
			that.bioDeviceType = parseInt(bioDevType);
			
			if (!isNumber(bioDevType)) {
				that.bioDeviceType = NaN;
			}
		} catch (exception){
			Debugger.log(exception);
		}
	};
	this.getBioDeviceType = function(){
		return that.bioDeviceType;
	};
}

// public instance method
Reader.prototype.isValid = function(edit) {
	var valid = true;
	
	if (this.type === undefined){
		valid = false;
		this.invalid['type'] = true;
	}
	if (this.name !== undefined){
		if (this.name.length > 32){
			valid = false;
			this.invalid['name'] = true;
		}
		// check for illegal characters
		var matches = (this.name.indexOf("|") >= 0);
		matches = matches || (this.name.indexOf(" ") >= 0);
		matches = matches || (this.name.indexOf("\r\n") >= 0);
		matches = matches || (this.name.indexOf("<") >= 0);
		matches = matches || (this.name.indexOf(">") >= 0);
		matches = matches || (this.name.indexOf("*") >=0);
		if (matches == true){
			valid = false;
			this.invalid['name'] = true;
		}
	} else {
		valid = false;
		this.invalid['name'] = true;
	}
	
	if (!NetworkAddress.isValidHostname(this.ipAddress)){
		valid = false;
		this.invalid['ipAddress'] = true;
	}
		
	if (!NetworkAddress.isValidPort(this.port)) {
		valid = false;
		this.invalid['port'] = true;
	}
	
	// long range is defined as 1 in ReaderType.java
	if (this.type == 1){
		if (this.ssThreshold === undefined || this.ssThreshold < this.defaultSsThresholdMin 
				|| this.ssThreshold > this.defaultSsThresholdMax || isNaN(this.ssThreshold)){
			valid = false;
			this.invalid['ssThreshold'] = true;
		}
		
		if (this.gain === undefined || this.gain < this.defaultGainMin
				|| this.gain > this.defaultGainMax || isNaN(this.gain)){
			valid = false;
			this.invalid['gain'] = true;
		}
	}
	
	if (this.lane !== undefined && isNaN(this.lane) || (this.lane !== undefined && this.lane < 0)){
		valid = false;
		this.invalid['lane'] = true;
	}
	
	// long range is defined as 1 in ReaderType.java
	if (this.type != 1 && 
			((this.deviceId === undefined) || this.deviceId < 0 || isNaN(this.deviceId))){
		valid = false;
		this.invalid['deviceId'] = true;
	}
	
	// certain fields are not editable on edit and should be validated
	// only when a new reader is created
	if (edit === undefined || edit === false)
	{
		if (!NetworkAddress.isValidMacAddress(this.macAddress)){
			valid = false;
			this.invalid['macAddress'] = true;
		}
	}
	
	// long range is defined as 1 in ReaderType.java
	if (this.type == 3 && 
			(this.bioDeviceType === undefined) || this.bioDeviceType < 0){
		valid = false;
		this.invalid['bioDeviceType'] = true;
	}
	
	return valid;
};

// public static method
Reader.areEqual = function(r1, r2) {
	if (r1 === undefined || r2 === undefined){
		return false;
	}
	if (r1.id !== r2.id){
		return false;
	}
	if (r1.name !== r2.name){
		return false;
	}
	if (r1.ipAddress !== r2.ipAddress){
		return false;
	}
	if (r1.macAddress !== r2.macAddress){
		return false;
	}
	if (r1.port !== r2.port){
		return false;
	}
	if (r1.ssThreshold !== r2.ssThreshold){
		return false;
	}
	if (r1.gain !== r2.gain){
		return false;
	}
	if (r1.lane !== r2.lane){
		return false;
	}
	if (r1.deviceId !== r2.deviceId){
		return false;
	}
	if (r1.type !== r2.type){
		return false;
	}
	if (r1.enabled !== r2.enabled){
		return false;
	}
	if (r1.modelData !== r2.modelData){
		return false;
	}
	if (r1.bioDeviceType !== r2.bioDeviceType){
		return false;
	}
	
	return true;
};