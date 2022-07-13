"use strict";

var fs = require("fs");

var config = null;

function generateXband(config)
{
    return {
        shortRangeTag: config.xband.shortRangeTag(),
        bandId: config.xband.bandId(),
        secureId: config.xband.secureId(),
        longRangeTag: config.xband.longRangeTag(),
        publicId: config.xband.publicId()
    };
}

var numberOfXbands = 1;

var args = process.argv.splice(2);
if (args.length < 1) {
    console.error("Need a configuration file");
    return;
}

if (args.length > 1) {
    numberOfXbands = parseInt(args[1]);
}

config = require(args[0]);
for (var i = 0; i < numberOfXbands; i++)
{
    console.log(JSON.stringify(generateXband(config)));
}
