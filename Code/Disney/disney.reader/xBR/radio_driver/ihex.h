#ifndef __IHEX_H__
#define __IHEX_H__

#include <map>
#include <vector>
#include <string>
#include <stdint.h>
#define IHEX_PAGE_SIZE	512

class page_t {
	public:
		page_t() : min(IHEX_PAGE_SIZE), max(0) { for(unsigned i = 0; i < IHEX_PAGE_SIZE; i++) data[i] = 0xFF; };
		uint8_t data[IHEX_PAGE_SIZE];
		unsigned min, max;
		void dump() { dump(0); };
		void dump(unsigned page_nr);
};

typedef std::vector<uint8_t> line_t;
typedef std::map<unsigned, page_t> pagemap_t; // map[page] = data

int radio_read_image(char *filename, pagemap_t &pagemap);

class ihexreader {
	public:
		ihexreader(char *filename = NULL);
		virtual ~ihexreader();
		pagemap_t* read(char *filename = NULL);
		void setVerbose(unsigned verbose) { this->verbose = verbose; };
	private:
		char *ihex_file;
		pagemap_t* parsefile();
		void parseIHEX(line_t &line, pagemap_t &pagemap);
		void parseline(std::string &s, pagemap_t &pagemap);
		unsigned verbose;
};

#endif // __IHEX_H__
