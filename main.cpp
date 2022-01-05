#include <stdlib.h>
#include <ctime>
#include <iomanip>
#include <thread>
#include <mutex>
#include <condition_variable>
#include "connectionHandler.h"

std::mutex m;
std::condition_variable cv;
bool terminate = false;

bool generateCommand(std::string &command, std::string &line);
bool generateReplay(std::string &msg, std::string &line);
void splitString(std::string s, std::vector<std::string> &v, char delimiter);

//acquire keyboard input, process it and sent it to the server
void handleKeyboardInput(ConnectionHandler& connectionHandler) {
    while (!terminate) {
        const short bufSize = 1024;
        char buf[bufSize];
        std::string line, command;
        bool isCorrect = false;
        while (!isCorrect) {
            std::cin.getline(buf, bufSize);
            line = buf;
            isCorrect = generateCommand(command, line);
            if (!isCorrect) {
                std::cout << "Invalid command!" << std::endl;
                command.clear();
            }
        }
        unsigned long len = line.length();
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        std::unique_lock<std::mutex> lk(m);
        cv.wait(lk);
    }
}

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

    //std::cout << "Sent " << len+1 << " bytes to server" << std::endl;
    // We can use one of three options to read data from the server:
    // 1. Read a fixed number of characters
    // 2. Read a line (up to the newline character using the getline() buffered reader
    // 3. Read up to the null character
    while (!terminate) {
        std::string rawMsg;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!connectionHandler.getLine(rawMsg)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        unsigned long len = rawMsg.length();
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        rawMsg.resize(len - 1);
        std::string msg;
        bool isCorrect = generateReplay(msg, rawMsg);
        if (!isCorrect){
            std::cout << "Received an undefined message:" << std::endl;
        }
        std::cout << msg << std::endl;
        if (msg.substr(0, 11) != "NOTIFICATION") {
            cv.notify_one();
        }
        if (msg == "ACK 03"){
            terminate = true;
        }
    }
    return 0;
}


bool generateCommand(std::string &command, std::string &line) {
    //splits to words
    std::vector<std::string> words;
    splitString(line, words, ' ');
    //remove unnecessary spaces
    for (int i = 0; i < words.size(); i++){
        if(words[i].empty() | (words[i].find_first_not_of(' ') < 0)){
            words.erase(words.begin() + i);
        }
    }
    if (words.empty()) {return false;}
    //identify command
    if (words[0] == "REGISTER") {      //REGISTER <Username> <Password> <Birthday>
        if(words.size() != 4)
            return false;
        command.append("01" + words[1] + '\0' + words[2] + '\0' + words[3] + '\0');
    }
    else if (words[0] == "LOGIN") {    //LOGIN <Username> <Password>
        if(words.size() != 3)
            return false;
        command.append("02" + words[1] + '\0' + words[2] + '\0' + '1');
    }
    else if (words[0] == "LOGOUT") {
        if(words.size() != 1)
            return false;
        command.append("03");
    }
    else if (words[0] == "FOLLOW") {    //FOLLOW <1/0> <UserName>
        if(words.size() != 3 || (words[1] != "0" & words[1] != "1"))
            return false;
        command.append("04" + words[1] + words[2]);
    }
    else if (words[0] == "POST") {     //POST <PostMsg>
        if(words.size() < 2)
            return false;
        std::string list;
        for (int i = 1; i < words.size() - 1; i++){
            list.append(words[i] + ' ');
        }
        list.append(words[words.size()-1]);
        command.append("05" + list + '\0');
    }
    else if (words[0] == "PM") {       //PM <Username> <Content>
        if(words.size() < 3)
            return false;
        std::time_t t = std::time(nullptr);
        struct tm *currTime = std::gmtime(&t);
        std::stringstream time;
        time << std::put_time(&*currTime, "%d-%m-%Y %H:%M");   //Format: DD-MM-YYYY HH:MM
        std::string content;
        for (int i = 2; i < words.size() - 1; i++){
            content.append(words[i] + ' ');
        }
        content.append(words[words.size()-1]);
        command.append("06" + words[1] + '\0' + content + '\0' + time.str() + '\0');
    }
    else if (words[0] == "LOGSTAT") {
        if(words.size() != 1)
            return false;
        command.append("07");
    }
    else if (words[0] == "STAT") {     //STAT <UserNames_list>
        if(words.size() < 2)
            return false;
        std::string list;
        for (int i = 1; i < words.size() - 1; i++){
            list.append(words[i] + '|');
        }
        list.append(words[words.size()-1]);
        command.append("08" + list +  '\0');

    }
    else if (words[0] == "BLOCK") {    //BLOCK <UserNames>
        if(words.size() != 2)
            return false;
        command.append("12" + words[1] + '\0');
    }
    else
    {
        return false;
    }
    return true;
}

bool generateReplay(std::string &msg, std::string &line){
    std::string op_code = line.substr(0, 1);
    if (op_code == "09"){
        msg.append("NOTIFICATION ");
        if (line.at(2) == '0'){
            msg.append("PM ");
        }
        else if (line.at(2) == '1'){
            msg.append("PUBLIC ");
        }
        else
            return false;

        std::vector<std::string> words;
        splitString(line.substr(3), words, '\0');
        msg.append(words[0] + " " + words[1]);
    }
    else if (op_code == "10"){
        msg.append("ACK ");
        std::string msg_op_code = line.substr(2, 3);
        msg.append(msg_op_code);
        if (msg_op_code == "04"){ //FOLLOW
            msg.append(" " + line.substr(4, line.length()-2));
        }
        else if (msg_op_code == "07" | msg_op_code == "08") {  //LOGSTAT or STAT
            msg.append(" " + line.substr(4, 5) + " " + line.substr(6, 7) + " " + line.substr(8, 9) + " " + line.substr(10, 11));
        }
    }
    else if (op_code == "11"){
        msg.append("ERROR " + line.substr(2, 3));
    }
    else return false;

    return true;
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

