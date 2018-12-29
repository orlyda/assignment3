package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.*;

public class EncoderDecoderImpl implements MessageEncoderDecoder<String> {
    private byte[] bytes=new byte[1 << 10];
    private int len=0;
    public  String decodeNextByte(byte nextByte){
        //if()
        return null;
    }


    public byte[] encode(String message) {
        return (message + "\n").getBytes(); //uses utf8 by default
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
}
