#include <connectionHandler.h>
 
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\0');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line);
}
 
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    int charCounter = 0, delimiterCounter =2,charCounterMax = 4;
    char* ca = new char[2];
    char* ack = new char[2];
    char* num_of_Users = new char[2];
    short ACKcode=0;
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
            if(charCounter<2)
                ca[charCounter]=ch;
            charCounter++;
            if(charCounter==2){
                if(bytesToShort(ca)==9)
                    delimiterCounter=2;
                else if(bytesToShort(ca)==10)
                    delimiterCounter=1;
                else if(bytesToShort(ca)==11)
                    delimiterCounter=0;
            }
            if(charCounter==4&&bytesToShort(ca)==10){
                ack[0] = frame[2];ack[1]=frame[3];
                ACKcode = bytesToShort(ack);
                if(ACKcode == 4||ACKcode==7)
                    charCounterMax=7;
                if(ACKcode==8)
                    charCounterMax=10;
            }
            if(charCounter==5&& ACKcode ==4&&frame[charCounter-1]==delimiter)
                delimiterCounter++;
            if(charCounter==6&&ACKcode >=4&&ACKcode<=7){
                num_of_Users[0]= frame[4];num_of_Users[1]=frame[5];
                delimiterCounter = bytesToShort(num_of_Users);
            }
            if(charCounter==3 && bytesToShort(ca)==9&&ch=='\0')
                delimiterCounter++;
            if(charCounter>2 && frame[charCounter-1]==delimiter)
                delimiterCounter--;
        }while (((delimiterCounter > 0)||(charCounter < charCounterMax)));
    }catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    delete [] num_of_Users;
    delete [] ack;
    delete [] ca;
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame) {
	return sendBytes(frame.c_str(),frame.length());
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}
short ConnectionHandler::bytesToShort(char* bytesArr)
    {
        short result = (short)((bytesArr[0] & 0xff) << 8);
        result += (short)(bytesArr[1] & 0xff);
        return result;
}
