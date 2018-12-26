package bgu.spl.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Clients {
    private Map<Integer,Client> clientMap;//combine the client with his connection id

    public Clients(){clientMap=new ConcurrentHashMap<>();
    }

    public Map<Integer, Client> getClientMap() {
        return clientMap;
    }

    public void addClient(Client c,int conId){
        clientMap.put(conId,c);
    }
    public  void removeClient(Integer connectionId){
        clientMap.remove(connectionId);
    }

}
