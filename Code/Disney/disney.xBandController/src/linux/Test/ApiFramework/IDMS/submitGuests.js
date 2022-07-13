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

function submitGuest(urlIDMS, guest, callback, errcallback, callbackData) {

    request(
        {
            url: urlIDMS + 'guest',
            method: 'POST',
            json: guest
        },
        function(error, response, body) {
            if (error) {
                console.error(error);
                return;
            }

            var guestId = -1;
            if (response.statusCode != 201 ) {
                errcallback("status code returned " + response.statusCode, callbackData);
                return;
            }

            assert.notEqual(response.headers.location, 0, "location");

            var regexpPattern = new RegExp('http:.*guest/(\\d+)');
            assert.notEqual(regexpPattern, null, "regexpPattern");

            var regexpArray = regexpPattern.exec(response.headers.location);
            assert.notEqual(regexpArray, null, "regexpArray");

            assert.notEqual(regexpArray[0].length, 0, "got match");
            guestId = regexpArray[1];

            callback(guest, guestId, callbackData);
        }
    );
}

function saveGuest(guest, guestId, callbackData) {
    guest.guestId = guestId;
    console.log(JSON.stringify(guest));
}

var args = process.argv.splice(2);
if (args.length < 2) {
    console.error("submitGuests <config> <filename>\nSubmit a guest for creation to IDMS and save the resulting data.");
    return;
}

var configFilename = args[0];
var inputFilename = args[1];

var config = require(configFilename);
var guests = readJSON(inputFilename);

guests.forEach(
    function (guest) {
        submitGuest(config.url, guest, saveGuest, function(err, data) {console.error("Error: " + err); });
    }
);
