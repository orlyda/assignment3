package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Client;
import bgu.spl.net.api.bidi.Clients;
import bgu.spl.net.api.bidi.Connections;

import javax.management.StringValueExp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<String> {
    private boolean shouldTerminate;
    private Connections<String> connections;
    private int connectionId;
    private Clients clients;

    public BidiMessagingProtocolImpl(){
        shouldTerminate=false;
        this.clients = Clients.getInstance();
    }

    public  void start(int connectionId, Connections<String> connections){
        this.connectionId=connectionId;
        this.connections=connections;
    }

    public void process(String message){
        if(message!=null){
            String OPcode = message.substring(0,2);
            short opCode=bytesToShort(OPcode.getBytes());
            if(opCode!=3 & opCode!=7)
                message = message.substring(2);
            switch (opCode){
                case 1:{register(message);
                    break;}
                case 2:{logIn(message);
                    break;}
                case 3:{logOut();
                    break;}
                case 4:{follow(message);
                    break;}
                case 5:{post(message);
                    break;}
                case 6:{pm(message);
                    break;}
                case 7:{userlist();
                    break;}
                case 8:{stat(message);
                    break;}
            }
        }
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public boolean shouldTerminate(){
        return shouldTerminate;
    }

    private void register(String msg){
        String username=msg.substring(0,msg.indexOf(String.valueOf('\0')));
        String password=msg.substring(msg.indexOf(String.valueOf('\0'))+1);
        Client c=new Client(username,password);
        if(clients.getClientMap().containsKey(username)) {//the user is already registered
            String reply = new String(shortToBytes((short) 11));
            String opcode = new String(shortToBytes((short)1));
            reply +=opcode;
            connections.send(connectionId, reply);
        }
        else {//register the new client
            clients.register(c);
            String reply = new String(shortToBytes((short) 10));
            String opcode = new String(shortToBytes((short)1));
            reply +=opcode;
            connections.send(connectionId, reply);
        }
    }

    private void  logIn(String msg){
        String username=msg.substring(0,msg.indexOf(String.valueOf('\0')));
        String password=msg.substring(msg.indexOf(String.valueOf('\0'))+1);
        if(!clients.getClientMap().containsKey(username)||clients.getLoggedClients().containsValue(username)||
                !clients.getClientMap().get(username).getPassword().equals(password)) {
            //it means the user already logged on, or the user doesn't exist, or the password don't match
            String reply = new String(shortToBytes((short) 11));
            String opcode = new String(shortToBytes((short)2));
            reply +=opcode;
            connections.send(connectionId, reply);
        }
        else { //log in the client
            clients.logInClient(username,connectionId);
            String reply = new String(shortToBytes((short) 10));
            String opcode = new String(shortToBytes((short)2));
            reply +=opcode;
            connections.send(connectionId, reply);
            for (String s : (Iterable<String>) clients.getClientMap().get(username).getAwaitMessages()) {
                connections.send(connectionId, s);
            }
        }
    }

    private void logOut(){
        if(!clients.getLoggedClients().isEmpty()) {
            String reply = new String(shortToBytes((short) 10));
            String opcode = new String(shortToBytes((short)3));
            reply +=opcode;
            clients.logOutClient(connectionId);
            connections.send(connectionId, reply);
            connections.disconnect(connectionId);
            shouldTerminate=true;
        }
        else{
            String reply = new String(shortToBytes((short) 11));
            String opcode = new String(shortToBytes((short)3));
            reply +=opcode;
            connections.send(connectionId, reply);
        }
    }

    private void follow(String msg){
        System.out.println(msg);
        System.out.println(msg.length());
        String usernames="";//create the list of the succeeded following usernames
        if (!checkUserLoggedIn((short) 4)){ return; }//check if the client is logged in
        String user=clients.getLoggedClients().get(connectionId);
        String s=msg.substring(3);
        String[]m=s.split(String.valueOf('\0'));
        short successful=0;
        if(msg.charAt(0)=='\0'){//the client want to follow the list of people
            for (String aM : m) {
                if(clients.getClientMap().containsKey(aM)){
                    if (!clients.getClientMap().get(user).getFollowing().contains(aM) &&
                            clients.getClientMap().containsKey(aM)) {
                        //the following user isn't already in the following list,and registered
                        clients.getClientMap().get(user).addFollowing(aM);//add the followed user to following list
                        clients.getClientMap().get(aM).addFollower(user);//add our client to the followers list at the followed client
                        usernames += aM + '\0';
                        successful++;
                    }
                }
            }
        }
        else {//the client wants to unfollow the list of people
            for (String aM : m) {
                if(clients.getClientMap().containsKey(aM)) {
                    if (!clients.getClientMap().get(user).getFollowing().contains(aM) &&
                            !clients.getClientMap().containsKey(aM)) {
                        //the user we want to remove exists in the following list, and registered
                        clients.getClientMap().get(user).removeFollowing(aM);//add the followed user to following list
                        clients.getClientMap().get(aM).removeFollower(user);//add our client to the followers list at the followed client
                        usernames += aM + '\0';
                        successful++;
                    }
                }
            }
        }
        if(successful==0){// the FOLLOW command failed for all users on the list
            String reply = new String(shortToBytes((short) 11));
            String opcode = new String(shortToBytes((short)4));
            reply +=opcode;
            connections.send(connectionId, reply);
        }
        else {//the FOLLOW command succeeded for at least one user
            String reply = new String(shortToBytes((short) 10));
            String opcode = new String(shortToBytes((short)4));
            String succ = new String(shortToBytes(successful));
            reply +=opcode+succ+usernames;
            connections.send(connectionId, reply);
        }
    }

    private void post(String msg) {
        if (!checkUserLoggedIn((short) 5)) {
            return;
        }
        String username = clients.getLoggedClients().get(connectionId);
        String toSend = new String(shortToBytes((short) 9));
        char FOLLOW = (byte)(1&0xFF);
        toSend += FOLLOW;
        for (String s : clients.getClientMap().get(username).getFollowers()) {//post the msg to all the followers
            clients.getClientMap().get(s).addMessage(username, msg);//add the message to follower list;
            clients.getClientMap().get(username).addNumPosts();//increase the number of posts
            sendNotification(toSend + username + "\0" + msg, s);
        }
        String[] names = msg.split("@");
        for (int i = 1; i < names.length; i++) {
            names[i] = names[i].substring(0, names[i].indexOf(" "));
            if (clients.getClientMap().containsKey(names[i])&&
            !clients.getClientMap().get(username).getFollowers().contains(names[i])) {
                //check the person we send the post to is registered and not in the followers list
                clients.getClientMap().get(names[i]).addMessage(username, msg);
                sendNotification(toSend + username + "\0" + msg, names[i]);
            }
        }
        String reply = new String(shortToBytes((short) 10));
        String opcode = new String(shortToBytes((short) 5));
        reply += opcode;
        connections.send(connectionId, reply);
    }

    private void pm(String msg) {
        if (!checkUserLoggedIn((short) 6)) {
            return;
        }
        System.out.println(msg);
        String sender = clients.getLoggedClients().get(connectionId);
        String receiver = msg.substring(0, msg.indexOf(String.valueOf('\0')));
        msg = msg.substring(msg.indexOf(String.valueOf('\0')));//the message content
        if(clients.getClientMap().containsKey(receiver)) {//check the receiver is registered
            clients.getClientMap().get(receiver).addMessage(sender, msg);
            String reply = new String(shortToBytes((short) 10));
            String opcode = new String(shortToBytes((short) 6));
            reply += opcode;
            connections.send(connectionId, reply);
            String toSend = new String(shortToBytes((short) 9));
            char PM = (byte)(0);
            toSend += PM;
            System.out.println(toSend+" "+sender+" "+msg);
            sendNotification(toSend + sender + msg, receiver);
        }
        else {
            String reply = new String(shortToBytes((short) 11));
            String opcode = new String(shortToBytes((short) 6));
            reply += opcode;
            connections.send(connectionId, reply);
        }
    }

    private void userlist(){
        if (checkUserLoggedIn((short) 7)) {
            String usernames = new String(shortToBytes((short) 10))+
                    new String(shortToBytes((short)7))+new String(shortToBytes((short)clients.getClientMap().size()));
            for (String s : clients.getUsername()) usernames += s + '\0';
            connections.send(connectionId, usernames);
        }
    }

    private void stat(String msg){
        if(!checkUserLoggedIn((short) 8))
            return;
        String username=msg.substring(0,msg.length()-1);
        if(!clients.getClientMap().containsKey(username)){
            String reply = new String(shortToBytes((short) 11));
            String opcode = new String(shortToBytes((short)8));
            reply +=opcode;
            connections.send(connectionId, reply);
            return;
        }
        String reply = new String(shortToBytes((short) 10));
        String opcode = new String(shortToBytes((short)8));
        String postsnum = new String(shortToBytes((short)clients.getClientMap().get(username).getNumPosts()));
        String followersNum = new String(shortToBytes((short)clients.getClientMap().get(username).getFollowers().size()));
        String followingNum = new String(shortToBytes((short)clients.getClientMap().get(username).getFollowing().size()));
        reply +=opcode+postsnum+followersNum+followingNum;
        connections.send(connectionId,reply);
    }

    private boolean checkUserLoggedIn(short opKey){
        if (!(clients.getLoggedClients().containsKey(connectionId))) {//the client is not logged in
            String reply = new String(shortToBytes((short) 11));
            connections.send(connectionId, reply+new String(shortToBytes((opKey))));
            return false;
        }
        return true;
    }
    private void sendNotification(String msg,String username){
        if (clients.getLoggedClients().containsValue(username)){
            connections.send(clients.getClientMap().get(username).getConnectionId(),msg);
        }
        else {
            clients.getClientMap().get(username).addWAitMessage(msg);
        }
    }
}
