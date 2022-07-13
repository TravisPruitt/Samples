/**
 *  @file   Media.h - media configuration
 *  @date   June, 2012
 *  @author Corey Wharton
 *
 *  Functions for installing, removing, and managing reader media resources.
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include <string>
#include "json/json.h"


namespace Reader
{


namespace Media
{


const std::string getMediaHash();
void setMediaHash(const char* hash);

const std::string getIdleEffectName();
void setIdleEffectName(const char* name);

bool removeMediaPackage();
bool installMediaPackage(const char* package);

void getMediaInventory(Json::Value& data);


} // namespace Media


} // namespace Reader
