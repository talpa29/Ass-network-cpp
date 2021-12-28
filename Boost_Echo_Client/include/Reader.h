
#ifndef ASS4_CPP_READER_H
#define ASS4_CPP_READER_H

#include "connectionHandler.h"
#include "EncDec.h"

class Reader{
private:
    ConnectionHandler &connectionHandler;
    std::mutex &mutex;
    EncDec encDec;
public:
    Reader(ConnectionHandler &connectionHandler1,std::mutex &mutex1);

    void run();
};

#endif //ASS4_CPP_READER_H
