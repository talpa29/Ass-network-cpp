#include "EncDec.h"
#include "connectionHandler.h"

EncDec::EncDec(ConnectionHandler &connectionHandler1, std::mutex &mutex1):connectionHandler(connectionHandler1),mutex(mutex1) {}

EncDec::~EncDec() {}

std::vector<unsigned char > EncDec::encode(std::string msg) {
    /**
    * recive messege, turn it to char vector
    * send to connection handler vecor of chars.
     *
    */
}

bool EncDec::decode() {}

void EncDec::run() {
    /**
     * second reading thread
     */
}