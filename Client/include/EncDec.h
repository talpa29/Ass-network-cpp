
#ifndef ASS4_CPP_ENCDEC_H
#define ASS4_CPP_ENCDEC_H
#include "connectionHandler.h"


class EncDec{
private:
    ConnectionHandler &connectionHandler;
public:
    EncDec(ConnectionHandler &connectionHandler1);

    bool encode(std::string& msg);

    void operator()();

    bool decode(std::string& msg);

};

#endif //ASS4_CPP_ENCDEC_H
