package bgu.spl.net.api.bidi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;

public class Client {
    private String username;
    private String password;
    private int numPosts;
    private HashSet<String> following;
    private HashSet<String> followers;
    private Map<String,String> messages;
    private LinkedList<String> waitingMessage;

    public Client(String user,String pass){
        username=user;
        password=pass;
        numPosts=0;
        following=new HashSet<>();
        followers=new HashSet<>();
        messages=new ConcurrentHashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public int getNumPosts() {
        return numPosts;
    }

    public void addNumPosts() {
        this.numPosts++;
    }

    public void addFollower(String user){
        followers.add(user);
    }
    public void removeFollower(String user){
        followers.remove(user);
    }
    public void addFollowing(String user){
        following.add(user);
    }
    public void removeFollowing(String user){
        following.remove(user);
    }

    public HashSet<String> getFollowers() {
        return followers;
    }

    public HashSet<String> getFollowing() {
        return following;
    }
    public void addMessage(String username,String msg){
        messages.put(username,msg);
    }
    public void addWAitMessage(String msg){
        waitingMessage.addLast(msg);
    }
    public LinkedList getAwaitMessages(){return waitingMessage;}
}
