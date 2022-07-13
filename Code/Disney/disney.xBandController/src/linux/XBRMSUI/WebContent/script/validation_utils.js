function fnValidateIPAddressV4(ipaddr) {
    //Remember, this function will validate only Class C IP.
    //change to other IP Classes as you need
    ipaddr = ipaddr.replace( /\s/g, "") //remove spaces for checking
    var re = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/; //regex. check for digits and in
                                          //all 4 quadrants of the IP
    if (re.test(ipaddr)) {
        //split into units with dots "."
        var parts = ipaddr.split(".");
        //if the first unit/quadrant of the IP is zero
        if (parseInt(parseFloat(parts[0])) == 0) {
            return false;
        }
        //if the fourth unit/quadrant of the IP is zero
        if (parseInt(parseFloat(parts[3])) == 0) {
            return false;
        }
        //if any part is greater than 255
        for (var i=0; i<parts.length; i++) {
            if (parseInt(parseFloat(parts[i])) > 255){
                return false;
            }
        }
        return true;
    } else {
        return false;
    }
}

function alphaOnlyCheck(data){
	var regex=/^[a-zA-z]+$/;
	
	if(regex.test(data)){
		return true;
	} else {
		return false;
	}
}

function numericOnlyCheck(data){
	data = data.replace( /\s/g, ""); //remove spaces
	var regex=/^[0-9]+$/;
	
	if(regex.test(data)){
		return true;
	} else {
		return false;
	}
}

function isUrl(s) {
	var regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
	return regexp.test(s);
}

function containsSpaces(data){
	var regex =/\s/;
	if (regex.test(data)){
		return true;
	} else {
		return false;
	}
}

function isValidIntWithinRange(data, min, max)
{
	return (!isNaN(parseInt(data,10)) && data >= min && data <= max);
}

function isValidPositiveJavaInt(data)
{
	return (!isNaN(parseInt(data,10)) && data >= 0 && data <= 2147483647);
}

function isValidPositiveJavaLong(data)
{
	return (!isNaN(parseInt(data,10)) && data >= 0 && data <= 9223372036854775807);
}
