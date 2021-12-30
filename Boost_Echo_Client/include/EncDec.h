
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

    std::vector<char > encode(std::string& msg);

    void run();

    bool decode();

};

#endif //ASS4_CPP_ENCDEC_H
