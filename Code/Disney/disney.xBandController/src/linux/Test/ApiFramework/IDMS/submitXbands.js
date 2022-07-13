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

function submitXband(urlIDMS, xband, callback, errcallback, callbackData) {

    var xbandPut = {
        shortRangeTag: xband.shortRangeTag,
        longRangeTag: xband.longRangeTag,
        publicId: xband.publicId,
        secureId: xband.secureId,
        bandId: xband.bandId
    };

    if (xbandPut.publicId == undefined) xbandPut.publicId = xband.shortRangeTag;
    if (xbandPut.secureId == undefined) xbandPut.secureId = xband.shortRangeTag;
    if (xbandPut.bandId == undefined) xbandPut.bandId = xband.shortRangeTag;

    request(
        {
            url: urlIDMS + 'xband',
            method: 'POST',
            json: xbandPut
        },
        function(error, response, body) {
            if (error) {
                console.error(error);
                return;
            }

            var xbandId = -1;
            if (response.statusCode != 201 ) {
                errcallback("status code returned " + response.statusCode, callbackData);
                return;
            }

            assert.notEqual(response.headers.location, 0, "location");

            var regexpPattern = new RegExp('http:.*xband/(\\d+)');
            assert.notEqual(regexpPattern, null, "regexpPattern");

            var regexpArray = regexpPattern.exec(response.headers.location);
            assert.notEqual(regexpArray, null, "regexpArray");

            assert.notEqual(regexpArray[0].length, 0, "got match");
            xbandId = regexpArray[1];

            callback(xband, xbandId, callbackData);
        }
    );
}

function saveXband(xband, xbandId, callbackData) {
    xband.xbandId = xbandId;
    console.log(JSON.stringify(xband));
}

var args = process.argv.splice(2);
if (args.length < 2) {
    console.error("submitXbands <conifg> <inputFilename>\nSubmit an xband to IDMS and save the resulting data.");
    return;
}

var configFilename = args[0];
var inputFilename = args[1];

var config = require(configFilename);
var xbands = readJSON(inputFilename);

xbands.forEach(
    function (xband) {
        submitXband(config.url, xband, saveXband, function(err, data) {console.error("Error: " + err); });
    }
);
