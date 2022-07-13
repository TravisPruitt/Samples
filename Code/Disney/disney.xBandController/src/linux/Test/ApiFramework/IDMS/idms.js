"use strict";

var config = require('./config.js');

var rdata = require('rdata');
var demo = require('demographic');
var test = require('hottest');

var assert = require('assert');
var fs = require('fs');


// Known limits
var maxLongRangeTag = 14;  // Based on code in the xbrc simulator 'Reader'
var maxShortRangeTag = 14; // Based on code in the xbrc simulator 'Reader'

var bignum = require('bignum');
function hexToInteger(hex)
{
    var sum = bignum('0');
    hex = hex.toLowerCase();

    for (var i = 0; i < hex.length; i += 2) // least significant byte is first
    {
        var currentByte = (parseInt(hex.charAt(i) + hex.charAt(i + 1), 16)) >>> 0;
        sum = sum.add(bignum(currentByte).mul(bignum.pow(256, i / 2)));
    }
    return sum.toString();
}

function decimalToHex(d, padding)
{
    var hex = Number(d).toString(16);
    padding = typeof (padding) === "undefined" || padding === null ? padding = 2 : padding;

    while (hex.length < padding)
        hex = "0" + hex;

    return hex.toUpperCase();
}
function checkEqual(o1, o2, message)
{
    if (o2 !== undefined)
    {
        if (o1 != undefined && o1 == o2) {
            test.ok(message);
        }
        else {
            test.fail(message + '(actual value = ' + JSON.stringify(o1) + ', expected value = ' + JSON.stringify(o2));
            assert(true == false);
        }
    }
}

function checkResponse(response, statusCode, message)
{
    assert(response !== undefined);
    assert(response.statusCode !== undefined);

    if (response.statusCode == statusCode)
        test.ok(message + ' (checking response)');
    else
    {
        test.fail(message + ' (response is ' + response.statusCode + ' but should be ' + statusCode + ')');
        assert(true == false, message + ' (aborting due to bad response');
    }
}

function checkXband(oXband, oTemplateXband, message)
{
    checkEqual(oXband.shortRangeTag, oTemplateXband.shortRangeTag, message + ' (checking shortRangeTag)');
    checkEqual(oXband.longRangeTag, oTemplateXband.longRangeTag, message + ' (checking longRangeTag)');
    checkEqual(oXband.xbandId, oTemplateXband.xbandId, message + ' (checking xbandId)');
    assert(oTemplateXband.bandType !== undefined);
    checkEqual(oXband.bandType, oTemplateXband.bandType, message + ' (checking bandType)');
}

function checkGuest(oGuest, guestContainer, message, skipIdentifiers)
{
    var oTemplateGuest = guestContainer.guest;
    checkEqual(oGuest.guestId, oTemplateGuest.guestId, message + ' (checking object guest\'s guestId)');
    checkEqual(oGuest.name.lastName, oTemplateGuest.name.lastName, message + ' (checking object guest\'s name.lastName');
    checkEqual(oGuest.name.firstName, oTemplateGuest.name.firstName, message + ' (checking object guest\'s name.firstName');
    checkEqual(oGuest.name.middleName, oTemplateGuest.name.middleName, message + ' (checking object guest\'s name.middleName');
    checkEqual(oGuest.name.title, oTemplateGuest.name.title, message + ' (checking object guest\'s name.title');
    checkEqual(oGuest.name.suffix, oTemplateGuest.name.suffix, message + ' (checking object guest\'s name.suffix');
    checkEqual(oGuest.visitCount, oTemplateGuest.visitCount, message + ' (checking object guest\'s visitCount');
    checkEqual(oGuest.status, oTemplateGuest.status, message + ' (checking object guest\'s status');
    checkEqual(oGuest.emailAddress, oTemplateGuest.emailAddress, message + ' (checking object guest\'s emailAddress');
    checkEqual(oGuest.parentEmail, oTemplateGuest.parentEmail, message + ' (checking object guest\'s parentEmail');
    checkEqual(oGuest.avatar, oTemplateGuest.avatar, message + ' (checking object guest\'s avatar');
    checkEqual(oGuest.countryCode, oTemplateGuest.countryCode, message + ' (checking object guest\'s countryCode');
    checkEqual(oGuest.languageCode, oTemplateGuest.languageCode, message + ' (checking object guest\'s languageCode');
    checkEqual(oGuest.gender, oTemplateGuest.gender, message + ' (checking object guest\'s gender');
    checkEqual(oGuest.dateOfBirth, oTemplateGuest.dateOfBirth, message + ' (checking object guest\'s dateOfBirth');

    if (skipIdentifiers !== undefined && skipIdentifiers == false)
        checkGuestIdentifierCollection(oGuest.identifiers, guestContainer, message);
}

function checkGuestWithXbands(oGuest, guestContainer, message, skipIdentifiers)
{
    var oTemplateGuest = guestContainer.guest;

    checkGuest(oGuest, guestContainer, message, skipIdentifiers);

    var xbands = guestContainer.xbands.slice(0); // Need copy for removing element each time we find one.
    for (var i in oGuest.xbands) {
        var xbandId = oGuest.xbands[i].xbandId;

        var index = xbands.indexOf(xbandId);
        if (index == -1)
        {
            test.fail(message + ' (xband contains only guests we assigned)');
        }
        else
        {
            test.ok(message + ' (xband contains only guests we assigned)');
            xbands.splice(index, 1);
        }
    }
    if (xbands.length == 0 )
        test.ok(message + ' (xband contains all guests we assigned)');
    else {
        console.log('got ' + JSON.stringify(oGuest.xbands));
        console.log('expected ' + JSON.stringify(guestContainer.xbands));
        console.log('Still have ' + JSON.stringify(xbands));
        test.fail(message + ' (xband contains all guests we assigned)');
    }
}

function checkXViewGuest(oGuest, oGuestContainer, message)
{
    var oTemplateGuest = oGuestContainer.guest;
    var status = 'Inactive';

    //console.log('got ' + JSON.stringify(oGuest));
    //console.log('exp ' + JSON.stringify(oTemplateGuest));
    checkEqual(oGuest.guestId, oTemplateGuest.guestId, message + ' (checking guestId)');
    checkEqual(oGuest.lastName, oTemplateGuest.name.lastName, message + '(checking lastName)');
    checkEqual(oGuest.firstName, oTemplateGuest.name.firstName, message + '(checking firstName)');
    checkEqual(oGuest.countryCode, oTemplateGuest.countryCode, message + '(checking countryCode');
    if (oGuest.active)
        status = 'Active';
    checkEqual(status, oTemplateGuest.status, message + ' (checking status)');
}

function checkGuestIdentifierCollection(guestIdentifierCollection, guestContainer, message)
{
    checkGuestIdentifiers(guestIdentifierCollection.identifiers, guestContainer, message + '(checking guest identifier collection)');

    checkEqual(
        guestIdentifierCollection.links.self.href,
        "/guest/" + guestContainer.guest.guestId + "/identifiers",
        message + ' (guest identifier self link)');

    checkEqual(
        guestIdentifierCollection.links.ownerProfile.href,
        "/guest/" + guestContainer.guest.guestId + "/profile",
        message + ' (guest identifier owner link)');
}

function checkGuestIdentifiers(identifierList, guestContainer, message)
{
    var gxpLinkId = 0;
    var xId = 0;
 
    for (var i in identifierList) {
        var guestIdentifier = identifierList[i];
        var type = guestIdentifier.type;
        var value = guestIdentifier.value;
        if (type == 'gxp-link-id')
        {
            checkEqual(value, guestContainer['gxp-link-id'], message + '(identier list has gxp-link-id)' );
            gxpLinkId++;
        }
        else if (type == 'xid')
        {
            checkEqual(value, guestContainer['xid'], message + '(identier list has xid)' );
            xId++;
        }
        else if (type == 'xband')
        {
            var xbandId = parseInt(value);
            if (guestContainer.xbands.indexOf(xbandId) == -1) {
                test.fail(message + '(checking identifer list element xband)');
            }
            else {
                test.ok(message + '(checking identifier list element xband)');
            }
        }
        else
        {
            assert("Unknown GuestIdentifierPut type '" + type + "'");
        }
    }

    if (guestContainer['gxp-link-id'] !== undefined)
        checkEqual(xId, 1, message + '(identifier list has one gxp-link-id)');

    if (guestContainer['xid'] !== undefined)
        checkEqual(xId, 1, message + '(identifier list has one xid)');
}

function checkGuestsXbands(xbandArray, guestContainer, message)
{
    assert.notEqual(xbandArray, undefined, "xbandArray");
    assert.notEqual(guestContainer.xbands, undefined, "guestContainer.xbands");

    var myXbands = guestContainer.xbands.slice(0); // Need copy for destrutive operations

    assert.notEqual(myXbands, undefined);
    assert.notEqual(xbandArray.length, undefined);
    for (var i = 0; i < xbandArray.length; i++)
    {
        var xband = xbandArray[i];

        var foundIt = false;
        assert.notEqual(myXbands.length, undefined);
        for (var j = 0; j < myXbands.length; j++)
        {
            if (myXbands[j] == xband.xBandRequestId)
            {
                foundIt = true;
                myXbands.splice(j, 1);
                break;
            }
        }
        if (foundIt)
        {
            test.ok(message + '(found xband)');
        }
        else
        {
            test.fail(message + '(found xband)');
        }
    }

    assert.notEqual(myXbands.length, undefined);
    if (myXbands.length > 0)
        test.fail(message + '(found all xbands)');
    else
        test.ok(message + '(found all xbands)');

}
function checkOneViewGuestData(guestData, guestContainer, message)
{
    //console.log("guestData: " + JSON.stringify(guestData));
    //console.log("guestContainer: " + JSON.stringify(guestContainer));

    checkGuestIdentifiers(guestData.guestData.guest.guestIdentifiers, guestContainer, message);
    checkGuestsXbands(guestData.guestData.xBands, guestContainer, message);
}

function checkCelebrationGuests(celebrationGuests, myCelebrationGuests, message)
{
    var myGuests = myCelebrationGuests.slice(0); // Need copy for destrutive operations
    for (var i = 0; i < celebrationGuests.length; i++)
    {
        var guest = celebrationGuests[i];

        var foundIt = false;
        assert.notEqual(myGuests.length, undefined);

        var j = 0;
        for (j = 0; j < myGuests.length; j++)
        {
            if (myGuests[j].firstname == guest.firstname && myGuests[j].lastname == guest.lastname)
            {
                foundIt = true;
                myGuests.splice(j, 1);
                break;
            }
        }
        if (foundIt)
        {
            test.ok(message + '(found guest)');
        }
        else
        {
            test.fail(message + '(found guest)');
            console.log('did not find ' + JSON.stringify(guest));
            console.log('celebrationGuests ' + JSON.stringify(celebrationGuests));
            console.log('my guests are ' + JSON.stringify(myCelebrationGuests));
        }
    }

    assert.notEqual(myGuests.length, undefined);
    if (myGuests.length > 0)
    {
        test.fail(message + '(found all guests)');
        console.log('celebrationGuests ' + JSON.stringify(celebrationGuests));
        console.log('my guests are ' + JSON.stringify(myCelebrationGuests));
    }
    else
        test.ok(message + '(found all guests)');
}

function checkCelebration(celebration, celebrationContainer, message)
{
    console.log('celebration is ' + JSON.stringify(celebration));
    console.log('celebrationContainer is ' + JSON.stringify(celebrationContainer));

    var myCelebration = celebrationContainer.celebration;
    //FIXME HACK BUG I DONT KNOW checkEqual(celebration.name, myCelebration.name, message + ' (checking name)');
    checkEqual(celebration.milestone, myCelebration.milestone, message + ' (checking milestone)');
    checkEqual(celebration.type, myCelebration.type, message + ' (checking type)');
    checkEqual(celebration.month, myCelebration.month, message + ' (checking month)');
    checkEqual(celebration.day, myCelebration.day, message + ' (checking day)');
    checkEqual(celebration.year, myCelebration.year, message + ' (checking year)');
    checkEqual(celebration.comment, myCelebration.comment, message + ' (checking comment)');

    if (celebrationContainer.guests !== undefined && celebrationContainer.guests !== null)
    {
        checkCelebrationGuests(celebration.guests, celebrationContainer.guests, message);
    }
}

function checkParty(oParty, partyContainer, message)
{
    checkEqual(oParty.partyId, partyContainer.party.partyId, message + ' (checking object party\'s partyId)');

    checkEqual(oParty.count, partyContainer.currentGuests.length, message + ' (checking object party\'s count)');
    for (var index in oParty.members)
    {
        var bFoundGuest = true;
        var oGuest = oParty.members[index];

        if (oGuest.guestId == partyContainer.party.primaryGuestId)
        {
            if (oGuest.isPrimary == true) {
                test.ok(message + ' (checking object party\'s primaryGuestId)');
            }
            else {
                test.fail(message + ' (checking object party\'s primaryGuestId)');
            }
        }
        else {
            if (oGuest.isPrimary == false) {
                test.ok(message + ' (checking object party\'s primaryGuestId)');
            }
            else {
                test.fail(message + ' (checking object party\'s primaryGuestId)');
            }
        }

        if (partyContainer.currentGuests.indexOf(oGuest.guestId) == -1) {
            test.fail(message + ' (checking object party\'s members');
        }
        else {
            test.ok(message + ' (checking object party\'s members');
        }
   }
}

var idms = module.exports = {

    getConfiguration: function(testData, nextTest)
    {
        test.remote(
            testData.url + 'configuration',
            function(oConfiguration, response)
            {
                var message = 'configuration returns 200';
                checkResponse(response, 200, message);
                return message;
            },
            function(oConfiguration, response)
            {
                var message = 'configuration databaseURL is not empty';
                assert.notEqual(oConfiguration.databaseURL.length, 0, message);
                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getStatus: function(testData, nextTest)
    {
        test.remote(
            testData.url + 'status',
            function(window, response) 
            {
                var message = 'status is \'Green\'';
                var node = window && window.document.querySelector('status>status');
                var status = node.firstChild.nodeValue;
                assert.equal(status, 'Green', message);
                return message;
            },
            function(window, response) 
            {
                var message = 'databaseVersion is present';
                var node = window && window.document.querySelector('status>databaseVersion');
                assert.notEqual(node, undefined, message);
                return message;
            },
            function(window, response)
            {
                var message = 'hostname is present';
                var node = window && window.document.querySelector('status>hostname');
                assert.notEqual(node, undefined, message);
                return message;
            },
            function(window, response)
            {
                var message = 'startTime is present';
                var node = window && window.document.querySelector('status>startTime');
                assert.notEqual(node, undefined, message);
                return message;
            },
            function(window, response) {
                var message = 'statusMessage is present';
                var node = window && window.document.querySelector('status>statusMessage');
                assert.notEqual(node, undefined, message);
                return message;
            },
            function(window, response) {
                var message = 'version is present';
                var node = window && window.document.querySelector('status>version');
                assert.notEqual(node, undefined, message);
                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    generateXband: function(testData, nextTest)
    {
        var publicid = rdata.string(10, "0123456789"); //rdata.hex(maxLongRangeTag);
        var xbandContainer = {
            assignedGuestId: null,
            update: true,
            xband: {
                shortRangeTag: rdata.hex(maxShortRangeTag),
                bandId: rdata.hex(10),
                secureId: rdata.hex(33),
                longRangeTag: decimalToHex(publicid, 10),
                publicId: publicid,
                bandType: 'Guest'
            }
        }

        console.log("xbandContainer = " + JSON.stringify(xbandContainer));
        testData.xbands.push(xbandContainer);
        testData.xband = testData.xbands.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },

    utilIncrementXband: function(testData, nextTest)
    {
        testData.xband += 1;
        if (testData.xband >= testData.xbands.length)
            testData.xband = testData.xbands.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    utilDecrementXband: function(testData, nextTest)
    {
        testData.xband -= 1;
        if (testData.xband < 0)
            testData.xband = 0;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    createXband: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;

        var xbandPut = {
            bandId: xband.bandId,
            longRangeTag: xband.longRangeTag,
            shortRangeTag: hexToInteger(xband.shortRangeTag),
            secureId: xband.secureId,
            publicId: xband.publicId
        };

        test.remote(
            {
                url: testData.url + 'xband',
                method: 'POST',
                json: xbandPut
            },
            function(window, response)
            {
                var message = 'create good xband';
                checkResponse(response, 201, message);
                assert.notEqual(response.headers.location, 0, message);

                var regexpPattern = new RegExp(testData.url + 'xband/(\\d+)');
                assert.notEqual(regexpPattern, null, message);

                var regexpArray = regexpPattern.exec(response.headers.location);
                assert.notEqual(regexpArray, null, message);

                assert.notEqual(regexpArray[0].length, 0, message);
                xband.xbandId = regexpArray[1];

                console.log("Create band with these values: " + JSON.stringify(xbandPut) + " and shortRangeTag " + xband.shortRangeTag);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    createXband404: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;

        var xbandPut = {
            bandId: xband.bandId,
            longRangeTag: xband.longRangeTag,
            shortRangeTag: xband.shortRangeTag,
            secureId: xband.secureId,
            publicId: xband.publicId
        };

        test.remote(
            {
                url: testData.url + 'xband',
                method: 'POST',
                json: xbandPut
            },
            function(window, response)
            {
                var message = 'create bad xband generating 404';
                checkResponse(response, 404, message);
                assert.notEqual(response.headers.location, 0, message);

                var regexpPattern = new RegExp(testData.url + 'xband/(\\d+)');
                assert.notEqual(regexpPattern, null, message);

                var regexpArray = regexpPattern.exec(response.headers.location);
                assert.notEqual(regexpArray, null, message);

                assert.notEqual(regexpArray[0].length, 0, message);
                xband.xbandId = regexpArray[1];

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getXbandByXbandId: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url: testData.url + 'xband/' + xband.xbandId
            },
            function(oXband, response)
            {
                var message = 'retrieve xband by xband id';

                checkResponse(response, 200, message);

                checkXband(oXband, xband, message);
                assert(oXband.guests !== undefined, 'guests is defined');
                assert(oXband.guests.length !== undefined, 'guests.length is defined');
                if (testData.xbands[testData.xband].assignedGuestId)
                {
                    checkEqual(oXband.guests.length, 1, message + ' (checking object xband\'s guests array has only one guest');

                    checkGuest(oXband.guests[0], testData.guests[testData.guest], message + ' (checking object xband\'s guest element)');
                }
                else
                {
                    checkEqual(oXband.guests.length, 0, message + ' (checking object xband\'s guests array is empty');
                }

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByShortRangeId: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        var update = testData.xbands[testData.xband].update;
        test.remote(
            {
                url: testData.url + 'xband/tapid/' + xband.shortRangeTag
            },
            function(oXband, response)
            {
                var message = 'retrieve XViewXband by short range id';

                console.log("url = " + testData.url + 'xband/tapid/' + xband.shortRangeTag);

                checkResponse(response, 200, message);

                checkXband(oXband, xband, message);

                if (update)
                {
                    testData.xbands[testData.xband].xband = oXband;
                    update = false;
                }

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByBandId:function (testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url: testData.url + 'xband/bandid/' + xband.bandId
            },
            function(oXband, response)
            {
                var message = 'retrieve Xband by band id';

                checkResponse(response, 200, message);

                checkXband(oXband, xband, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByLongRangeId:function (testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url: testData.url + 'xband/lrid/' + xband.longRangeTag
            },
            function(oXband, response)
            {
                var message = 'retrieve Xband by lrid';

                checkResponse(response, 200, message);

                checkXband(oXband, xband, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandBySecureId: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url: testData.url + 'xband/secureid/' + xband.secureId
            },
            function(oXband, response)
            {
                var message = 'retrieve Xband by secureId';

                checkResponse(response, 200, message);

                checkXband(oXband, xband, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    assignXbandToGuest: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        var guestId = testData.guests[testData.guest].guest.guestId;
        test.remote(
            {
                url : testData.url + 'xband/' + xband.xbandId + '/' + guestId
            },
            function(window, response)
            {
                var message = 'assign xband to guest via GET';
                checkResponse(response, 200, message);

                testData.xbands[testData.xband].assignedGuestId = guestId;
                testData.guests[testData.guest].xbands.push(xband.xbandId);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    changeXbandTypeToPuck: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url : testData.url + 'xband/updateType/' + xband.xbandId + '/Puck'
            },
            function(window, response)
            {
                var message = 'change type of xband to puck';
                checkResponse(response, 200, message);

                xband.bandType = 'Puck';
                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    changeXbandTypeToGuest: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url : testData.url + 'xband/updateType/' + xband.xbandId + '/Guest'
            },
            function(window, response)
            {
                var message = 'change type of xband to guest';
                checkResponse(response, 200, message);

                xband.bandType = 'Guest';
                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    unassignXband: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        var myUrl = testData.url + 'xband/unassign/' + xband.xbandId + "/" + xband.guestId;

        test.remote(
            myUrl,
            function(window, response)
            {
                var message = 'unassign xbandid/guest id';

                checkResponse(response, 200, message);
                testData.xbands[testData.xband].assignedGuestId = null;

                if (nextTest !== undefined && nextTest.length !== 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getXbandByTapIdOld: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url: testData.url + 'xbands/tapid/' + xband.shortRangeTag
            },
            function(oXband, response)
            {
                var message = 'retrieve XViewXband by tap ID';

                checkResponse(response, 200, message);

                assert.equal(
                    oXband.tapId,
                    xband.shortRangeTag,
                    'retrieved xband matches tap id');
                assert.equal(
                    oXband.lrid,
                    xband.longRangeTag,
                    'retrieved xband matches long range id');
                assert.equal(
                    oXband.xbandId,
                    xband.xbandId,
                    'retrieved xband matches xband id');

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    generateInvalidXband: function(testData, nextTest)
    {
        var xbandContainer = {
            assignedGuestId: null,
            update: true,
            xband: {
                shortRangeTag: rdata.hex(maxShortRangeTag),
                bandId: rdata.hex(10),
                secureId: rdata.hex(33),
                longRangeTag: rdata.hex(maxLongRangeTag),
                publicId: rdata.hex(14),
                xbandId: 9999999,
                bandType: 'Guest'
            }
        }

        testData.xbandsInvalid.push(xbandContainer);
        testData.xbandInvalid = testData.xbandsInvalid.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    getXbandByInvalidXbandId: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url: testData.url + 'xband/' + xband.xbandId
            },
            function(oXband, response)
            {
                var message = 'retrieve xband by INVALID xband id';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByInvalidShortRangeId: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url: testData.url + 'xband/tapid/' + xband.shortRangeTag
            },
            function(oXband, response)
            {
                var message = 'retrieve XViewXband by INVALID short range id';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByInvalidBandId:function (testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url: testData.url + 'xband/bandid/' + xband.bandId
            },
            function(oXband, response)
            {
                var message = 'retrieve Xband by INVALID band id';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByInvalidLongRangeId:function (testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url: testData.url + 'xband/lrid/' + xband.longRangeTag
            },
            function(oXband, response)
            {
                var message = 'retrieve Xband by INVALID lrid';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getXbandByInvalidSecureId: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url: testData.url + 'xband/secureid/' + xband.secureId
            },
            function(oXband, response)
            {
                var message = 'retrieve Xband by INVALID secureId';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    assignInvalidXbandToValidGuest: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        var guestId = testData.guests[testData.guest].guest.guestId;
        test.remote(
            {
                url : testData.url + 'xband/' + xband.xbandId + '/' + guestId
            },
            function(window, response)
            {
                var message = 'assign INVALID xband to valid guest via GET';
                checkResponse(response, 500, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    assignValidXbandToInvalidGuest: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        var guestId = testData.guestsInvalid[testData.guestInvalid].guest.guestId;
        test.remote(
            {
                url : testData.url + 'xband/' + xband.xbandId + '/' + guestId
            },
            function(window, response)
            {
                var message = 'assign valid xband to INVALID guest via GET';
                checkResponse(response, 500, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    assignInvalidXbandToInvalidGuest: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        var guestId = testData.guestsInvalid[testData.guestInvalid].guest.guestId;
        test.remote(
            {
                url : testData.url + 'xband/' + xband.xbandId + '/' + guestId
            },
            function(window, response)
            {
                var message = 'assign INVALID xband to INVALID guest via GET';
                checkResponse(response, 500, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    changeInvalidXbandTypeToPuck: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url : testData.url + 'xband/updateType/' + xband.xbandId + '/Puck'
            },
            function(window, response)
            {
                var message = 'change type of INVALID xband to puck';
                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    changeValidXbandTypeToInvalidType: function(testData, nextTest)
    {
        var xband = testData.xbands[testData.xband].xband;
        test.remote(
            {
                url : testData.url + 'xband/updateType/' + xband.xbandId + '/Balloon'
            },
            function(window, response)
            {
                var message = 'change type of valid xband to invalid type';
                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    changeInvalidXbandTypeToInvalidType: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url : testData.url + 'xband/updateType/' + xband.xbandId + '/Balloon'
            },
            function(window, response)
            {
                var message = 'change type of invalid xband to invalid';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    unassignInvalidXbandFromValidGuest: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        var myUrl = testData.url + 'xband/unassign/' + xband.xbandId + "/" + xband.guestId;

        test.remote(
            myUrl,
            function(window, response)
            {
                var message = 'unassign INVALID xbandid from valid guest id';

                checkResponse(response, 404, message);

                if (nextTest !== undefined && nextTest.length !== 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getXbandByInvalidTapIdOld: function(testData, nextTest)
    {
        var xband = testData.xbandsInvalid[testData.xbandInvalid].xband;
        test.remote(
            {
                url: testData.url + 'xbands/tapid/' + xband.shortRangeTag
            },
            function(oXband, response)
            {
                var message = 'retrieve XViewXband by INVALID tap ID';

                checkResponse(response, 404, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    generateCelebration: function(testData, nextTest)
    {
        var celebrationContainer = {
                guestContainer: testData.guests[testData.guest],
                xid: testData.guests[testData.guest].xid,
                role: "CELEBRANT", //PARTICIPANT, SUPRISE_CELEBRANT
                guests: [],
            celebration: {
                name: "Happy " + rdata.element(demo.celebrations()),
                milestone: "18th Birthday",
                type: "Birthday",
                date: "2012-01-03",
                startDate: "2012-01-03",
                endDate: "2012-01-03",
                recognitionDate: "2012-01-03",
                comment: "We has comment"
            }
        };

        testData.celebrations.push(celebrationContainer);
        testData.celebration = testData.celebrations.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    createCelebration: function(testData, nextTest)
    {
        var celebrationContainer = testData.celebrations[testData.celebration];
        var myCelebration = celebrationContainer.celebration;

        var postCelebration = {
            xid: celebrationContainer.xid,
            role: celebrationContainer.role,
            name: myCelebration.name,
            milestone: myCelebration.milestone,
            type: myCelebration.type,
            date: myCelebration.date,
            startDate: myCelebration.startDate,
            endDate: myCelebration.endDate,
            recognitionDate: myCelebration.recognitionDate,
            comment: myCelebration.comment
        }
        test.remote(
            {
                url: testData.url + 'celebration',
                method: 'POST',
                json: postCelebration
            },
            function(window, response)
            {
                var message = 'create a new celebration';
                checkResponse(response, 201, message);

                var regexpPattern = new RegExp(testData.url + 'celebration/id;celebration\-id=(\\d+)');
                assert.notEqual(regexpPattern, null, message);

                var regexpArray = regexpPattern.exec(response.headers.location);
                assert.notEqual(regexpArray, null, message);

                assert.notEqual(regexpArray[0].length, 0, message);
                celebrationContainer.celebrationId = regexpArray[1];

                var celebrationGuest = {
                    role: 'CELEBRANT',
                    firstname: celebrationContainer.guestContainer.guest.name.firstName,
                    lastname: celebrationContainer.guestContainer.guest.name.lastName
                };

                celebrationContainer.guests.push(celebrationGuest);


                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    updateCelebration: function(testData, nextTest)
    {
        var celebrationContainer = testData.celebrations[testData.celebration];
        var myCelebration = celebrationContainer.celebration;
        var celebrationPut = {
            celebrationId: celebrationContainer.celebrationId,
            name: 'Updated: ' + myCelebration.name,
            milestone: myCelebration.milestone,
            type: myCelebration.type,
            date: myCelebration.date,
            startDate: myCelebration.startDate,
            endDate: myCelebration.endDate,
            recognitionDate: myCelebration.recognitionDate,
            comment:  'Updated: ' + myCelebration.comment
        };
        test.remote(
            {
                url : testData.url + 'celebration',
                method : 'PUT',
                json : celebrationPut
            },
            function(window, response)
            {
                var message = 'update an existing celebration';
                checkResponse(response, 200, message);

                myCelebration.name = celebrationPut.name;
                myCelebration.comment = celebrationPut.comment;

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    addCelebrationGuest: function(testData, nextTest)
    {
        var celebrationContainer = testData.celebrations[testData.celebration];
        var guestContainer = testData.guests[testData.guest];
        var newGuest = {
            celebrationId: celebrationContainer.celebrationId,
            xid: guestContainer['xid'],
            role: "PARTICIPANT",
        };
        test.remote(
            {
                url: testData.url + 'celebration/guest',
                method: 'POST',
                json: newGuest
            },
            function(window, response)
            {
                var message = 'add guest (PARTICIPANT) to celebration';

                checkResponse(response, 200, message);

                var celebrationGuest = {
                    role: 'PARTICIPANT',
                    firstname: guestContainer.guest.name.firstName,
                    lastname: guestContainer.guest.name.lastName,
                    xid: guestContainer['xid']
                };

                celebrationContainer.guests.push(celebrationGuest);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    deleteCelebrationGuest: function(testData, nextTest)
    {
        var celebrationContainer = testData.celebrations[testData.celebration];
        var guest = celebrationContainer.guests.pop();
        var oldGuest = {
            celebrationId: celebrationContainer.celebrationId,
            xid: guest['xid']
        };
        test.remote(
            {
                url: testData.url + 'celebration/guest',
                method: 'DELETE',
                json: oldGuest
            },
            function(window, response)
            {
                var message = 'remove guest from celebration';

                checkResponse(response, 200, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getCelebration: function(testData, nextTest)
    {
        var celebrationContainer = testData.celebrations[testData.celebration];
        var myCelebration = celebrationContainer.celebration;
        test.remote(
            {
                url: testData.url + 'celebration/id;celebration-id=' + celebrationContainer.celebrationId
            },
            function(celebration, response)
            {
                var message = 'retrieve celebration by celebration id';
                checkResponse(response, 200, message);

                //console.log("url = " + testData.url + 'celebration/id;celebration-id=' + celebrationContainer.celebrationId + "\n" + "returned: " + JSON.stringify(celebration));

                checkCelebration(celebration, celebrationContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    generateGuest: function(testData, nextTest)
    {
        var guestContainer = {
            xbands: [],
            guest: {
                swid: [rdata.hex(8), rdata.hex(4), rdata.hex(4), rdata.hex(4), rdata.hex(12)].join('-'),
                name: {
                    lastName: rdata.element(demo.lastNames()),
                    firstName: rdata.element(demo.firstNames()),
                    middleName: rdata.element(demo.middleNames()),
                    title: rdata.element(demo.titles()),
                    suffix: rdata.element(demo.suffixes()),
                },
                countryCode: rdata.element(["US", "BR", "CA"]),
                languageCode: "ENG",
                visitCount: "234",
                emailAddress: Math.random() + "@random" + Math.random() + ".com",
                parentEmail: "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                status: "Active",
                avatar: "Stitch",
                gender: rdata.element(["MALE", "FEMALE"]),
                //dateOfBirth: rdata.date(1910, 1, 2011, 12),
                //guestId: filled in by createGuest
            },
            'gxp-link-id': rdata.string(6, "123456789"),
            'transactional-guest-id': rdata.string(6, "123456789")
        };

        guestContainer.xid = guestContainer.guest.swid.replace(/\-/g, '');
        console.log('guest = ' + JSON.stringify(guestContainer));
        testData.guests.push(guestContainer);
        testData.guest = testData.guests.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    utilIncrementGuest: function(testData, nextTest)
    {
        testData.guest += 1;
        if (testData.guest >= testData.guests.length)
            testData.guest = testData.guests.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    utilDecrementGuest: function(testData, nextTest)
    {
        testData.guest -= 1;
        if (testData.guest < 0)
            testData.guest = 0;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    createGuest: function(testData, nextTest)
    {
        var newGuest = testData.guests[testData.guest].guest;
        test.remote(
            {
                url: testData.url + 'guest',
                method: 'POST',
                json: newGuest
            },
            function(window, response)
            {
                var message = 'create a new guest';

                checkResponse(response, 201, message);
                assert.notEqual(response.headers.location, 0, message);

                var regexpPattern = new RegExp(testData.url + 'guest/(\\d+)');
                assert.notEqual(regexpPattern, null, message);

                var regexpArray = regexpPattern.exec(response.headers.location);
                assert.notEqual(regexpArray, null, message);

                assert.notEqual(regexpArray[0].length, 0, message);
                assert(regexpArray[1] != undefined, 'createGuest: ' + response.headers.location);
                newGuest.guestId = regexpArray[1];

                if (nextTest !== undefined && nextTest.length !== 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    updateGuest: function(testData, nextTest)
    {
        var oTemplateGuest = testData.guests[testData.guest].guest;
        oTemplateGuest.visitCount = (parseInt(oTemplateGuest.visitCount) + 1).toString();
        oTemplateGuest.avatar = "Milo";
        test.remote(
            {
                url: testData.url + 'guest/',
                method: 'PUT',
                json: oTemplateGuest
            },
            function(window, response)
            {
                var message = 'update an existing guest';
                checkResponse(response, 204, message);

                if (nextTest !== undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getGuestById: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        assert(guestContainer.guest.guestId !== undefined, 'getGuestById');
        test.remote(
            {
                url: testData.url + 'guest/' + guestContainer.guest.guestId
            },
            function(oGuest, response)
            {
                var message = 'get guest by guest ID';

                checkResponse(response, 200, message);

                console.log("url is " + testData.url + 'guest/' + guestContainer.guest.guestId + '\nreturned ' + JSON.stringify(oGuest));

                checkGuestWithXbands(oGuest, guestContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getGuestByEmail: function (testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        test.remote(
            {
                url: testData.url + 'guest/searchEmail/' + guestContainer.guest.emailAddress
            },
            function(oGuest, response)
            {
                var message = 'get guest by email';

                checkResponse(response, 200, message);

                checkGuest(oGuest, guestContainer, message, true);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getGuestXbands: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        var oTemplateGuest = guestContainer.guest;
        test.remote(
            {
                url: testData.url + 'guest/' + oTemplateGuest.guestId + '/xbands'
            },
            function(xbandCollection, response)
            {
                var message = 'get guest xbands';

                checkResponse(response, 200, message);

                console.log("url = " + testData.url + 'guest/' + oTemplateGuest.guestId + '/xbands' + '\nreturned ' + JSON.stringify(xbandCollection) + '\nguestContainer = ' + JSON.stringify(guestContainer));

                for (var i in xbandCollection.xbands) {
                    var xbandId = xbandCollection.xbands[i].xbandId;
                    assert.notEqual(guestContainer.xbands.indexOf(xbandId), -1, message);
                }

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    postGuestIdentifiersTransactionalGuestId: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        var oTemplateGuest = testData.guests[testData.guest].guest;
        var guestIdentifierPut = {
            'identifier-type': "transactional-guest-id",
            'identifier-value': guestContainer['transactional-guest-id']
        };
        test.remote(
            {
                url: testData.url + 'guest/' + oTemplateGuest.guestId + '/identifiers',
                method: 'POST',
                json: guestIdentifierPut
            },
            function(window, response)
            {
                var message = 'post xid';
                var duplicate = guestContainer['transactional-guest-id-posted'];
                if (duplicate !== true)
                {
                    message += '(first post)';
                    checkResponse(response, 201, message);
                }
                else
                {
                    message += '(duplicate post)';
                    checkResponse(response, 400, message);
                }
                assert.notEqual(response.headers.location, 0, message);

                guestContainer['transactional-guest-id-posted'] = true;

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    postGuestIdentifiersGxpLinkId: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        var duplicate = guestContainer['gxp-link-id-posted'];
        var oTemplateGuest = testData.guests[testData.guest].guest;
        if (testData.guest > 0 && duplicate === true)
        {
            oTemplateGuest = testData.guests[testData.guest - 1].guest;
        }

        var guestIdentifierPut = {
            'identifier-type': "gxp-link-id",
            'identifier-value': guestContainer['gxp-link-id']
        };
        console.log('guestContainer = ' + JSON.stringify(guestContainer));
        console.log('oTemplateGuest = ' + JSON.stringify(oTemplateGuest));
        test.remote(
            {
                url: testData.url + 'guest/' + oTemplateGuest.guestId + '/identifiers',
                method: 'POST',
                json: guestIdentifierPut
            },
            function(window, response)
            {
                var message = 'post gxp-link-id';
                if (duplicate !== true)
                {
                    message += '(first post)';
                    checkResponse(response, 201, message);
                }
                else
                {
                    message += '(duplicate post)';
                    checkResponse(response, 400, message);
                }
                assert.notEqual(response.headers.location, 0, message);

                guestContainer['gxp-link-id-posted'] = true;

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getGuestIdentifiers: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        var oGuest = testData.guests[testData.guest].guest;
        test.remote(
            {
                url: testData.url + 'guest/' + guestContainer.guest.guestId + '/identifiers'
            },
            function(guestIdentifierCollection, response)
            {
                var message = 'get guest identifiers';

                checkResponse(response, 200, message);

                console.log("url = " + testData.url + 'guest/' + guestContainer.guest.guestId + '/identifiers' + "\nresult = " + JSON.stringify(guestIdentifierCollection));

                checkGuestIdentifierCollection(guestIdentifierCollection, guestContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getGuestsByName: function(testData, nextTest)
    {
        var oTemplateGuest = testData.guests[testData.guest].guest;
        test.remote(
            {
                url: testData.url + 'guest/search/' + oTemplateGuest.name.firstName + "_" + oTemplateGuest.name.lastName
            },
            function(oNames, response)
            {
                var message = 'IDMS/guest/search/' + oTemplateGuest.name.firstName + "_" + oTemplateGuest.name.lastName;

                checkResponse(response, 200, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getGuestByIdOld: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        var oTemplateGuest = guestContainer.guest;
        test.remote(
            {
                url: testData.url + 'guests/' + oTemplateGuest.guestId
            },
            function(oGuest, response)
            {
                var message = 'retrieve Guest by guest ID (old)';

                checkResponse(response, 200, message);

                checkXViewGuest(oGuest, guestContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getGuestDataByGuestId: function(testData, nextTest)
    {
        var guestContainer = testData.guests[testData.guest];
        var myGuest = guestContainer.guest;
        test.remote(
            {
                url: testData.url + 'guest-data?' + 'guest-id-type=guestId' + '&guest-id-value=' + myGuest.guestId + '&start-date-time=unused' + '&end-date-time=unused'
            },
            function(guestData, response)
            {
                var message = 'retrieve GuestData by guest ID';

                checkResponse(response, 200, message);

                checkOneViewGuestData(guestData, guestContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    generateInvalidGuest: function(testData, nextTest)
    {
        var guestContainer = {
            xbands: [],
            guest: {
                swid: [rdata.hex(8), rdata.hex(4), rdata.hex(4), rdata.hex(4), rdata.hex(12)].join('-'),
                name: {
                    lastName: rdata.element(demo.lastNames()),
                    firstName: rdata.element(demo.firstNames()),
                    middleName: rdata.element(demo.middleNames()),
                    title: rdata.element(demo.titles()),
                    suffix: rdata.element(demo.suffixes()),
                },
                countryCode: rdata.element(["US", "BR", "CA"]),
                languageCode: "ENG",
                visitCount: "234",
                emailAddress: Math.random() + "@random" + Math.random() + ".com",
                parentEmail: "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                status: "Active",
                avatar: "Stitch",
                gender: rdata.element(["MALE", "FEMALE"]),
                //dateOfBirth: rdata.date(1910, 1, 2011, 12),
                //guestId: filled in by createGuest
            }
        };

        guestContainer.xid = guestContainer.guest.swid.replace(/\-/g, '');
        //console.log('guest = ' + JSON.stringify(guestContainer));
        guestContainer.guest.guestId = 999999;
        testData.guestsInvalid.push(guestContainer);
        testData.guestInvalid = testData.guestsInvalid.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    generateParty: function(testData, nextTest)
    {
        var partyContainer = {
            party: {
                partyName: rdata.element(demo.lastNames()) + rdata.hex(5),
            },
            currentGuests: [],
        };

        testData.parties.push(partyContainer);
        testData.party = testData.parties.length - 1;

        if (nextTest != undefined && nextTest.length != 0)
        {
            var f = nextTest.shift();
            f(testData, nextTest);
        }
    },
    createParty: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        var party = partyContainer.party;
        var guestId = testData.guests[testData.guest].guest.guestId;
        var newParty = {
            primaryGuestId: guestId,
            partyName: party.partyName
        };

        test.remote(
            {
                url: testData.url + 'party',
                method: 'POST',
                json: newParty
            },
            function(window, response)
            {
                var message = 'create a new party';

                checkResponse(response, 201, message);
                assert.notEqual(response.headers.location, 0, message);

                var regexpPattern = new RegExp(testData.url + 'party/(\\d+)');
                assert.notEqual(regexpPattern, null, message);

                var regexpArray = regexpPattern.exec(response.headers.location);
                assert.notEqual(regexpArray, null, message);

                assert.notEqual(regexpArray[0].length, 0, 'url matches regex');
                party.partyId = regexpArray[1];
                party.primaryGuestId = guestId;
                partyContainer.currentGuests.push(parseInt(guestId));

                if (nextTest !== undefined && nextTest.length !== 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    addGuestToParty: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        var guestId = testData.guests[testData.guest].guest.guestId;
        var newGuest = {
            guestId: guestId,
            partyId: partyContainer.party.partyId
        };

        test.remote(
            {
                url: testData.url + 'party/guest',
                method: 'POST',
                json: newGuest
            },
            function(window, response)
            {
                var message = 'add guest to party';

                checkResponse(response, 201, message);

                partyContainer.currentGuests.push(parseInt(guestId));

                if (nextTest !== undefined && nextTest.length !== 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getPartyById: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        test.remote(
            {
                url: testData.url + 'party/' + partyContainer.party.partyId
            },
            function(oParty, response)
            {
                var message = 'retrieve party by party id';

                checkResponse(response, 200, message);

                checkParty(oParty, partyContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getPartyGuestsById: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        test.remote(
            {
                url: testData.url + 'party/' + partyContainer.party.partyId + '/guests'
            },
            function(partyMembers, response)
            {
                var message = 'retrieve Party by party ID';

                checkResponse(response, 200, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    searchPartyByName: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        var party = partyContainer.party;
        test.remote(
            {
                url: testData.url + 'party/search/' + party.partyName
            },
            function(oParty, response)
            {
                var message = 'search for Party by name';

                checkResponse(response, 200, message);

                checkParty(oParty, partyContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    // vist extends party
    addGuestToVisit: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        var guestId = testData.guests[testData.guest].guest.guestId;
        var newGuest = {
            guestId: guestId,
            partyId: partyContainer.party.partyId
        };

        test.remote(
            {
                url: testData.url + 'visit/guest',
                method: 'POST',
                json: newGuest
            },
            function(window, response)
            {
                var message = 'add guest to visit';

                checkResponse(response, 201, message);

                partyContainer.currentGuests.push(parseInt(guestId));

                if (nextTest !== undefined && nextTest.length !== 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }
                return message;
            }
        );
    },
    getVisitById: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        test.remote(
            {
                url: testData.url + 'visit/' + partyContainer.party.partyId
            },
            function(oParty, response)
            {
                var message = 'retrieve visit by party id';

                checkResponse(response, 200, message);

                checkParty(oParty, partyContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    getVisitGuestsById: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        test.remote(
            {
                url: testData.url + 'visit/' + partyContainer.party.partyId + '/guests'
            },
            function(partyMembers, response)
            {
                var message = 'retrieve visit guests by party ID';

                checkResponse(response, 200, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    },
    searchVisitByName: function(testData, nextTest)
    {
        var partyContainer = testData.parties[testData.party];
        var party = partyContainer.party;
        test.remote(
            {
                url: testData.url + 'visit/search/' + party.partyName
            },
            function(oParty, response)
            {
                var message = 'search for visit by name';

                checkResponse(response, 200, message);

                checkParty(oParty, partyContainer, message);

                if (nextTest != undefined && nextTest.length != 0)
                {
                    var f = nextTest.shift();
                    f(testData, nextTest);
                }

                return message;
            }
        );
    }
};



