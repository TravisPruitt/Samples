function Reader()
{
	// reader properties
	this.id; 
	this.name;
	this.type;	// value of ReaderType.name()
	this.typeDescription; // value of ReaderType.getDescription()
	this.group;
	this.signalStrengthThreshold = -90;
	this.gain = 1;
	this.mac;
	this.ip;
	this.port;
	this.lastEventIdReceived;
	this.x;
	this.y;
	this.timeLastHello;
	this.lane;
	this.deviceId;
	this.version;
	this.minXbrcVersion;
	this.transmitter;
	this.modelData;
	this.status;
	this.statusMessage;
	this.hasLight;
	this.isTap;
	this.batteryLevel;
	this.hardwareType;
	
	// location properties
	this.locationId;
	this.locationName;
	
	// xbrc properties
	this.xbrcId;
	this.xbrcIp;
	this.xbrcPort;
	this.xbrcName;
}


