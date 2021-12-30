#include "EncDec.h"
#include "connectionHandler.h"
using namespace std;

EncDec::EncDec(ConnectionHandler &connectionHandler1, std::mutex &mutex1):connectionHandler(connectionHandler1),mutex(mutex1) {}

EncDec::~EncDec() {}

std::vector<char > EncDec::encode(std::string& msg) {
    /**
    * recive messege, turn it to char vector
    * send to connection handler vecor of chars.
     *
    */
    string delimiter = " ";
    vector<string> message;
    size_t pos = 0;
    vector<char > out;
    out.push_back('0');
    out.push_back('0');
    while((pos = msg.find(delimiter)) != string::npos){
        string word = msg.substr(0,pos);
        message.push_back(word);
        msg.erase(0,pos+delimiter.length());
    }
    message.push_back(msg.substr(0,pos));
    short opcode;
    string opCodeString = message[0];
    if (opCodeString == "REGISTER")
        opcode = 1;
    else if (opCodeString == "LOGIN")
        opcode = 2;
    else if (opCodeString == "LOGOUT")
        opcode = 3;
    else if (opCodeString == "FOLLOW")
        opcode = 4;
    else if (opCodeString == "POST")
        opcode = 5;
    else if (opCodeString == "PM")
        opcode = 6;
    else if (opCodeString == "LOGSTAT")
        opcode = 7;
    else if (opCodeString == "STAT")
        opcode = 8;
    else if (opCodeString == "NOTIFICATION")
        opcode = 9;
    switch (opcode) {
        case 1:
            for (int i = 1; i < message.size(); ++i) {
                for (int j = 0; j < message[i].size(); ++j) {
                    out.push_back(message[i][j]);
                }
                out.push_back('0');
            }
            out.push_back(';');


    }
    char chars[out.size()];
    stringstream stream;
    for (int i = 2; i < out.size(); ++i) {
        chars[i] = out[i];
        stream <<hex<< int(chars[i]);
    }
    string  a = stream.str();

    chars[0] = ((opcode >> 8 ) & 0xFF );
    chars[1] = (opcode & 0xFF);

    return out;

}

bool EncDec::decode() {}

void EncDec::run() {
    /**
     * second reading thread
     */
}