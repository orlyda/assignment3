package bgu.spl.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Clients {
    private Map<String,Client> clientMap;
    private Map<Integer,String> loggedClients;

    public Clients(){clientMap=new ConcurrentHashMap<>();
    loggedClients=new ConcurrentHashMap<>();
    }

    public Map<String, Client> getClientMap() {
        return clientMap;
    }

    public void addClient(Client c){
        clientMap.put(c.getUsername(),c);
    }
    public  void removeClient(String username){
        clientMap.remove(username);
    }

    public void logInClient(String name,int conId){
        loggedClients.put(conId,name);
    }
    public void logOutClient(int conId){
        loggedClients.remove(conId);
    }

    public Map<Integer, String> getLoggedClients() {
        return loggedClients;
    }
}
