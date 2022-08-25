
#ifndef ASS4_CPP_READER_H
#define ASS4_CPP_READER_H

#include "connectionHandler.h"
#include "EncDec.h"

class Reader{
private:
    ConnectionHandler &connectionHandler;
    EncDec &encDec;
public:
    Reader(ConnectionHandler &connectionHandler1,EncDec &encDec1);

    void operator()();
};

#endif //ASS4_CPP_READER_H
