var request = require('request');
var utils = require('./utils.js');
var self = this;
var thread_count = (process.argv[2] || 2) * 1;
var total = thread_count;

//Test Settings

//****** Alpha Lab ******
var uie_host = 'nl-flfa-00097.wdw.disney.com'
//******* MK LDU ********
//var uie_host = 'nge-mk-mobile.wdw.disney.com'
//***********************


//SET THIS TO TRUE TO GET DEBUG OUTPUT
var debug = true;

var delay = 5000;
var maxDelay = 8000;
var globalSecureId = 40000023;
var maxSecureId = ((parseInt(process.argv[3], 10) + parseInt(40000023, 10)) * 1 || 40005023) * 1;

var uie_port = '8080'
var email = 'uie.testuser@yahoo.com'

//METRICS
var m_CreateParty = {
	"total" : 0,
	"count" : 0
}
var m_AssignBands = {
	"total" : 0,
	"count" : 0
}
var m_ParkSchedule = {
	"total" : 0,
	"count" : 0
}
var m_XPassAvailability = {
	"total" : 0,
	"count" : 0
}
var m_XPassOfferSet = {
	"total" : 0,
	"count" : 0
}
var m_XPassBook = {
	"total" : 0,
	"count" : 0
}

//Global variables
var error_count = 0;
var success_count = 0;
var count = 0;



//
var doBooking = function() {

	//Get the card's secure id
	var secureId = getSecureId();
	
	//Generate random thread id
	var thread_id = utils.randomString();

	//Local thread variables
	var xid;
	var party_id;
	var guest_id;
	var response_body;
	var visit_date;
	var offersetid;
	var request_start_time;
	var response_time;	
	
	if(debug) {
		console.log('[' + thread_id + '] - [DEBUG] ' + 'secure id is: ' + secureId);
	}
	
	//createParty
	// POST /labdemo/data/CreateParty'
	// PAYLOAD: {"id": 0, "fname": "test", "lname": "user1", "size": 1, "email": "uie.testuser@yahoo.com", "guests": [{ "islead": "true", "fname": "test", "lname": "user1","magicbands": []}]}
	request_start_time = new Date();
	request({
		url: 'http://' + uie_host + ':' + uie_port + '/labdemo/data/CreateParty',
		method: 'POST',
		body: JSON.stringify({"id": 0, "fname": "uieload", "lname": "testuser", "size": 1, "email": "uie.testuser@yahoo.com", "guests": [{ "islead": "true", "fname": "test", "lname": "user1","magicbands": []}]})
	}, function(err, response){
		
		//Response time calculations
		response_time = new Date() - request_start_time;
		console.log('[' + thread_id + '] [INFO] - CreateParty response time: ', response_time, 'ms');
		m_CreateParty.total = m_CreateParty.total + response_time;
		m_CreateParty.count++;
		
		utils.checkResponse(response, thread_id);

		if(debug) {
			console.log('[' + thread_id + '] [DEBUG] - ' + 'CreateParty response: ' + response.body + '\n');
		}
		
		response_body = JSON.parse(response.body);
		
		//Save off the xid and partyid
		xid = response_body.guests[0].xid;
		party_id = response_body.id;
		guest_id = response_body.guests[0].idmsid;

		//DEBUG
		if(debug) {
			console.log('[' + thread_id + '] [DEBUG] - ' + 'xid: ' + xid + ' party ID: ' + party_id + ' guest ID: ' + guest_id + 'secure ID: ' + secureId + '\n');
		}
		
		//Set the body for the assign bands request
		var assign_bands_payload = '{"idtype": "secureid","party": {"id": "' + party_id + '","fname": "uieload","celebrations": "uietest\'s Birthday","lname": "testuser","size": 1,"email": "' + email + '","guests": [{"label3.color": "-16777216","visitcount": 0,"showcheck": "true","label3": "FASTPASS Card Assigned","lname": "testuser","email": "' + email + '","magicbands": ["' + secureId + '"],"fname": "uieload","celebrationtext": "Birthday Celebration","xid": "' + xid + '","celebration": "Birthday","avatar": "pluto","idmsid": "' + guest_id + '"}]}}';
		
		//DEBUG	
		if(debug) {
			console.log('[' + thread_id + '] - [DEBUG] ' + 'AssignBands payload: ' + assign_bands_payload + '\n');
		}
		
		
		//AssignBands
		//POST /labdemo/data/AssignBands
		//PAYLOAD: {"party":{"id": "630","fname": "uieload","celebrations": "uietest's Birthday","lname": "testuser","size": 1,"email": "uie.testuser@yahoo.com","guests": [{"label3.color": "-16777216","visitcount": 0,"showcheck": "true","label3": "FASTPASS Card Assigned","lname": "testuser","email": "uie.testuser@yahoo.com","magicbands": ["35050000"],"fname": "uieload","celebrationtext": "Birthday Celebration","xid": "44b3beaa2c6c4f0f92b80b26ff1fd346","celebration": "Birthday","avatar": "pluto","idmsid": "36001916"}]}, "idtype": "secureid"}
		//Payload: "idtype": "secureid","party": {"id": "630","fname": "uieload","celebrations": "uietest's Birthday","lname": "testuser","size": 1,"email": "uie.testuser@yahoo.com","guests": [{"label3.color": "-16777216","visitcount": 0,"showcheck": "true","label3": "FASTPASS Card Assigned","lname": "testuser","email": "uie.testuser@yahoo.com","magicbands": ["35050000"],"fname": "uieload","celebrationtext": "Birthday Celebration","xid": "44b3beaa2c6c4f0f92b80b26ff1fd346","celebration": "Birthday","avatar": "pluto","idmsid": "36001916"}]}}
		request_start_time = new Date();
		request({
			url: 'http://' + uie_host + ':' + uie_port + '/labdemo/data/AssignBands',
			method: 'POST',
			body: assign_bands_payload
		}, function(err, response2){
			
			//Response time calculations
			response_time = new Date() - request_start_time;
			console.log('[' + thread_id + '] [INFO] - AssignBands response time: ', response_time, 'ms');
			m_AssignBands.total = m_AssignBands.total + response_time;
			m_AssignBands.count++;
			
			utils.checkResponse(response2, thread_id);
			
			if(debug) {
				console.log('[' + thread_id + '] [DEBUG] - ' + 'AssignBands response: ' + response2.body + '\n');
			}
			
			
			//ParkSchedule
			//GET /labdemo/data/ParkSchedule?park=80007944&6108'
			request_start_time = new Date();
			request({
				url: 'http://' + uie_host + ':' + uie_port + '/labdemo/data/ParkSchedule?park=80007944&6108',
				method: 'GET'
			}, function(err, response3) {
				
				//Response time calculations
				response_time = new Date() - request_start_time;
				console.log('[' + thread_id + '] [INFO] - ParkSchedule response time: ', response_time, 'ms');
				m_ParkSchedule.total = m_ParkSchedule.total + response_time;
				m_ParkSchedule.count++;
			
				utils.checkResponse(response3, thread_id);
						
				if(debug) {
					console.log('[' + thread_id + '] - ' + 'ParkSchedule response: ' + response3.body + '\n');
				}
				response3_body = JSON.parse(response3.body);
					
				//Grab a date between today and 4 days from now
				//visit_date = Math.floor(Math.random()*5);
				
				//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
				//Randomly select one of the four dates
				// SELECT VISIT DATE
				//visit_date = 1 + Math.floor(Math.random()*4);
				visit_date = Math.floor(Math.random()*4);
				//visit_date = 0; 
				//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
				
				visit_date = response3_body.parkschedule[visit_date].value;
				
				if(debug) {
					console.log('[' + thread_id + '] [DEBUG] - ' + 'visit date to book entitlements for: ' + visit_date);
				}
					
				//convert date from '25/04/2012' format to '2012-04-25'
				visit_date = visit_date.substring(6,10) + '-' + visit_date.substring(3,5) + '-' + visit_date.substring(0,2);
				if(debug) {
					console.log('[' + thread_id + '] - ' + 'converting to GXP required format... ' + visit_date);
				}
				
				
				//XPassAvailability
				//GET http://10.92.63.94:8080/labdemo/data/XPassAvailability?date=2012-04-25&park=80007944&gid=d26e80a3228044de857c8d5b737942b1&
				request_start_time = new Date();
				request({
					url: 'http://' + uie_host + ':' + uie_port + '/labdemo/data/XPassAvailability?date=' + visit_date + '&park=80007944&gid=' + xid,
					method: 'GET'
				}, function(err, response4) {
				
					//Response time calculations
					response_time = new Date() - request_start_time;
					console.log('[' + thread_id + '] [INFO] - XPassAvailability response time: ', response_time, 'ms');
					m_XPassAvailability.total = m_XPassAvailability.total + response_time;
					m_XPassAvailability.count++;
				
					utils.checkResponse(response4, thread_id);
				
					if(debug) {
						console.log('[' + thread_id + '] [DEBUG] - ' + 'XPassAvailability response: ' + response4.body + '\n');
					}
					response4_body = JSON.parse(response4.body);
							
					//choose 2 tier1 selections
					var tier1_1 = Math.floor(Math.random()*5);
					var tier1_2 = Math.floor(Math.random()*5);
					while (tier1_1 == tier1_2) {
						tier1_2 = Math.floor(Math.random()*5);
					}
							
					//choose 2 tier2 selections
					var tier2_1 = Math.floor(Math.random()*5);
					var tier2_2 = Math.floor(Math.random()*5);
					while (tier2_1 == tier2_2) {
						tier2_2 = Math.floor(Math.random()*5);
					}	
							
					var xPassOfferSetUrl = 'http://' + uie_host + ':' + uie_port + '/labdemo/data/XPassOfferSet?xid=' + xid + '&date=' + visit_date + '&gid=' + xid + '&park=80007944&pref=' + response4_body.tier1[tier1_1].id + '&pref=' + response4_body.tier1[tier1_2].id + '&pref=' + response4_body.tier2[tier2_1].id + '&pref=' + response4_body.tier2[tier2_2].id;
					if(debug) {
						console.log('[' + thread_id + '] [DEBUG] - ' + 'url: ' + xPassOfferSetUrl);
					}
					
					
					//XPassOfferSet
					//GET labdemo/data/XPassOfferSet?xid=d26e80a3228044de857c8d5b737942b1&date=2012-04-25&gid=d26e80a3228044de857c8d5b737942b1&park=80007944&pref=80010176&pref=80010192&pref=80010170&pref=80010213
					request_start_time = new Date();
					request({
						url: 'http://' + uie_host + ':' + uie_port + '/labdemo/data/XPassOfferSet?xid=' + xid + '&date=' + visit_date + '&gid=' + xid + '&park=80007944&pref=' + response4_body.tier1[tier1_1].id + '&pref=' + response4_body.tier1[tier1_2].id + '&pref=' + response4_body.tier2[tier2_1].id + '&pref=' + response4_body.tier2[tier2_2].id,
						method: 'GET'
					}, function(err, response5) {
						
						//Response time calculations
						response_time = new Date() - request_start_time;
						console.log('[' + thread_id + '] [INFO] - XPassOfferSet response time: ', response_time, 'ms');
						m_XPassOfferSet.total = m_XPassOfferSet.total + response_time;
						m_XPassOfferSet.count++; 
						
						utils.checkResponse(response5, thread_id);
					
						if(debug) {
							console.log('[' + thread_id + '] [DEBUG] - ' + 'XPassOfferSet response: ' + response5.body + '\n');
						}
						
						var response5_body = JSON.parse(response5.body);
						
						//DEBUG
						if(debug) {
							console.log('[' + thread_id + '] [DEBUG] - ' + response5_body.offersetIds[0] + '  ' + visit_date + '  ' + xid);
						}
						
						//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
						//Randomly select one of the four offer sets
						// SELECT OFFER SET
						// Random number from 0-3 will select offer set A-D
						var offerset = Math.floor(Math.random()*3); 
						//var offerset = 0;
						//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
				
						//DEBUG
						var xPassBookPayload = '{"version": 1, "offersetid": ' + response5_body.offersetIds[offerset] + ', "date": "' + visit_date + '", "xid": "' + xid + '"}';
						if(debug) {
							console.log('[' + thread_id + '] [DEBUG] - ' + 'xPassBook payload: ' + xPassBookPayload);
						}
						
						offersetid = response5_body.offersetIds[offerset];
						
						
						//XPassBook
						//POST /labdemo/data/XPassBook
						//PAYLOAD: {"version": 1, "offersetid": 5077987, "date": "2012-04-25", "xid": "d26e80a3228044de857c8d5b737942b1"}
						request_start_time = new Date();
						request({	
							url: 'http://' + uie_host + ':' + uie_port + '/labdemo/data/XPassBook',
							method: 'POST',
							body: xPassBookPayload
							//JSON.stringify({"version": 1, "offersetid": ' + response5_body.offersetIds[offerset] + ', "date": "' + visit_date + '", "xid": "' + xid + '"})					  
						}, function(err, response6) {
						
							//Response time calculations
							response_time = new Date() - request_start_time;
							console.log('[' + thread_id + '] [INFO] - XPassBook response time: ', response_time, 'ms');
							m_XPassBook.total = m_XPassBook.total + response_time;
							m_XPassBook.count++; 
						
							if(debug) {
								console.log('[' + thread_id + '] [DEBUG] - ' + 'XPassBook response: ' + response6.body + '\n');
							}
						
							if(utils.checkResponse(response, thread_id) == 0) {
								console.log('[' + thread_id + '] - ' + 'Completed booking offer set ' + offersetid + ' for guest ' + guest_id + ' with xid of ' + xid + ' - batch: ' + count); 
								success_count++;
							} 
							else {
								console.log('[' + thread_id + '] - ' + 'Failed in booking offer set ' + offersetid + ' for guest ' + guest_id + ' with xid of ' + xid + ' - batch: ' + count); 
								error_count++;
							}
							
						});
					});
				});
			});
		});
	});
	
	return --thread_count;
};


//returns and increments the global secure ID counter
function getSecureId() {
	return globalSecureId++;
}
 
//Function to throttle load
function chunk() {
	while(doBooking()){}
	thread_count = total;
    count++;     
	
	//If we've reached the end, log the results
	if (globalSecureId >= maxSecureId) {
		console.log('\n******************************************************************');
		//console.log('* ' + success_count + ' bookings were made during this test');
		console.log('* Test Complete!');
		console.log('* ' + error_count + ' error(s) were encountered during the test');
		console.log('*');
		console.log('* CreateParty average response time: ', m_CreateParty.total / m_CreateParty.count, 'ms');
		console.log('* AssignBands average response time: ', m_AssignBands.total / m_AssignBands.count, 'ms');
		console.log('* ParkSchedule average response time: ', m_ParkSchedule.total / m_ParkSchedule.count, 'ms');
		console.log('* XPassAvailability average response time: ', m_XPassAvailability.total / m_XPassAvailability.count, 'ms');
		console.log('* XPassOfferSet average response time: ', m_XPassOfferSet.total / m_XPassOfferSet.count, 'ms');
		console.log('* XPassBook average response time: ', m_XPassBook.total / m_XPassBook.count, 'ms');
		console.log('*');
		console.log('******************************************************************\n');
	}
	
	if (globalSecureId < maxSecureId) {
	
		//vary timing from 2-10 seconds
		var delay = parseInt(2000, 10) + parseInt(Math.floor(Math.random()*maxDelay), 10);
		chunk.timer = setTimeout(chunk, delay);
	}
	

}

//entry point
chunk(); 


