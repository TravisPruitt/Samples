#include <iostream>
#include <fstream>
#include <stdio.h>
#include <sstream>
#include "ihex.h"

using namespace Reader;

static void chomp(std::string &str)
{
	ssize_t i = str.size() - 1;
	while(isspace(str[i]))
		i--;
	str.erase(i + 1);
}

void page_t::dump(unsigned page_nr)
{
	for(unsigned i = 0; i < IHEX_PAGE_SIZE; i += 16)
	{
		printf("%04x  ", i + (page_nr * IHEX_PAGE_SIZE));
		for(unsigned u = 0; u < 16; u++) {
			printf("%02x ", data[u+i]);
		}
		printf("\n");
	}
}


ihexreader::ihexreader() :
    _pagemap(NULL), verbose(0)
{
}

ihexreader::~ihexreader()
{
    if (_pagemap)
        delete _pagemap;
}

bool ihexreader::read(const char* filename)
{
    if (_pagemap)
        delete _pagemap;

	_pagemap = parsefile(filename);

    return _pagemap != NULL;
}

void ihexreader::parseIHEX(line_t &line, pagemap_t &pagemap)
{
	unsigned len = line[0];
	unsigned addr = line[1] << 8 | line[2];
	unsigned record_type = line[3];
	unsigned offs = addr % IHEX_PAGE_SIZE;
    
	if(record_type == 0) {
		page_t &page = pagemap[addr/IHEX_PAGE_SIZE];
        
		if(page.min > offs)
			page.min = offs;
		if((offs + len) >= IHEX_PAGE_SIZE) {
			// need to split into two pages
			page.max = IHEX_PAGE_SIZE - 1;
			unsigned first_len = IHEX_PAGE_SIZE - offs;
			unsigned second_len = len - first_len;
			std::copy(line.begin() + 4, line.begin() + 4 + first_len, &page.data[offs]);
			page_t &second_page = pagemap[addr/IHEX_PAGE_SIZE + 1];
			second_page.min = 0;
			std::copy(line.begin() + 4 + first_len,
                      line.begin() + 4 + first_len + second_len,
                      &second_page.data[0]);
		}
		else {
			if((offs + len) > page.max)
				page.max = offs + len - 1;
			std::copy(line.begin() + 4,
                      line.begin() + 4 + len,
                      &page.data[offs]);
		}
	}
}

void ihexreader::parseline(std::string &s, pagemap_t &pagemap)
{
	std::string hex = "0123456789ABCDEF";
	line_t line;

	chomp(s);
    
	std::stringstream ss(s);
	char scode;
	ss >> scode;
	if(scode != ':') {
		return;
	}
	while(ss.peek() != EOF) {
		char high, low;
		uint8_t byte;
        
		ss >> high;
		ss >> low;
        
		byte = 16 * hex.find(high);
		byte += hex.find(low);
		line.push_back(byte);
	}
	parseIHEX(line, pagemap);
}

pagemap_t* ihexreader::parsefile(const char* filename)
{
	std::ifstream f;
	std::string l;
	pagemap_t *pagemap;
    
	f.open(filename, std::ifstream::in);
	if(!f.good())
		return NULL;
    
	pagemap = new pagemap_t();
    
	while(getline(f, l)) {
		parseline(l, *pagemap);
	}
    
	for(pagemap_t::iterator i = pagemap->begin(); i != pagemap->end(); ++i) {
		page_t &page = i->second;
		if(verbose > 0) {
			printf("Page %d %d %d(%x)\n", i->first, page.min, page.max, page.max);
			printf("%d bytes at %x\n", page.max - page.min + 1, i->first * IHEX_PAGE_SIZE + page.min);
		}
		if(verbose > 1) {
			page.dump(i->first);
		}
	}
	return pagemap;
}
