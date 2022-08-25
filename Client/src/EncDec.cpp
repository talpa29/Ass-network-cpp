#include <stdlib.h>
#include "EncDec.h"
#include "connectionHandler.h"
#include <chrono>
#include <ctime>
#include <algorithm>
#include <string>

using namespace std;

EncDec::EncDec(ConnectionHandler &connectionHandler1):connectionHandler(connectionHandler1) {}

bool EncDec::encode(std::string& msg) {
    string delimiter = " ";
    vector<string> message;
    size_t pos = 0;
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
    else if (opCodeString == "PM") {
        opcode = 6;
    }
    else if (opCodeString == "LOGSTAT")
        opcode = 7;
    else if (opCodeString == "STAT")
        opcode = 8;
    else if (opCodeString == "NOTIFICATION")
        opcode = 9;
    else if (opCodeString == "BLOCK") {
        opcode = 12;
    }

    char bytesArr[2];
    bytesArr[0] = ((opcode >> 8) & 0xFF);
    bytesArr[1] = (opcode & 0xFF);
    connectionHandler.sendBytes(bytesArr,2);
    bool result = true;
    if(opcode == 6) {
        //send username separately
        result = result & connectionHandler.sendLine(message[1]);
        //
        string content = message[2];
        for (int i = 3; i < static_cast<int>(message.size()); ++i) {
            content = content + " " + message[i];
        }
        result = result & connectionHandler.sendLine(content);
        auto start = std::chrono::system_clock::now();
        // Some computation here
        auto end = std::chrono::system_clock::now();
        std::chrono::duration<double> elapsed_seconds = end-start;
        std::time_t end_time = std::chrono::system_clock::to_time_t(end);
        string time =  std::ctime(&end_time);
        time = time.substr(0, time.length() - 1);
        result = result & connectionHandler.sendLine(time);
        //send at the end the finish of the line
        char finishline[] = {';'};
        connectionHandler.sendBytes(finishline,1);
        return result;
    }
    else {
        for (int i = 1; i < static_cast<int>(message.size()); ++i) {
            result = result & connectionHandler.sendLine(message[i]);
        }
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
        short result1 = (short)(bytesArr3[0] & 0xff);
        //read line
        connectionHandler.getLine(msg);
        string backfromserver = "Notification";
        switch (result1) {
            case 0:
                backfromserver = backfromserver + " PM";
                break;
            case 1:
                backfromserver = backfromserver + " Public";
        }
        replace(msg.begin(),msg.end(),'\0',' ');
        msg = msg.substr(0,msg.length() - 1);
        backfromserver = backfromserver + " " + msg;
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
            char tmparry[2];
            switch (Messageopcode) {
                case 1:
                    messagefromserver = messagefromserver + " 1";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 2:
                    messagefromserver = messagefromserver + " 2";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 3:
                    messagefromserver = messagefromserver + " 3";
                    connectionHandler.terminates();
                    break;
                case 4: {
                    connectionHandler.getLine(line);
                    line = line.substr(0, line.size() - 2);
                    messagefromserver = messagefromserver + " 4 " + line;
                    break;
                }
                case 5:
                    messagefromserver = messagefromserver + " 5";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 6:
                    messagefromserver = messagefromserver + " 6";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 7:  {
                    messagefromserver = messagefromserver + " 7";
                    for(int i = 0; i < 4; i++){
                        connectionHandler.getBytes(bytesArr2,2);
                        short MessageBytes = (short)((bytesArr2[0] & 0xff) << 8);
                        MessageBytes += (short)(bytesArr2[1] & 0xff);
                        messagefromserver = messagefromserver + " " + to_string(MessageBytes);
                    }
                    connectionHandler.getBytes(tmparry,1);
                    break;
                }
                case 8: {
                    messagefromserver = messagefromserver + " 8";
                    for(int i = 0; i < 4; i++){
                        connectionHandler.getBytes(bytesArr2,2);
                        short MessageBytes = (short)((bytesArr2[0] & 0xff) << 8);
                        MessageBytes += (short)(bytesArr2[1] & 0xff);
                        messagefromserver = messagefromserver + " " + to_string(MessageBytes);
                    }
                    connectionHandler.getBytes(tmparry,1);
                    break;
                }
                case 12: {
                    messagefromserver = messagefromserver + " 12";
                    connectionHandler.getBytes(tmparry,1);
                }
            }
        }
        else {
            messagefromserver = "ERROR";
            char tmparry[2];
            switch (Messageopcode) {
                case 1:
                    messagefromserver = messagefromserver + " 1";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 2:
                    messagefromserver = messagefromserver + " 2";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 3:
                    messagefromserver = messagefromserver + " 3";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 4:
                    messagefromserver = messagefromserver + " 4";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 5:
                    messagefromserver = messagefromserver + " 5";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 6:
                    messagefromserver = messagefromserver + " 6";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 7:
                    messagefromserver = messagefromserver + " 7";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 8:
                    messagefromserver = messagefromserver + " 8";
                    connectionHandler.getBytes(tmparry,1);
                    break;
                case 12:
                    messagefromserver = messagefromserver + " 12";
                    connectionHandler.getBytes(tmparry,1);

            }
        }

        cout << messagefromserver << endl;
    }
    return true;



}

void EncDec::operator()() {
    while (!connectionHandler.shouldterminate()) {
        string ans;
        decode(ans);
    }

}