#!/bin/sh

rm -f localguests localxbands localguestssubmitted localxbandssubmitted localguestsassigned

node ./generateGuests.js ./local-config.js 10 > localguests
node ./generateXbands.js ./local-config.js 10 > localxbands

node ./submitGuests.js ./local-config.js localguests > localguestssubmitted
node ./submitXbands.js ./local-config.js localxbands > localxbandssubmitted

node ./submitAssignments.js ./local-config.js localxbandssubmitted localguestssubmitted > localguestsassigned
