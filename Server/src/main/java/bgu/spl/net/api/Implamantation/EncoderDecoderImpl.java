package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.*;
import java.util.Arrays;

public class EncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private byte[] opCode=new byte[2];
    private int count=0;

    @Override
    public String decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte=='\0')
            count++;
        if(len>=2)
        {
            short s=bytesToShort(opCode);
            if(s==3||s==7)
                return String.valueOf(s);
            if((s==8||s==5)&&nextByte=='\0')
                return popString();
            if((s==1||s==2||s==6||s==9)&&count==2){
                return popString();
            }

        }
        pushByte(nextByte);
        return null; //not a line yet
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    @Override
    public byte[] encode(String message) {
        return (message + "\n").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
