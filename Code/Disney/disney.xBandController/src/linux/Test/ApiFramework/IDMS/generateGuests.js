"use strict";

var fs = require("fs");
var rdata = require("rdata");
var demo = require("demographic");

var config = null;

function generateGuest(config)
{
    var lastName = rdata.element(demo.lastNames());
    var firstName = rdata.element(demo.firstNames());
    var middleName = rdata.element(demo.middleNames());
    var title = rdata.element(demo.titles());
    var suffix = rdata.element(demo.suffixes());

    return {
        swid: [rdata.hex(8), rdata.hex(4), rdata.hex(4), rdata.hex(4), rdata.hex(12)].join('-'),
        name: {
            lastName: lastName,
            firstName: firstName,
            middleName: middleName,
            title: title,
            suffix: suffix,
        },
        countryCode: rdata.element(["US", "BR", "CA"]),
        languageCode: "ENG",
        visitCount: "234",
        emailAddress: firstName + '.' + lastName + '@somewhere.com',
        parentEmail: 'parent.' + firstName + '.' + lastName + '@somewhereelse.com',
        status: "Active",
        avatar: "Stitch",
        gender: rdata.element(["MALE", "FEMALE"]),
        //dateOfBirth: rdata.date(1910, 1, 2011, 12),
        //guestId: filled in by createGuest
    }
}

var numberOfGuests = 1;
var args = process.argv.splice(2);
if (args.length < 1) {
    console.error("Need a configuration file");
    return;
}
config = require(args[0]);

if (args.length > 1) {
    numberOfGuests = parseInt(args[1]);
}

for (var i = 0; i < numberOfGuests; i++)
{
    console.log(JSON.stringify(generateGuest(config)));
}
