"use strict";

var http = require('http');
http.globalAgent.maxSockets = 100;

var config = require('./config.js');
var idms = require('./idms.js');


function doTests()
{
    var data = {
        url: config.url,
        xbands: [], // Appended to by generateXband
        xband: -1,  // Updated by generateXband
        xbandsInvalid: [],
        xbandInvalid: -1,
        guests: [], // Appended to by generateGuest
        guest: -1,  // Updated by generateGuest
        guestsInvalid: [],
        guestInvalid: -1,
        celebrations: [], // Appended to by generateCelebration
        celebration: -1, // Updated by generateCelebration
        parties: [],
        party: -1
    };

idms.getConfiguration(
    data,
    [
        idms.getStatus,
        // New xband
        idms.generateXband, idms.createXband, idms.getXbandByShortRangeId,
        idms.getXbandByXbandId, idms.getXbandByLongRangeId, /*idms.getXbandBySecureId,*/
        idms.getXbandByTapIdOld,
        /*idms.changeXbandTypeToPuck,*/ idms.getXbandByShortRangeId, /*idms.changeXbandTypeToGuest,*/ idms.getXbandByShortRangeId,
        // New guest
        idms.generateGuest, idms.createGuest, idms.getGuestById,
        idms.updateGuest, idms.getGuestById, idms.getGuestByEmail,// idms.getGuestsByName,
        idms.getGuestByIdOld,
        idms.assignXbandToGuest, // Associate guest and xband
        idms.getGuestXbands,
        idms.getGuestById,
        idms.generateGuest, idms.createGuest, idms.getGuestById,
        idms.postGuestIdentifiersGxpLinkId, idms.getGuestIdentifiers,
        // BUG idms.postGuestIdentifiersGxpLinkId, idms.getGuestIdentifiers,
       // New xband
        idms.generateXband, idms.createXband, idms.getXbandByShortRangeId,
        idms.assignXbandToGuest, // Associate guest and xband
        idms.getXbandByXbandId,
        idms.getXbandByBandId,
        idms.getGuestXbands,
        idms.getGuestById,
        idms.getGuestById, idms.getGuestById, idms.getGuestById,
        idms.getXbandByXbandId, idms.getXbandByBandId,
        // Bad band
        idms.generateInvalidXband, idms.getXbandByInvalidShortRangeId, idms.getXbandByInvalidXbandId, idms.getXbandByInvalidBandId, idms.getXbandByInvalidLongRangeId, /*idms.getXbandByInvalidSecureId,*/
        // Bad guest
        idms.generateInvalidGuest,
        // Bad stuff
        idms.assignInvalidXbandToValidGuest,
        idms.assignValidXbandToInvalidGuest,
        idms.assignInvalidXbandToInvalidGuest,
        //BUG idms.changeInvalidXbandTypeToPuck,
        //BUG idms.changeValidXbandTypeToInvalidType,
        //BUG idms.changeInvalidXbandTypeToInvalidType,
        idms.unassignInvalidXbandFromValidGuest,
        idms.getXbandByInvalidTapIdOld,
        // New celebration
        idms.generateCelebration, idms.createCelebration,
        idms.getCelebration,
//        idms.updateCelebration, idms.getCelebration,
        idms.getGuestDataByGuestId,
        // New celebration
        idms.generateCelebration, idms.createCelebration,
        idms.getCelebration,
        idms.updateCelebration, idms.getCelebration,
        idms.generateGuest, idms.createGuest, idms.addCelebrationGuest,
        idms.getCelebration,
        idms.generateGuest, idms.createGuest, idms.addCelebrationGuest,
        idms.getCelebration,
        idms.deleteCelebrationGuest,
        idms.getCelebration,
        idms.generateGuest, idms.createGuest, idms.addCelebrationGuest,
        idms.getCelebration,
        // Create a party using current guest
        idms.generateParty, idms.createParty,
        idms.getPartyById, idms.searchPartyByName,
        idms.generateGuest, idms.createGuest, idms.addGuestToParty,
        idms.getPartyById, idms.searchPartyByName,
        idms.generateGuest, idms.createGuest, idms.addGuestToParty,
        idms.getPartyById, idms.searchPartyByName,
        idms.generateGuest, idms.createGuest, idms.addGuestToParty,
        idms.getPartyById,
        idms.generateParty, idms.createParty,
        idms.getVisitById, idms.searchVisitByName,
        idms.generateGuest, idms.createGuest, idms.addGuestToVisit,
        idms.getVisitById, idms.searchVisitByName
    ]
);
}

for (var i = 0; i < 1; i++)
{
    setTimeout(doTests, 0);
}
