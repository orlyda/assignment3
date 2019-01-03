	#include <Task2.h>

	using std::cout;
	using std::endl;
	using std::string;


		   Task2::Task2(ConnectionHandler *connectionHandler):connectionHandler(connectionHandler), shouldTerminate(false){}//Constructor
		    Task2::~Task2(){}//Destructor
		    Task2:: Task2(const Task2 &other): connectionHandler(other.connectionHandler), shouldTerminate(other.shouldTerminate){}//Copy Constructor
		    Task2::Task2(Task2 &&other): connectionHandler(other.connectionHandler), shouldTerminate(other.shouldTerminate){//Move Constructor
		    	other.connectionHandler =nullptr;
		    }
		    Task2& Task2::operator=(const Task2 &other){//Copy Assignment
		    	if(this!=&other){
		    		this-> connectionHandler = other.connectionHandler;
		    		this->shouldTerminate= other.shouldTerminate;
		    	}
		    	return *this;
		    }
		    Task2& Task2::operator=(Task2 &&other){//Move Assignmemt
		    	if(this!=&other){
		    		this-> connectionHandler = other.connectionHandler;
		    		this->shouldTerminate= other.shouldTerminate;
		    	}
		    	other.connectionHandler =nullptr;
		    	return *this;
		    }
		    void Task2::processNotification(std::string answer){
		    	std::string isPM=answer.substr(2,1);
		    	int PM= atoi(isPM.c_str());
		    	answer = answer.substr(3);
		    	size_t first = answer.find_first_of("0");
		    	size_t last= answer.find_last_of("0");
		    	std::string name = answer.substr(0,first);
		    	std::string content=answer.substr(first+1,last-first-2);
		    	std::string toPrint("Notification ");
		    	switch(PM){
		    		case 0:{toPrint+="PM ";
		    		break;}
		    		case 1:{toPrint+="Public ";
		    		break;}
		    	}
		    	toPrint+=name+" "+content;
		    	std::cout<<toPrint<<std::endl;
		    }
		    bool Task2::processACK(std::string answer){
		        char* ca = new char[2];
		        string optional="";
		        ca[0]=answer[2];ca[1]=answer[3];
		        short opcode = bytesToShort(ca);
		        delete [] ca;
		        string toPrint("ACK ");
		        toPrint+= std::to_string(opcode)+=" ";
		        if(opcode==4||opcode==7){
		        	char* numOfusers = new char[2];
		        	numOfusers[0]=answer[4];numOfusers[1]=answer[5];
		        	toPrint+=std::to_string(bytesToShort(numOfusers))+" ";
		        	optional+= answer.substr(6);
		        	for(unsigned int i = 0; i< optional.size();i++){
		        		if(optional[i]=='\0')
		        			optional[i]=' ';
		        	}
		        	delete [] numOfusers;
		        }
		        if(opcode==8){
		        	char* num = new char[2]; 
		        	for(unsigned int i= 0; i<6;i++){
		        		num[i%2]=answer[i+4];
		        		if(i%2==1){
		        			toPrint+=std::to_string(bytesToShort(num));
		        		}
		        		if(i<5&&i%2==1)
		        			toPrint+=" ";
		        	}
		        	delete [] num;
		        }
		        toPrint+=optional;
		        std::cout<<toPrint<<std::endl;
		        return opcode==3;
		    }
		    void Task2::processError(std::string answer){
		    	std::string code=answer.substr(2,2);
		        char* ca = new char[2];
		        ca[0]=answer[2];ca[1]=answer[3];
		        short opcode = bytesToShort(ca);
		        delete [] ca;
		        std::string toPrint("ERROR ");
		        toPrint+= std::to_string(opcode);
		        std::cout<<toPrint<<std::endl;
		    }

		    bool Task2::ParseMessage(std::string answer){
		        char* ca = new char[2];
		        ca[0]=answer[0];ca[1]=answer[1];
		        short opcode = bytesToShort(ca);
		        delete [] ca;
		        bool toReturn =false;
		        switch(opcode){
		         case 9: {processNotification(answer);
		         	break;}
		         case 10: {toReturn = processACK(answer);
		         	break;
		         }
		         case 11: {processError(answer);
		         	break;}
		    }
		        return toReturn;
		    }
		    short Task2::bytesToShort(char* bytesArr)
		    {
		      short result = (short)((bytesArr[0] & 0xff) << 8);
		      result += (short)(bytesArr[1] & 0xff);
		      return result;
		    }
		    void Task2::run(){
		        while(!this->shouldTerminate){
		             std::string answer;
		             if (!this->connectionHandler->getLine(answer)) {
		                 std::cout << "Disconnected. Exiting...\n" << std::endl;
		                 break;
		             }
		            this->shouldTerminate=ParseMessage(answer);
		        }
		    }