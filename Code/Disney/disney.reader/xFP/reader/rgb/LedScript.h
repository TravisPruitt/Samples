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

    void setEffectName(const char* name);

    virtual int next();
    virtual void reset(unsigned position = 0);
    virtual void getEffectName(std::string& name);
    virtual uint32_t getMinTime();
    virtual bool isInterruptible();
    virtual bool isLocked();
    virtual unsigned getCurrentPosition();

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

    // Hold the sequence name of the effect
    std::string effectName;

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

    // Holds interruptible state of the script
    bool interruptible;

    // Holds the locked state of the script
    bool locked;

    // Hold the xBIO brightness setting
    unsigned xBioBrightness;

    // Indicates whether this script should start off where the previous
    // script (if any) left off.
    bool resumePosition;

    std::vector<std::string> lines;

    // A map of colors and their names as defined in the script
    colormap_t colors;
};


} // namespace Reader


#endif // __LEDSCRIPT__H_
