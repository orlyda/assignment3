CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude	 
LDFLAGS:=-lboost_system	-pthread

all:	BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o	bin/Task2.o	bin/Task1.o	$(LDFLAGS) 

BGSclient:	bin/connectionHandler.o bin/echoClient.o bin/Task2.o	bin/Task1.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/Task2.o:	src/Task2.cpp
	g++	$(CFLAGS)	-o	bin/Task2.o	src/Task2.cpp

bin/Task1.o:	src/Task1.cpp
	g++	$(CFLAGS)	-o	bin/Task1.o	src/Task1.cpp
		
.PHONY: clean
clean:
	rm -f bin/*
