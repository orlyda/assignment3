package bgu.spl.net.api.bidi;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Clients {
    private static class Singletone {
        private static Clients instance = new Clients();
    }

    public static Clients getInstance() {
        return Singletone.instance;
    }
    private Map<String,Client> clientMap;
    private Map<Integer,String> loggedClients;
    private LinkedList<String> username;//the users in registration order

    private Clients(){
        clientMap=new ConcurrentHashMap<>();
        loggedClients=new ConcurrentHashMap<>();
        username=new LinkedList<>();
    }

    public Map<String, Client> getClientMap() {
        return clientMap;
    }

    public void logInClient(String name,int conId){
        loggedClients.put(conId,name);
        clientMap.get(name).setConnectionId(conId);
    }

    public void logOutClient(int conId){
        loggedClients.remove(conId);
        clientMap.get(getLoggedClients().get(conId)).setConnectionId(0);
    }

    public Map<Integer, String> getLoggedClients() {
        return loggedClients;
    }

    public void register(Client c){
        clientMap.put(c.getUsername(),c);
        username.addLast(c.getUsername());
    }

    public LinkedList<String> getUsername() {
        return username;
    }
}
