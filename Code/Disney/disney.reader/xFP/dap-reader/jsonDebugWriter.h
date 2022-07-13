/**
    @file   jsonDebugWriter.h
    @date   Oct 2011

    This is basically the json Styled Writer, except that long strings are abbreviated with elipses.
    Useful for outputting json to the console and log files.
*/


#ifndef __JSON_DEBUG_WRITER_H
#define __JSON_DEBUG_WRITER_H

#include <json/json.h>



namespace Reader
{

class DebugWriter : public Json::Writer
{
public:
    DebugWriter();
    virtual ~DebugWriter(){}

public: // overridden from Writer
    virtual std::string write( const Json::Value &root );

private:
    void writeValue( const Json::Value &value );
    void writeArrayValue( const Json::Value &value );
    bool isMultineArray( const Json::Value &value );
    void pushValue( const std::string &value );
    void writeIndent();
    void writeWithIndent( const std::string &value );
    void indent();
    void unindent();
    std::string normalizeEOL( const std::string &text );

    typedef std::vector<std::string> ChildValues;

    ChildValues childValues_;
    std::string document_;
    std::string indentString_;
    int rightMargin_;
    int indentSize_;
    bool addChildValues_;
};

}

#endif
