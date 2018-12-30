package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Client;
import bgu.spl.net.api.bidi.Clients;
import bgu.spl.net.api.bidi.Connections;


public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<String> {
    private boolean shouldTerminate;
    private Connections<String> connections;
    private int connectionId;
    private Clients clients;

    public BidiMessagingProtocolImpl(){
        shouldTerminate=false;
        connections=new ConnectionsImpl<>();
        this.clients = Clients.getInstance();
    }

    public  void start(int connectionId, Connections<String> connections){
        this.connectionId=connectionId;
        this.connections=connections;

    }

    public void process(String message){
        if(message!=null){
            short opCode=bytesToShort(message.getBytes());
            message = message.substring(2);
            System.out.println(message);
            switch (opCode){
                case 1:register(message);
                case 2:logIn(message);
                case 3:logOut();
                case 4:follow(message);
                case 5:post(message);
                case 6:pm(message);
                case 7:userlist();
                case 8:stat(message);
            }
        }
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public boolean shouldTerminate(){
        return shouldTerminate;
    }

    private void logOut(){
        if(!clients.getClientMap().isEmpty()) {
            connections.send(connectionId, "103");
            connections.disconnect(connectionId);
            clients.logOutClient(connectionId);
        }
        else connections.send(connectionId,"113");
    }

    private void register(String msg){
        String username=msg.substring(1,msg.indexOf("\0"));
        String password=msg.substring(msg.indexOf("\0")+1,msg.length()-1);
        Client c=new Client(username,password);
        if(clients.getClientMap().containsKey(username)) {//the user is already registered
            connections.send(connectionId,"111");
        }
        else {//register the new client
            connections.send(connectionId, "101");
            clients.register(c);
        }
    }

    private void  logIn(String msg){
        String username=msg.substring(1,msg.indexOf("\0"));
        String password=msg.substring(msg.indexOf("\0")+1,msg.length()-1);
        if(!clients.getClientMap().containsKey(username)||clients.getLoggedClients().containsValue(username)||
                !clients.getClientMap().get(username).getUsername().equals(password))
            //it means the user already logged on, or the user doesn't exist, or the password don't match
            connections.send(connectionId,"112");
        else { //log in the client
            clients.logInClient(username,connectionId);
            connections.send(connectionId, "102");
            for (String s : (Iterable<String>) clients.getClientMap().get(username).getAwaitMessages()) {
                connections.send(connectionId, s);
            }
        }
    }

    private void follow(String msg){
        int i=0;
        String usernames="";//create the list of the succeeded following usernames
        boolean succsess=false;
        if (!checkUserLoggedIn(4)){
            return;
        }
        while(i<msg.length()-2 &&(msg.charAt(i)<'0'||msg.charAt(i)>'9') ){i++;}
        String user=clients.getLoggedClients().get(connectionId);
        String s=msg.substring(i,msg.length()-1);
        String[]m=s.split("\0");
        int count=0;//count the amount of succeeded follows
        if(msg.charAt(1)=='0'){//the client want to follow the list of people
            for (String aM : m) {
                if (!clients.getClientMap().get(user).getFollowing().contains(aM)) {
                    //the following user isn't already in the following list
                    succsess = true;
                    clients.getClientMap().get(user).addFollowing(aM);//add the followed user to following list
                    clients.getClientMap().get(aM).addFollower(user);//add our client to the followers list at the followed client
                    count++;
                    usernames+="aM\0";
                }
            }
        }
        else {//the client want to unfollow the list of people
            for (String aM : m) {
                if (clients.getClientMap().get(user).getFollowing().contains(aM)) {
                    //the user we want to remove exists in the following list
                    succsess = true;
                    clients.getClientMap().get(user).removeFollowing(aM);//add the followed user to following list
                    clients.getClientMap().get(aM).removeFollower(user);//add our client to the followers list at the followed client
                    count++;
                    usernames+="aM\0";
                }
            }
        }
        if(!succsess){// the FOLLOW command failed for all users on the list
            connections.send(connectionId,"114");
        }
        else {//the FOLLOW command succeeded for at least one user
            connections.send(connectionId,"104"+String.valueOf(count)+usernames);
        }
    }

    private void post(String msg) {
        if (!checkUserLoggedIn(5)) {
            msg = msg.substring(1, msg.length() - 1);
            String username = clients.getLoggedClients().get(connectionId);
            for (String s : clients.getClientMap().get(username).getFollowers()) {//post the msg to all the followers
                clients.getClientMap().get(s).addMessage(username, msg);//add the message to follower list;
                clients.getClientMap().get(username).addNumPosts();//increase the number of posts
                sendNotification("51"+username+"\0"+msg,s);
            }
            String[] names = msg.split("@");
            for (int i = 1; i < names.length; i++) {
                names[i] = names[i].substring(0, names[i].indexOf(" "));
                clients.getClientMap().get(names[i]).addMessage(username, msg);
                sendNotification("51"+username+"\0"+msg,names[i]);
            }
            connections.send(connectionId, "105");

        }
    }

    private void pm(String msg){
        if (!checkUserLoggedIn(6)) {
            String sender = clients.getLoggedClients().get(connectionId);
            String receiver = msg.substring(1, msg.indexOf("\0"));
            msg = msg.substring(receiver.length() + 2, msg.length() - 1);//the message content
            clients.getClientMap().get(receiver).addMessage(sender, msg);
            connections.send(connectionId, "106");
            sendNotification("51"+sender+"\0"+msg,receiver);
        }
    }

    private void userlist(){
        if (checkUserLoggedIn(7)) {
            String usernames = "107";
            for (String s : clients.getUsername()) usernames += s + "\0";
            connections.send(connectionId, usernames);
        }
    }

    private void stat(String msg){
        if(checkUserLoggedIn(8))
            return;
        String username=msg.substring(1);
        if(!clients.getClientMap().containsKey(username)) {
            connections.send(connectionId, "118");
            return;
        }
        connections.send(connectionId,"108"+clients.getClientMap().get(username).getNumPosts()+
        clients.getClientMap().get(username).getFollowers().size()+
        clients.getClientMap().get(username).getFollowing().size());


    }

    private boolean checkUserLoggedIn(int opKey){
        if (!clients.getLoggedClients().containsKey(connectionId)) {//the client is not logged in
            connections.send(connectionId, "11"+String.valueOf(opKey));
            return false;
        }
        return true;
    }
    private void sendNotification(String msg,String username){
        if (clients.getLoggedClients().containsValue(username)){
            connections.send(connectionId,msg);
        }
        else {
            clients.getClientMap().get(username).addWAitMessage(msg);
        }
    }
}
