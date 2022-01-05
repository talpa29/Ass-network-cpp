#include <stdlib.h>
#include <connectionHandler.h>
#include "EncDec.h"
#include "Reader.h"
#include <thread>

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    EncDec reciver(connectionHandler);
    EncDec senderenc(connectionHandler);
    Reader sender(connectionHandler,senderenc);

    std::thread sendert(std::ref(sender));
    std::thread recivert(std::ref(reciver));

    recivert.join();
    sendert.detach();

    return 0;
}

