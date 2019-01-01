package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.bidi.Client;
import bgu.spl.net.api.bidi.ConnectionHandler;
import bgu.spl.net.api.bidi.Connections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnectionsImpl<T> implements Connections<T> {
    private Map<Integer,ConnectionHandler> connectionHandlerMap;
    private AtomicInteger ID = new AtomicInteger();

    public ConnectionsImpl(){
        connectionHandlerMap=new ConcurrentHashMap<>();
        ID.set(1);
    }


    public int addConnection(ConnectionHandler connectionHandler) {
        int id = ID.getAndIncrement();
        connectionHandlerMap.putIfAbsent(id, connectionHandler);
        return id;
    }

    public boolean send(int connectionId, T msg){
        if(!connectionHandlerMap.containsKey(connectionId))
            return false;
        ConnectionHandler c=connectionHandlerMap.get(connectionId);
        System.out.println("GOT HERE");
        c.send(msg);
        return true;
    }

    public void broadcast(T msg){
        connectionHandlerMap.forEach((i,c)-> c.send(msg));
    }

    public void disconnect(int connectionId){
        connectionHandlerMap.remove(connectionId);
    }

}