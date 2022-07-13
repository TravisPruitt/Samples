
#include "mongoose.h"

#include <string>

namespace Reader
{

class WebServer
{
    public:
        static WebServer* instance();
        void start();
        void stop();

    private:
         WebServer();
         ~WebServer();
         struct mg_context *_context;
};

}

