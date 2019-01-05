#include <Task1.h>

using std::cout;
using std::endl;
using std::string;


Task1::Task1(ConnectionHandler *connectionHandler, bool *logout,bool *shouldTerminate):connectionHandler(connectionHandler),
logout(logout),shouldTerminate(shouldTerminate){}//Constructor
Task1::~Task1(){}//Destructor
Task1:: Task1(const Task1 &other): connectionHandler(other.connectionHandler),logout(other.logout),shouldTerminate(other.shouldTerminate){}//Copy Constructor
Task1::Task1(Task1 &&other): connectionHandler(other.connectionHandler),logout(other.logout) ,shouldTerminate(other.shouldTerminate){//Move Constructor
	other.connectionHandler =nullptr;
	other.logout = nullptr;
	other.shouldTerminate = nullptr;
}
Task1& Task1::operator=(const Task1 &other){//Copy Assignment
	if(this!=&other){
		this-> connectionHandler = other.connectionHandler;
		this-> logout = other.logout;
		this->shouldTerminate= other.shouldTerminate;
	}
	return *this;
}
Task1& Task1::operator=(Task1 &&other){//Move Assignmemt
	if(this!=&other){
		this-> connectionHandler = other.connectionHandler;
		this-> logout = other.logout;
		this->shouldTerminate= other.shouldTerminate;
	}
	other.connectionHandler =nullptr;
	other.logout = nullptr;
	other.shouldTerminate = nullptr;
	return *this;
}

void Task1::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

std::string Task1::processRegister(std::string message){
    message = message.substr(9);
    size_t space = message.find(" ");
    std::string name = message.substr(0,space);
    std::string password = message.substr(space+1);
    char* ca = new char[2];
    shortToBytes(1,ca);
    std::string line("");
    line.push_back(ca[0]);line.push_back(ca[1]);
    delete [] ca;
    line+= name+'\0'+password;
    line.push_back('\0');
    return line;
}

std::string Task1::processLogin(std::string message){
    message = message.substr(6);
    char* ca = new char[2];
    shortToBytes(2,ca);
    std::string line("");
    line.push_back(ca[0]);line.push_back(ca[1]);
    delete [] ca;
    size_t space = message.find(" ");
    std::string name = message.substr(0,space);
    std::string password = message.substr(space+1);
    line+= name;
    line.push_back('\0');
    line+=password;
    line.push_back('\0');
    return line;
}

std::string Task1::processLogout(std::string message){
    *(this->logout)=true;
    char* ca = new char[2];
    shortToBytes(3,ca);
    std::string line("");
    line.push_back(ca[0]);line.push_back(ca[1]);
    delete [] ca;
    return line;
}

std::string Task1::processFollow(std::string message){
    char* ca = new char[2];
    shortToBytes(4,ca);
    std::string line("");
    line.push_back(ca[0]);line.push_back(ca[1]);
    delete [] ca;
    if(message[7]=='0') line.push_back('\0');
    else line.push_back((1&0xFF));
    message = message.substr(9);
    size_t space = message.find(" ");
    std::string numOfusers = message.substr(0,space);
    int num = atoi(numOfusers.c_str());
    char* users = new char[space];
    shortToBytes(((short)num),users);
    line.push_back(users[0]);line.push_back(users[1]);
    delete [] users;
    string userslist = message.substr(space+numOfusers.size());
    for(unsigned int i = 0;i<userslist.size();i++)
        if(userslist[i]==' ') userslist[i]='\0';
    line+= userslist;
    line.push_back('\0');
    return line;
}

std::string Task1::processPost(std::string message){
    char* ca = new char[2];
    shortToBytes(5,ca);
    std::string line("");
    line.push_back(ca[0]);line.push_back(ca[1]);
    delete [] ca;
    std::string content = message.substr(5);
    line+= content;
    line.push_back('\0');
    return line;
}
 std::string Task1::processPM(std::string message){
                char* ca = new char[2];
                shortToBytes(6,ca);
                std::string line("");
                line.push_back(ca[0]);line.push_back(ca[1]);
                delete [] ca;
                message=message.substr(3);
                int space  = message.find(" ");
                string name = message.substr(0,space);
                string content = message.substr(space+1);
                line+= name;
                line.push_back('\0');
                line+=content;
                line.push_back('\0');
                return line;
 }

            std::string Task1::processUserlist(std::string message){
                char* ca = new char[2];
                shortToBytes(7,ca);
                std::string line("");
                line.push_back(ca[0]);line.push_back(ca[1]);
                delete [] ca;
                return line;
            }

std::string Task1::processStat(std::string message){
    char* ca = new char[2];
    shortToBytes(8,ca);
    std::string line("");
    line.push_back(ca[0]);line.push_back(ca[1]);
    delete [] ca;
    string name = message.substr(4);
    line+= name;
    line.push_back('\0');
    return line;
}

std::string Task1::ParseMessage(std::string message){
    if(message.find("REGISTER")==0)
        message = processRegister(message);
    if(message.find("LOGIN")==0)
        message = processLogin(message);
    if(message.find("LOGOUT")==0)
        message = processLogout(message);
    if(message.find("FOLLOW")==0)
        message = processFollow(message);
    if(message.find("POST")==0)
        message = processPost(message);
    if(message.find("PM")==0)
        message = processPM(message);
    if(message.find("USERLIST")==0)
        message = processUserlist(message);
    if(message.find("STAT")==0)
        message = processStat(message);
    return message;
}

void Task1::run(){
    while(!*(this->shouldTerminate)){
    	if(!*(this->logout)){
         	const short bufsize = 1024;
         	char buf[bufsize];
         	std::cin.getline(buf, bufsize);
         	std::string line(buf);
         	line = ParseMessage(line);
         	if (!connectionHandler->sendLine(line)) {
             	std::cout << "Disconnected. Exiting...\n" << std::endl;
             	break;
         	}
       	}
    }
}


