/**
    IStatus.h
    Greg Strange
    Dec 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __STATUS_INTERFACE_H
#define __STATUS_INTERFACE_H


#include <string>

namespace Reader
{

    class IStatus 
    {
    public:
        enum Status {Green, Yellow, Red};
        virtual Status getStatus(std::string& msg) = 0;

        static const char* statusToString(Status status)
        {
            switch (status)
            {
            case Green: return "Green";
            case Yellow: return "Yellow";
            case Red: return "Red";
            default: return "Unknown";
            }
        };

    protected:
        IStatus() {};
        ~IStatus() {};

    private:
        IStatus(const IStatus&);
        const IStatus& operator=(const IStatus&);
    };

}




#endif
