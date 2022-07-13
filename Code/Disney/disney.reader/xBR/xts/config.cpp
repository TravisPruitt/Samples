/**
    Config.cpp - Configuration file
    November 1, 2011
    Mike Wilson	- Code stolen from Greg Strange

    The config file contents are formatted as Json.  
    See jsoncpp documentation for more information on how to access / modify the JSON data.
*/

// C/C++ Std Lib
#include <cstring>
#include <stdint.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <algorithm>
// System Lib
#include <sys/stat.h>
// Global-App
#include <json/json.h>
#include "test-server.h"
// Local-ish
#include "config.h"
#include "logging.h"

namespace config
{
	Json::Value value;	// The publicly modifiable data, which is stored in the config file as json.
	const char *configFilePath = DEFAULT_CONFIG_FILE;

	// pass in two root objects, recursive function walks the tree, and if it finds 
	// a value in defConf but not the corresponding value in editConf (config file), it copies the value from defConf into editConf
	bool JSONTree(Json::Value & defConf, Json::Value & editConf, int treeDepth = 0);	// do not use treeDepth, only used internally for recursion
	
	void printDepth(int depth)
	{
		for( int tab = 0 ; tab < depth; tab++) 
			logs::conf("   ");
	}
	
	void printJsonVal(Json::Value & val, int depth)
	{	
		printDepth(depth);
		     if(val.isString())	logs::conf("%s (string)", val.asString().c_str() );
		else if(val.isBool())	logs::conf("%d (bool)", val.asBool() );
		else if(val.isInt())	logs::conf("%d (int)", val.asInt() );
		else if(val.isUInt()) 	logs::conf("%u (uint)", val.asUInt() );
		else if(val.isDouble())	logs::conf("%f (double)", val.asDouble() );
		else 					logs::conf("type??"); 
	}
	
	bool JSONTree(Json::Value & defConf, Json::Value & editConf, int treeDepth) 
	{
		bool needs_saved = false;
	
		if(treeDepth == 0)
			needs_saved = false;
		
		if(defConf.isArray())	// contents are an array. We do not recurse the array items, treat as a single value.
		{
			logs::conf("\n"); // close the value line
			printDepth(treeDepth); logs::conf("[\n"); // Print opening { @ depth.
			for(Json::ArrayIndex i = 0; i < defConf.size(); ++i)
			{
				printJsonVal(defConf[i], treeDepth + 1);
				logs::conf("\n");
			}
			printDepth(treeDepth); logs::conf("]\n");			
		}
		else if(defConf.size() == 0 )	// has no members, print value
		{		
			printJsonVal(editConf, 0); 
			logs::conf("\n");
		}
		else 						// Has members, print it's name and then recurse through it's members
		{
			logs::conf("\n"); 
			printDepth(treeDepth); logs::conf("{\n"); // Print opening { @ depth.
						
			Json::ValueIterator iDef = defConf.begin();
			for(; iDef != defConf.end() ; iDef++ ) 
			{
				printDepth(treeDepth + 1);
				logs::conf("%s", iDef.memberName());	

				if(strlen(iDef.memberName()) == 0) // this is an array
				{
					logs::conf(" (ARRAY) - should not occur! ");
				}
				else
				{
					if (!editConf.isMember(iDef.memberName()))
					{
						editConf[iDef.memberName()] = *iDef; // copy default setting
						needs_saved = true;
						logs::conf(" (new) : ");
					}
					else
						logs::conf(" : ");
						
					needs_saved |= JSONTree( *iDef, editConf[iDef.memberName()], treeDepth + 1);
				}
			} 
			printDepth(treeDepth);
			logs::conf("}\n");
		}
		
		return needs_saved;
	}

	void init()
	{

		logs::conf( "init()\n");

		load();	// load the existing config file if it exists.
		
		std::string defaults = DEFAULT;
        
		// read the default values in. If this is updated code from when
		// the configuration file was created, it will update the 
		// config file values that have been added. If the config file
		// did not exist, we reconstruct it here.
		size_t rep;
		while ( (rep = defaults.find('\'') ) != std::string::npos )
			defaults.replace(rep,1,"\"");
			
		std::stringstream deff(defaults);	
		
		Json::Value defJson;				// store json into here
		Json::Reader reader;				// parser object
		if (!reader.parse(deff,defJson))	// parse
		{
			logs::conf("default values:\n%s\n", defaults.c_str());
			ABORT("default_json in incorrect format\n");
		}

		// iterate the whole tree and apply defaults to any missing members.
		bool needs_saved = JSONTree(defJson, value);
		if( needs_saved ) 
			save();
			
		logs::conf("config::init() completed successfully\n");	
	}

	void load()
	{
		// Read in _values saved in the config file
		logs::conf("opening config file <%s>\n", configFilePath);

		std::ifstream infile;
		infile.open(configFilePath, std::ifstream::in);
		
		Json::Reader jsonReader;
		if (infile.fail())
		{
			logs::conf("Config file = %s\n", configFilePath);
			WARNING("Failed open");
		}
		else if (!jsonReader.parse(infile, value, false))
			WARNING("failed parse of config");
		
		infile.close();
		logs::conf("config::load() completed successfully\n");	
	}
	
	/**
		Overwrite the config file with the current data in the "_values" object.
	*/
	void save()
	{
		logs::conf("saving config file <%s>\n", configFilePath);
/*		
			S_IRUSR | S_IWUSR | S_IXUSR
		|	S_IRGRP | S_IWGRP | S_IXGRP
		|	S_IROTH | S_IWOTH | S_IXOTH		
*/		
		std::ofstream outfile;
		outfile.open(configFilePath, std::ofstream::out);
		if (outfile.fail())
		{
			logs::conf("config file = %s\n", configFilePath);
			ABORT("unable to open file for writing");
		}

		Json::StyledStreamWriter jsonWriter;
		jsonWriter.write(outfile, value);
		// std::cout << value;

		outfile.close();
	}

} // namespace config
