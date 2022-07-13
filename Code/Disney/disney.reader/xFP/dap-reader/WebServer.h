
#include "mongoose.h"
#include "json/json.h"


#ifndef __WEB_SERVER_H
#define __WEB_SERVER_H


namespace Reader
{

class WebServer
{
    public:
        static WebServer* instance();

        bool start();
        void stop();

        int getListeningPort()
        {
            return listening_port;
        }

    private:  // methods
        WebServer();
        ~WebServer();

        // no copies please
        WebServer(const WebServer&);
        const WebServer& operator=(const WebServer&);

        void parsePortSpecification(std::string spec);
        const char** getConfigOptions();

    private:  //data
        static const unsigned int max_options = 6;
        static const unsigned int max_option_fields = (max_options*2)+1;
        static const unsigned int max_option_size = 40;

        int listening_port;

        struct mg_context *_context;
};

}


#endif
