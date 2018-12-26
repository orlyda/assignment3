package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Client;
import bgu.spl.net.api.bidi.Clients;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.Implamantation.EncoderDecoderImpl;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<String> {
    private boolean shouldTerminate;
    private Connections<String> connections;
    private int connectionId;
    private Clients clients;

    public BidiMessagingProtocolImpl(){
        shouldTerminate=false;
        connections=new ConnectionsImpl<>();

    }

    public  void start(int connectionId, Connections<String> connections){
        this.connectionId=connectionId;
        this.connections=connections;
    }

    public void process(String message){
        if(message!=null){
            short opCode=bytesToShort(message.getBytes());
            switch (opCode){
                case 1:{register(message);}
                case 2:{logIn(message);}
                case 3:{disconnect();}
            }

        }
    }
    public void addClients(Clients c){
        clients=c;
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public boolean shouldTerminate(){
        return shouldTerminate;
    }

    private void disconnect(){
        if(!clients.getClientMap().isEmpty()) {
            connections.send(connectionId, "103");
            connections.disconnect(connectionId);
            clients.logOutClient(connectionId);
        }
        else connections.send(connectionId,"113");


    }

    private void register(String msg){
        String username=msg.substring(1,msg.indexOf("\0")-1);
        String password=msg.substring(msg.indexOf("\0")+1,msg.length()-2);
        Client c=new Client(username,password);
        if(clients.getClientMap().containsKey(username)) {//the user is already registered
            connections.send(connectionId,"111");
        }
        else {//register the new client
            connections.send(connectionId, "101");
            clients.addClient(c);
        }

    }
    private void  logIn(String msg){
        String username=msg.substring(1,msg.indexOf("\0")-1);
        String password=msg.substring(msg.indexOf("\0")+1,msg.length()-2);
        if(!clients.getClientMap().containsKey(username)||clients.getLoggedClients().containsValue(username)||
                !clients.getClientMap().get(username).getUsername().equals(password))
            //it means the user already logged on, or the user doesn't exist, or the password don't match
            connections.send(connectionId,"112");
        else { //log in the client
            clients.logInClient(username,connectionId);
            connections.send(connectionId, "102");
        }
    }
    private void follow(String msg){
        String m=msg.

    }
}
