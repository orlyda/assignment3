#ifndef TASK1_H_
#define TASK1_H_
#include <stdlib.h>
#include <string>         
#include <cstddef>        
#include <connectionHandler.h>
class Task1{
public:
Task1(ConnectionHandler *connectionHandler);// Constructor
Task1(const Task1 &other);// Copy Constructor
Task1(Task1 &&other);// Move Constructor
Task1& operator=(const Task1 &other);// Copy Assignment
Task1& operator=(Task1 && other);// Move Assignment
void shortToBytes(short num, char* bytesArr);
std::string ParseMessage(std::string message);
std::string processRegister(std::string message);
std::string processLogin(std::string message);
std::string processLogout(std::string message);
std::string processFollow(std::string message);
std::string processPost(std::string message);
std::string processPM(std::string message);
std::string processUserlist(std::string message);
std::string processStat(std::string message);
 virtual ~Task1();
 void run();
private:
    ConnectionHandler *connectionHandler;
    bool shouldTerminate;
};
#endif