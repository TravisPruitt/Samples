/**
 *  @file   LedScript.cpp
 *  @date   Sep, 2012
 *
 *  Copyright (c) 2012, synapse.com
*/


#include <iostream>
#include <fstream>
#include <sstream>
#include <algorithm>

#include "LedScript.h"
//#include "BiometricReader.h"
#include "Sound.h"
#include "log.h"


#define DEFAULT_MIN_TIME 0.0


namespace Reader
{


LedScript::LedScript(RGBCluster* cluster) :
    LedEffect(cluster), timeMode(Duration),
    currentLine(0), firstLine(0), previousTime(0), 
    minTime(DEFAULT_MIN_TIME), interruptible(true),
    xBioBrightness(0)
{
}


bool LedScript::loadFile(const char* filename)
{
    std::ifstream script(filename, std::ifstream::in);
    if (!script.is_open())
        return false;

    return load(script);
}


bool LedScript::loadBuffer(const char *buf, size_t size)
{
    std::stringstream ss;
    ss.write(buf, size);

    return load(ss);
}


void LedScript::reset()
{
    currentLine = firstLine;
    previousTime = 0;
}


int LedScript::next()
{
    if (currentLine >= lines.size())
        return -1;
    
    double time = 0;
    std::string& line = lines[currentLine];

    LOG_TRAFFIC("Processing line %d: %s", currentLine, line.c_str());
    
    // skip comments
    if (line[0] == '#')
    {
        ;// NOP
    }
    // '$' denotes a command
    else if (line[0] == '$')
    {
        handleCommand(line);
    }
    else
    {
        time = parseLine(line);
    }
    
    ++currentLine;

    // Return the time to wait in ms
    return (time*1000);
}

bool LedScript::load(std::istream& script)
{
    firstLine = 0;
    reset();

    std::string line;
    while (!script.eof())
    {
        getline(script, line);

        // Remove trailing carriage returns
        while (line.find ("\r") != std::string::npos)
            line.erase(line.find("\r"), 1);

        lines.push_back(line);
    }

    // Move to first non-command line. This preloads any
    // important front-loaded directives such as $mintime &
    // $notinterruptible and speeds up processing on reset.
    while (isPreloadableLine(lines[currentLine]))
    {
        if (lines[currentLine][0] != '#')
            handleCommand(lines[currentLine]);
        ++currentLine;
        ++firstLine;
    }
    --currentLine;
    --firstLine;

    return true;
}


/*
 *  Simple helper method that determines whether the given line
 *  is preloadable and can be excluded from future processing.
 */
bool LedScript::isPreloadableLine(std::string& line)
{
    // Any non-command/comment automatically disqualifies
    if ( line[0] != '$' && line[0] != '#' )
        return false;

    return (line.find("$sound") == std::string::npos) &&
        (line.find("$repeat") == std::string::npos) &&
        (line.find("$goto") == std::string::npos);
}


void LedScript::handleCommand(const std::string &cmd)
{
    std::stringstream ss;

    if (cmd.find("$color") == 0)
    {
        ss.str(cmd.substr(7));
        handleCmdColor(ss);
    }
    else if (cmd.find("$sound") == 0)
    {
        ss.str(cmd.substr(7));
        handleCmdSound(ss);
    }
    else if (cmd.find("$xbiobrightness") == 0)
    {
        ss.str(cmd.substr(16));
        //handleCmdBioBrightness(ss);
    }
    else if (cmd.find("$xbio") == 0)
    {
        ss.str(cmd.substr(6));
        //handleCmdBio(ss);
    }
    else if (cmd.find("$repeat") == 0)
    {
        reset();
    }
    else if (cmd.find("$goto") == 0)
    {
        ss.str(cmd.substr(6));
        handleCmdGoto(ss);
    }
    else if (cmd.find("$timemode_duration") == 0)
    {
        timeMode = Duration;
    }
    else if (cmd.find("$timemode_absolute") == 0)
    {
        timeMode = Absolute;
    }
    else if (cmd.find("$mintime") == 0)
    {
        ss.str(cmd.substr(9));
        double time;

        ss >> time;
        if (!ss.fail())
            minTime = time;
    }
    else if (cmd.find("$notinterruptible") == 0)
    {
        interruptible = false;
    }
    else
    {
        LOG_DEBUG("Unknown command \"%s\" (%d)", cmd.c_str(), currentLine);
    }

    if (ss.fail())
        LOG_DEBUG("Unable to parse command \"%s\" (%d)", cmd.c_str(), currentLine);
}
    

void LedScript::handleCmdGoto(std::stringstream& ss)
{
    int moveNumber;
    int lineNumber;
    
    // Get and validate the number of lines to move
    ss >> moveNumber;
    if (ss.fail() || moveNumber == 0)
    {
        LOG_DEBUG("$goto: Invalid statement (%d)", currentLine);
        return;
    }

    // Move the to the line before the specified line. The 
    // previous line is necesssary since currentLine will
    // be incremented after finished processing this line.
    lineNumber = currentLine + moveNumber - 1;
    if (lineNumber < 0 || lineNumber >= (int)lines.size())
    {
        LOG_DEBUG("$goto: Out of range parameter (%d)", currentLine);
        return;
    }
    currentLine = lineNumber;
    previousTime = 0;

    // Set previousTime correctly if were are in absolute mode
    if (timeMode == Absolute && currentLine != 0)
    {
        // Find the next line that isn't a comment or command
        while (lineNumber > 0 &&                // Stay in range
               (lines[lineNumber][0] == '$' ||  // ignore commands
                lines[lineNumber][0] == '#'))   // ignore comments
        {
            --lineNumber;
        }

        // If lineNumber == 0 then we're back at the beginning
        // and we keep previousTime set to zero.
        if (lineNumber > 0)
        {
            std::stringstream time_ss(lines[lineNumber]);
            time_ss >> previousTime;
        }
    }
}


void LedScript::handleCmdColor(std::stringstream& ss)
{
    std::string color;
    unsigned r, g, b;
    
    ss >> color;
    ss >> r;
    ss >> g;
    ss >> b;
    if (ss.fail())
    {
        LOG_DEBUG("$color: Unable to parse RGB values (%d)", currentLine);
        return;
    }

    addColor(color, RGBColor(r, g, b));
}


void LedScript::handleCmdSound(std::stringstream& ss)
{
    std::string name;
    getline(ss, name, ',');
    if (ss.fail())
    {
        LOG_DEBUG("$sound: Unable to parse sound name (%d)", currentLine);
        return;
    }

    // Strip .wav extension if it's present
    size_t pos = name.find(".wav");
    if (pos != std::string::npos)
        name.erase(pos, 4);

    Sound::instance()->play(name.c_str());
}


// void LedScript::handleCmdBioBrightness(std::stringstream& ss)
// {
//     unsigned level;
//     ss >> level;
//     if (ss.fail() ||
//         level < MIN_XBIO_BRIGHTNESS ||
//         level > MAX_XBIO_BRIGHTNESS)
//     {
//         LOG_DEBUG("$xbiobrightness: Invalid or out of range parameter (%d)", currentLine);
//         return;
//     }
//     xBioBrightness = level;
// }


// void LedScript::handleCmdBio(std::stringstream& ss)
// {
//     std::string name;
//     getline(ss, name, ',');
//     if (ss.fail())
//     {
//         LOG_DEBUG("$xbio: Unable to parse script name (%d)", currentLine);
//         return;
//     }

//     BiometricReader::instance()->playScript(name.c_str(), (LED_BRIGHTNESS)xBioBrightness);
// }


double LedScript::parseLine(const std::string& line)
{
    std::string s;
    double time;

    unsigned led = 0;
    std::stringstream ss(line);

    // Read 1st item (time)
    getline(ss, s, ',');
    std::stringstream time_ss(s);
    time_ss >> time;
    
    cluster->clearAll();
    while(getline(ss, s, ','))
    {
        if(s != "" && s != "\n" && s != "\r\n" && s!= "\r")
            cluster->setColor(led, findColor(s));
        led++;
    }
    cluster->update();

    double current_time = time;
    if(timeMode == Absolute)
    {
        current_time -= previousTime;
        previousTime = time;
    }

    return current_time;
}


std::string LedScript::cleanColorName(const std::string &s)
{
    // Lowercase s
    std::string data = s;
    std::transform(data.begin(), data.end(), data.begin(), ::tolower);
    
    // Remove all white spaces
    std::string::iterator new_end = std::remove_if(data.begin(), data.end(), ::isspace);
    data.erase(new_end, data.end());
    return data;
}


RGBColor& LedScript::findColor(const std::string &s)
{
    std::string color = cleanColorName(s);
    static RGBColor offColor = RGBColor(0, 0, 0);

    colormap_t::iterator i = colors.find(color);
    if(i == colors.end())
    {
        LOG_DEBUG("Unknown color \"%s\"", color.c_str());
        return offColor;
    }

    return i->second;
}


} // namespace Reader
