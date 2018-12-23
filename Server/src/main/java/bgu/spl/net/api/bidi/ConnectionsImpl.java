package bgu.spl.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T>{
    private Map<Integer,ConnectionHandler> connectionHandlerMap;

    public ConnectionsImpl(){
        connectionHandlerMap=new ConcurrentHashMap<>();
    }

    public boolean send(int connectionId, T msg){}

    public void broadcast(T msg){}

    public void disconnect(int connectionId){}

}