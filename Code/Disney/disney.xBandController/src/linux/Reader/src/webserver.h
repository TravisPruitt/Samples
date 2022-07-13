/*
 * webserver.h
 *
 *  Created on: Jun 22, 2011
 *      Author: mvellon
 */

#ifndef WEBSERVER_H_
#define WEBSERVER_H_

#include "reader.h"

int CreateWebServer(struct Reader*);
int FreeWebServer(struct Reader*);

#endif /* WEBSERVER_H_ */
