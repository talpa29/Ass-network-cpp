
#ifndef ASS4_CPP_ENCDEC_H
#define ASS4_CPP_ENCDEC_H
#include "connectionHandler.h"

class EncDec{
private:
    ConnectionHandler &connectionHandler;
    std::mutex &mutex;
public:
    EncDec(ConnectionHandler &connectionHandler1,std::mutex &mutex1);
    virtual ~EncDec();

    bool encode(std::string& msg);

    void run();

    bool decode(std::string& msg);

};

#endif //ASS4_CPP_ENCDEC_H
