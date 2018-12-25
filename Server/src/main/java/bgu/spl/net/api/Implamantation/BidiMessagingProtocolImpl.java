package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.Implamantation.EncoderDecoderImpl;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;

import java.util.HashMap;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<String> {
    private boolean shouldTerminate;
    private Connections<String> connections;
    private int connectionId;
    private MessageEncoderDecoder<String> encoderDecoder;

    public BidiMessagingProtocolImpl(){
        shouldTerminate=false;
        connections=new ConnectionsImpl<>();
        encoderDecoder=new EncoderDecoderImpl();
    }

    public  void start(int connectionId, Connections<String> connections){
        this.connectionId=connectionId;
        this.connections=connections;
    }

    public void process(String message){
        if(message!=null){
            byte[] msg=encoderDecoder.encode(message);
            short opCode=bytesToShort(msg);
            switch (opCode){
                case 1:{}
                case 2:{}
                case 3:{}
            }

        }
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
}
