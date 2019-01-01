#ifndef TASK2_H_
#define TASK2_H_
#include <stdlib.h>
#include <string>   
#include <cstddef>              
#include <connectionHandler.h>
using std::string;
class Task2{
public:
Task2(ConnectionHandler *connectionHandler);// Constructor
Task2(const Task2 &other);// Copy Constructor
Task2(Task2 &&other);// Move Constructor
Task2& operator=(const Task2 &other);// Copy Assignment
Task2& operator=(Task2 && other);// Move Assignment
virtual ~Task2();
short bytesToShort(char* bytesArr);
bool ParseMessage(std::string answer);
void processNotification(std::string answer);
bool processACK(std::string answer);
void processError(std::string answer);
void run();
private:
    ConnectionHandler *connectionHandler;
    bool shouldTerminate;
};
#endif