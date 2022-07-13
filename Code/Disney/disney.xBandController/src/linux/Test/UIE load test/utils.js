
//utils.js 
exports.randomString = function randomString() { 
    var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"; 
    var string_length = 12	; 
    var randomString = ''; 
    for (var i=0; i<string_length; i++) { 
        var rnum = Math.floor(Math.random() * chars.length); 
        randomString += chars.substring(rnum,rnum+1); 
    } 
    return randomString; 
}

exports.checkResponse = function checkResponse(response, thread_id) {
	if (response.statusCode != '200') {
		console.log('[' + thread_id + '] - ' + 'ERROR - response code ' + response.statusCode + ' is indicitive of a problem');
		console.log('[' + thread_id + '] - ' + '  The response body is: ');
		console.log('[' + thread_id + '] - ' + response.body);
		return 1;
	}
	return 0;
} 