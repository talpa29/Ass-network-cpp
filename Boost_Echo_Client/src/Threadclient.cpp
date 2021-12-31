#include <stdlib.h>
#include <connectionHandler.h>
#include "EncDec.h"
#include "Reader.h"

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
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

    std::mutex mutex;
    EncDec reciver(connectionHandler,mutex);
    Reader sender(connectionHandler,reciver,mutex);

    std::thread sendert(std::ref(sender));
    std::thread recivert(std::ref(reciver));

    sendert.join();
    recivert.join();
    return 0;
}

