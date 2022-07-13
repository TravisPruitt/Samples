#ifndef __IHEX_H__
#define __IHEX_H__

#include <map>
#include <vector>
#include <string>
#include <stdint.h>


#define IHEX_PAGE_SIZE	512

namespace Reader
{

class page_t
{
public:
    page_t() :
    min(IHEX_PAGE_SIZE), max(0)
    {
        for(unsigned i = 0; i < IHEX_PAGE_SIZE; i++)
            data[i] = 0xFF;
    };
    
    void dump() { dump(0); };
    void dump(unsigned page_nr);

    uint8_t data[IHEX_PAGE_SIZE];
    unsigned min, max;
};

typedef std::vector<uint8_t> line_t;
typedef std::map<unsigned, page_t> pagemap_t; // map[page] = data

class ihexreader
{
public:
    ihexreader();
    virtual ~ihexreader();
    
    bool read(const char *filename);
    void setVerbose(unsigned verbose) { this->verbose = verbose; };

    pagemap_t* data()
    {
        return _pagemap;
    }

private:
    void parseIHEX(line_t &line, pagemap_t &pagemap);
    void parseline(std::string &s, pagemap_t &pagemap);    
    pagemap_t* parsefile(const char* filename);

    pagemap_t* _pagemap;
    unsigned verbose;
};

}

#endif // __IHEX_H__
