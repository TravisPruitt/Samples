/**
 *  @file   LedScript.h
 *  @date   Sep, 2012
 *
 *  Copyright (c) 2012, synapse.com
*/


#ifndef __LEDSCRIPT__H_
#define __LEDSCRIPT__H_


#include "LedEffect.h"
#include "rgbcolor.h"
#include "rgbcluster.h"


namespace Reader
{

/*
 *  LedScript - Plays Led sequences from a script
 */
class LedScript : public LedEffect
{
public:
    LedScript(RGBCluster* cluster);
    
    virtual ~LedScript() {}

    bool loadFile(const char* filename);
    bool loadBuffer(const char* buf, size_t size);

    virtual void reset();
    virtual int next();

    virtual uint32_t getMinTime()
    {
        return (uint32_t)(minTime*1000);
    }

    virtual bool isInterruptible()
    {
        return interruptible;
    }

private:
    bool load(std::istream& script);
    bool isPreloadableLine(std::string& line);

    void handleCommand(const std::string& cmd);
    void handleCmdGoto(std::stringstream& ss);
    void handleCmdColor(std::stringstream& ss);
    void handleCmdSound(std::stringstream& ss);
    void handleCmdBio(std::stringstream& ss);
    void handleCmdBioBrightness(std::stringstream& ss);

    double parseLine(const std::string& line);

    std::string cleanColorName(const std::string &s);
    RGBColor& findColor(const std::string &s);
    void addColor(const std::string& name, RGBColor color)
    {
        colors[cleanColorName(name)] = color;
    }


private:
    enum TimeMode { Absolute, Duration };

    // Specifies absolute or relative time when parsing scripts
    TimeMode timeMode;

    // Currently executing line in the script
    unsigned currentLine;

    // Holds the first non-command line number (for fast reset)
    unsigned firstLine;

    // Time of previous script line not including commands
    // (used in absolute time mode)
    double previousTime;

    // Holds the scripts minimum display time (TAPS only) in seconds
    double minTime;

    // Hold interruptible state of the script
    bool interruptible;

    // Hold the xBIO brightness setting
    unsigned xBioBrightness;

    std::vector<std::string> lines;

    // A map of colors and their names as defined in the script
    colormap_t colors;
};


} // namespace Reader


#endif // __LEDSCRIPT__H_
