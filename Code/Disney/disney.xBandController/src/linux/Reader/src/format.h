/*
 * format.h
 *
 *  Created on: Jul 5, 2011
 *      Author: mvellon
 */

#ifndef FORMAT_H_
#define FORMAT_H_

#include "reader.h"
#include "json.h"

// external declarations
void FormatTime(struct timeval *ptv, char *pszBuf, int cb);
char *FormatJSONEvents(struct Reader *pReader, EVENTINFO *pEvents, size_t sCount, char* (*EventJSON)(struct Reader *pReader, EVENTINFO *pei));

// Write event structure into a REST query suitable for hardware device echo
char *EventBandTapREST(EVENTINFO *pei);
char *EventBandPingREST(EVENTINFO *pei);

// Prepare message indicating device is shutting down
char *EventShutdown(const char *name, size_t eno, struct timeval *tv, size_t timeout);

// xBR diagnostic event
char * 
EventXbrDiagnostic(
    struct Reader *pReader,
    EVENTINFO *pei);

// Write event structure into a string suitable for xBRC
char *EventBandPingJSON(struct Reader *pReader, EVENTINFO *pei);
char *EventBandTapJSON(struct Reader *pReader, EVENTINFO *pei);
char *EventXBRDiagnostic(struct Reader *pReader, EVENTINFO *ei);

char *EventCarPingXML(EVENTINFO *pei);
char *EventXtpGpioJSON(struct Reader *pReader, EVENTINFO *pei);

char *EventXBRJSON(struct Reader *pReader, EVENTINFO *pei);


// Parse JSON data (from test data) into an event structure, failing if there is a provided name that doesn't match the name field
int JSONXBREvent(json_value *event, const char *name, struct timeval*, EVENTINFO *ei);
int JSONBandPingEvent(json_value *event, const char *name, struct timeval*, EVENTINFO *ei);
int JSONBandTapEvent(json_value *event, const char *name, struct timeval*, EVENTINFO *ei);
int JSONCarPingEvent(json_value *event, const char *name, struct timeval*, EVENTINFO *ei);

#endif /* FORMAT_H_ */
