#include <cstdlib>
#include <ctime>
#include <iomanip>
#include <thread>
#include <mutex>
#include <condition_variable>
#include "connectionHandler.h"

std::mutex m;
std::condition_variable cv;
const char zero(0);
const char one(1);
const char endMsg(';');

void shortToBytes(const short &num, char *bytesArr);
short bytesToShort(const char *bytesArr);
void splitString(std::string s, std::vector<std::string> &v, char delimiter);
void handleKeyboardInput(ConnectionHandler& ch);

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

    std::thread inputHandler(handleKeyboardInput, std::ref(connectionHandler));

    // We can use one of three options to read data from the server:
    // 1. Read a fixed number of characters
    // 2. Read a line-up to the newline character using the getLine() buffered reader
    // 3. Read up to the null character

    //receive messages from the clint and print them
    while (!connectionHandler.ToTerminate()) {
        std::string ServerMsg;
        char opCodeBytes[2];
        connectionHandler.getBytes(opCodeBytes, 2);
        short opcode = bytesToShort(opCodeBytes);

        switch (opcode) {

            case 9:{
                ServerMsg.append("NOTIFICATION ");
                char type;
                connectionHandler.getBytes(&type, 1);
                if (type == '0') {
                    ServerMsg.append("PM ");
                } else {
                    ServerMsg.append("PUBLIC ");
                }

                std::string userName;
                std::string content;
                connectionHandler.getFrameAscii(userName, 0);
                connectionHandler.getFrameAscii(content, 0);
                ServerMsg.append(userName);
                ServerMsg.append(" ");
                ServerMsg.append(content);
                break;
            }

            case 10: {
                ServerMsg.append("ACK ");
                char msgOpcodeBytes[2];
                connectionHandler.getBytes(msgOpcodeBytes, 2);
                short msgOpcode = bytesToShort(msgOpcodeBytes);
                ServerMsg.append(std::to_string(msgOpcode));
                switch (msgOpcode) {
                    case 3: {    //LOGOUT
                        std::unique_lock<std::mutex> lock(m);
                        connectionHandler.Terminate();
                        cv.notify_all();
                        break;
                    }
                    case 4: {   //FOLLOW
                        std::string userName;
                        connectionHandler.getFrameAscii(userName, 0);
                        ServerMsg.append(userName);
                        break;
                    }
                    case 7:
                    case 8: {   //LOGSTAT or STAT
                        for (int i = 0; i < 4; i++) {
                            char bytes[2];
                            connectionHandler.getBytes(bytes, 2);
                            short data = bytesToShort(bytes);
                            ServerMsg.append(std::to_string(data));
                            ServerMsg.append(" ");
                        }
                        break;
                    }
                    default: {
                        std::cout << "op_code of ACK message is incorrect!" << std::endl;}

                }
                break;
            }
            case 11 :{
                ServerMsg.append("ERROR ");
                char msgOpcodeBytes[2];
                connectionHandler.getBytes(msgOpcodeBytes, 2);
                short msgOpcode = bytesToShort(msgOpcodeBytes);
                ServerMsg.append(std::to_string(msgOpcode));

                if (msgOpcode == 3){
                    std::unique_lock<std::mutex> lock(m);
                    cv.notify_all();
                }

                break;
            }

            default: {
                std::cout << "server message op_code is incorrect!" << std::endl;
            }
                break;

        }
        char endMessage[1];
        connectionHandler.getBytes(endMessage, 1);
        if(endMessage[0] != ';')
            throw std::runtime_error("message didn't end with ';'");

        std::cout << ServerMsg << std::endl;
    }

    inputHandler.join();
    connectionHandler.close();
    return 0;
}

//thread: acquire keyboard input, process it and sent it to the server
void handleKeyboardInput(ConnectionHandler& ch) {
    while (!ch.ToTerminate()) {
        const short bufSize = 1024;
        char buf[bufSize];
        std::vector<std::string> command;
        bool isCorrect = false;

        while (!isCorrect) {    //if the input is invalid get another input
            isCorrect = true;
            std::cin.getline(buf, bufSize);
            std::string line = buf;
            //splits to words
            std::vector<std::string> words;
            splitString(line, words, ' ');
            //remove unnecessary spaces
            for (int i = 0; i < words.size(); i++){
                if(words[i].empty() | (words[i].find_first_not_of(' ') < 0)){
                    words.erase(words.begin() + i);
                }
            }
            if (words.empty()){
                isCorrect = false;
                break;
            }
            //identify command
            //send op_code
            //send parameters
            if (words[0] == "REGISTER") {      //REGISTER <Username> <Password> <Birthday>
                if(words.size() != 4) {
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(1, opCode);
                ch.sendBytes(opCode, 2);
                ch.sendFrameAscii(words[1], zero);
                ch.sendFrameAscii(words[2], zero);
                ch.sendFrameAscii(words[3], zero);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "LOGIN") {    //LOGIN <Username> <Password> <captcha>
                if(words.size() != 4 || words[3] != "1"){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(2, opCode);
                ch.sendBytes(opCode, 2);
                ch.sendFrameAscii(words[1], zero);
                ch.sendFrameAscii(words[2], zero);
                ch.sendBytes(&one, 1);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "LOGOUT") {
                if(words.size() != 1){
                    isCorrect = false;
                    break;
                }
                std::unique_lock<std::mutex> lock(m);
                char opCode[2];
                shortToBytes(3, opCode);
                ch.sendBytes(opCode, 2);
                ch.sendBytes(&endMsg, 1);
                cv.wait(lock);
            }
            else if (words[0] == "FOLLOW") {    //FOLLOW <1/0> <UserName>
                if(words.size() != 3 || (words[1] != "0" & words[1] != "1")){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(4, opCode);
                ch.sendBytes(opCode, 2);
                if (words[1] == "1"){
                    ch.sendBytes(&one, 1);
                }
                else{
                    ch.sendBytes(&zero, 1);
                }
                ch.sendFrameAscii(words[2], zero);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "POST") {     //POST <PostMsg>
                if(words.size() < 2){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(5, opCode);
                ch.sendBytes(opCode, 2);
                std::string list;
                for (int i = 1; i < words.size() - 1; i++){
                    list.append(words[i] + ' ');
                }
                list.append(words[words.size()-1]);
                ch.sendFrameAscii(list, zero);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "PM") {       //PM <Username> <Content>
                if(words.size() < 3){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(6, opCode);
                ch.sendBytes(opCode, 2);
                ch.sendFrameAscii(words[1], zero);

                std::time_t t = std::time(nullptr);
                struct tm *currTime = std::gmtime(&t);
                std::stringstream time;
                std::stringstream date;
                time << std::put_time(&*currTime, "%d-%m-%Y %H:%M");   //Format: DD-MM-YYYY HH:MM
                date << std::put_time(&*currTime, "%d-%m-%Y");         //Format: DD-MM-YYYY
                std::string content;
                for (int i = 2; i < words.size(); i++){
                    content.append(words[i] + ' ');
                }
                content.append(date.str());

                ch.sendFrameAscii(content, zero);
                ch.sendFrameAscii(time.str(), zero);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "LOGSTAT") {
                if(words.size() != 1){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(7, opCode);
                ch.sendBytes(opCode, 2);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "STAT") {     //STAT <UserNames_list>
                if(words.size() < 2){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                shortToBytes(8, opCode);
                ch.sendBytes(opCode, 2);

                std::string list;
                for (int i = 1; i < words.size() - 1; i++){
                    list.append(words[i] + '|');
                }
                list.append(words[words.size()-1]);
                ch.sendFrameAscii(list, zero);
                ch.sendBytes(&endMsg, 1);
            }
            else if (words[0] == "BLOCK") {    //BLOCK <UserNames>
                if(words.size() != 2){
                    isCorrect = false;
                    break;
                }
                char opCode[2];
                opCode[0] = '1';
                opCode[1] = '2';
                ch.sendBytes(opCode, 2);
                ch.sendFrameAscii(words[1], zero);
                ch.sendBytes(&endMsg, 1);
            }
            else { isCorrect = false; }

        }
        if (!isCorrect) {
            std::cout << "Invalid command!" << std::endl;
        }
    }
}


void shortToBytes(const short &num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

short bytesToShort(const char *bytesArr) {
    auto result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void splitString(std::string s, std::vector<std::string> &v, char delimiter){
    std::string temp = "";
    for (int i = 0; i < s.length(); i++){

        if(s[i] == delimiter){
            v.push_back(temp);
            temp = "";
        }
        else{
            temp.push_back(s[i]);
        }
    }
    v.push_back(temp);
}

