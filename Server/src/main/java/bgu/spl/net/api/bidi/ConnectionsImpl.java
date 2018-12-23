package bgu.spl.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T>{
    private Map<Integer,ConnectionHandler> connectionHandlerMap;

    public ConnectionsImpl(){
        connectionHandlerMap=new ConcurrentHashMap<>();
    }

    public boolean send(int connectionId, T msg){
        if(!connectionHandlerMap.containsKey(connectionId))
            return false;
        ConnectionHandler c=connectionHandlerMap.get(connectionId);
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