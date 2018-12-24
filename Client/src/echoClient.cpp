#include <stdlib.h>
#include <thread>
#include <connectionHandler.h>
#include <Task1.h>
#include <Task2.h>

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
	Task1 task1(&connectionHandler);
    Task2 task2(&connectionHandler);
    std::thread th1(&Task1::run, &task1);
    std::thread th2(&Task2::run, &task2);
    th1.join();
    th2.join();
    return 0;
}
