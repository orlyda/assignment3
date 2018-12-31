package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.*;
import java.util.Arrays;

public class EncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private byte[] opCode=new byte[2];
    private int count=0;
    private short opcode = 0;
    private int delimiterCount = 2;
    private byte[] numOfUsers =new byte[2];

    @Override
    public String decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte=='\0')
            count++;
        if(len==2) {
            opcode = bytesToShort(opCode);
            if(opcode==3||opcode==7)
                return popString();
        }
        if(len>2){
            if((opcode==8||opcode==5)&&nextByte=='\0')
                return popString();
            else if((opcode==1||opcode==2||opcode==6)&&count==3){
                return popString();
            }
            else if(opcode == 4&delimiterCount==count)
                return popString();
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
        if(len<2)
            opCode[len]=nextByte;
        if(opcode==4 & (len ==4|len==5)){
            numOfUsers[len]=nextByte;
            if(len == 5)
                delimiterCount += bytesToShort(numOfUsers);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        count =0;
        delimiterCount = 2;
        return result;
    }
}
