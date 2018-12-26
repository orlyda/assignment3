package bgu.spl.net.api.bidi;

import java.util.HashSet;

public class Client {
    private String username;
    private String password;
    private int numPosts;
    private int numFollowers;
    private int numFollowing;
    private boolean loggedin;
    private HashSet<String> following;
    private HashSet<String> followers;

    public Client(String user,String pass){
        username=user;
        password=pass;
        numPosts=0;
        numFollowers=0;
        numFollowing=0;
        loggedin=false;
    }

    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public int getNumFollowing() {
        return numFollowing;
    }

    public int getNumPosts() {
        return numPosts;
    }

    public void addNumFollowers() {
        this.numFollowers++;
    }
    public void addNumPosts() {
        this.numPosts++;
    }
    public void addNumFollowing() {
        this.numFollowing++;
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

}
