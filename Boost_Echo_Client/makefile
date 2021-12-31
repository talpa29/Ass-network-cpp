CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: Threadclient
	g++ -o bin/clientrun bin/connectionHandler.o bin/EncDec.o bin/Threadclient.o bin/Reader.o $(LDFLAGS)

Threadclient: bin/connectionHandler.o bin/Threadclient.o bin/Reader.o bin/EncDec.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/Threadclient.o: src/Threadclient.cpp
	g++ $(CFLAGS) -o bin/Threadclient.o src/Threadclient.cpp

bin/Reader.o: src/Reader.cpp
	g++ $(CFLAGS) -o bin/Reader.o src/Reader.cpp

bin/EncDec.o: src/EncDec.cpp
	g++ $(CFLAGS) -o bin/EncDec.o src/EncDec.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
