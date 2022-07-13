var fs = require('fs');

var lastNames = [];
var middleNames = [];
var firstNames = [];

function readData(fileName) {
    return fs.readFileSync(fileName, 'utf8').split("\n");
}

demographic = module.exports = {
    celebrations: function() {
        return ["Birthday", "Retirement", "Anniversary", "Graduation", "Reunion"];
    },
    lastNames: function() {
        if (lastNames.length == 0) {
            lastNames = readData(__dirname + '/data/LastNames.txt');
        }
        return lastNames;
    },
    middleNames: function() {
        if (middleNames.length == 0) {
            middleNames = readData(__dirname + '/data/MiddleNames.txt');
        }
        return middleNames;
    },
    firstNames: function() {
        if (firstNames.length == 0) {
            firstNames = readData(__dirname + '/data/FirstNames.txt');
        }
        return firstNames;
    },
    titles: function() {
        return ["Mr.", "Mrs", "Ms.", "Senator", ""];
    },
    suffixes: function() {
        return ["", "II", "III", "Esq."];
    },
    countryCodes: function() {
        return ["US", "BR", "CA"];
    }
};
