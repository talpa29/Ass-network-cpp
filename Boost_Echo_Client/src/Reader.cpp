#include "Reader.h"
#include "connectionHandler.h"
#include "EncDec.h"

Reader::Reader(ConnectionHandler &connectionHandler1, EncDec &encDec1, std::mutex &mutex1):connectionHandler(connectionHandler1),
                                                                                           encDec(encDec1),mutex(mutex1) {};

void Reader::operator()() {
    while(!connectionHandler.shouldterminate()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if(!encDec.encode(line))
            connectionHandler.terminates();
    }


}
