"use strict";

var fs = require("fs");
var request = require("request");
var events = require("events");
var assert = require("assert");

function readJSON(fileName) {
    var entries = fs.readFileSync(fileName, 'utf8').split('\n');

    var result = [];
    entries.forEach(
        function (element)
        {
            if (element != "")
                result.push(JSON.parse(element));
        }
    );

    return result;
}

function submitAssignment(urlIDMS, xband, guest, callback, errcallback, callbackData) {

    var xbandPut = {
        shortRangeTag: xband.shortRangeTag,
        longRangeTag: xband.longRangeTag,
        publicId: xband.publicId,
        secureId: xband.secureId,
        bandId: xband.bandId
    };

    request(
        {
            url: urlIDMS + 'xband/' + xband.xbandId + '/' + guest.guestId,
        },
        function(error, response, body) {
            if (error) {
                console.error(error);
                return;
            }

            var xbandId = -1;
            if (response.statusCode != 200 ) {
                errcallback("status code returned " + response.statusCode, callbackData);
                return;
            }

            callback(xband, guest, callbackData);
        }
    );
}

function saveAssignment(xband, guest, callbackdata) {

    if (guest.xbands == undefined) {
        guest.xbands = [];
    }
    guest.xbands.push(xband.xbandId);

    console.log(JSON.stringify(guest));
}

var args = process.argv.splice(2);
if (args.length < 3) {
    console.error("generateAssignments <config> <filenameSubmittedXbands> <filenameSubmittedGuests>\nSubmit xbands/guests for assignment and save the guests objects.");
    return;
}

var configFilename = args[0];
var inputXbandsFilename = args[1];
var inputGuestsFilename = args[2];

var config = require(configFilename);
var xbands = readJSON(inputXbandsFilename);
var guests = readJSON(inputGuestsFilename);

var assignments = Math.min(xbands.length, guests.length);
for (var i = 0; i < assignments; i++) {
    submitAssignment(
        config.url,
        xbands[i],
        guests[i],
        saveAssignment,
        function(err, data) {
            console.error("error: " + err); 
        }
    );
}
