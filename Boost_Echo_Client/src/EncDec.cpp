#include "EncDec.h"
#include "connectionHandler.h"
using namespace std;

EncDec::EncDec(ConnectionHandler &connectionHandler1, std::mutex &mutex1):connectionHandler(connectionHandler1),mutex(mutex1) {}

EncDec::~EncDec() {}

bool EncDec::encode(std::string& msg) {
    string delimiter = " ";
    vector<string> message;
    size_t pos = 0;
    while((pos = msg.find(delimiter)) != string::npos){
        string word = msg.substr(0,pos);
        message.push_back(word);
        message.push_back("0");
        msg.erase(0,pos+delimiter.length());
    }
    message.push_back(msg.substr(0,pos));
    message.push_back("0");
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
    char bytesArr[2];
    bytesArr[0] = ((opcode >> 8) & 0xFF);
    bytesArr[1] = (opcode & 0xFF);
    connectionHandler.sendBytes(bytesArr,2);
    bool result = true;
    for (int i = 0; i < message.size(); ++i) {
       result = result & connectionHandler.sendLine(message[i]);
    }
    char finishline[] = {';'};
    connectionHandler.sendBytes(finishline,1);

    return result;


}

bool EncDec::decode(string& msg) {
    //opcode
    char bytesArr1[2];
    connectionHandler.getBytes(bytesArr1,2);
    short result = (short)((bytesArr1[0] & 0xff) << 8);
    result += (short)(bytesArr1[1] & 0xff);
    if(result == 9) {
        char bytesArr3[1];
        connectionHandler.getBytes(bytesArr3,1);
        short result1 = (short)(bytesArr1[1] & 0xff);
        //read line
        connectionHandler.getLine(msg);
        string delimiter = "0";
        size_t pos = 0;
        string backfromserver = "Notification";
        switch (result1) {
            case 0:
                backfromserver + " PM";
                break;
            case 1:
                backfromserver + " Public";
        }

        while((pos = msg.find(delimiter)) != string::npos){
            backfromserver =backfromserver + " " + msg.substr(0,pos);
            msg.erase(0,pos+delimiter.length());
        }
        cout << backfromserver << endl;

    }
    else {
        char bytesArr2[2];
        connectionHandler.getBytes(bytesArr2,2);
        short Messageopcode = (short)((bytesArr2[0] & 0xff) << 8);
        Messageopcode += (short)(bytesArr2[1] & 0xff);
        string messagefromserver;
           if(result == 10) {
               messagefromserver = "ACK";
               string line;
               connectionHandler.getLine(line);
               string delimiter = "0";
               size_t pos = 0;
               switch (Messageopcode) {
                   case 3:
                       connectionHandler.terminates();
                       break;
                   case 4:
                       //TODO - follow case, need to know what to print
                       break;
                   case 7:
                       //TODO - logstat, need to know if i print the ack
                       break;
                   case 8:
                       //TODO - stat, need to know if i print the ack
                       break;


               }
               while((pos = msg.find(delimiter)) != string::npos){
                   messagefromserver =messagefromserver + " " + msg.substr(0,pos);
                   msg.erase(0,pos+delimiter.length());
               }
           }
           else {
               messagefromserver = "ERROR";
               switch (Messageopcode) {
                   case 1:
                       messagefromserver = messagefromserver + " Register";
                       break;
                   case 2:
                       messagefromserver = messagefromserver + " LOGIN";
                       break;
                   case 3:
                       messagefromserver = messagefromserver + " LOGOUT";
                       break;
                   case 5:
                       messagefromserver = messagefromserver + " POST";
                       break;
                   case 6:
                       messagefromserver = messagefromserver + " PM";
                       break;
                   case 12:
                       messagefromserver = messagefromserver + " BLOCK";

               }
               cout << messagefromserver << endl;
           }
    }



}

void EncDec::run() {
    while (!connectionHandler.shouldterminate()) {
        string ans;
        decode(ans);
    }


}