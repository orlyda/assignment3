package bgu.spl.net.api.Implamantation;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.*;
import java.nio.charset.*;

public class EncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private byte[] bytes=new byte[2];
    private int len=0;
    public  Message decodeNextByte(byte nextByte){
        if()
    }


    public byte[] encode(Message message){
        short op=message.getOpCode();
        bytes=shortToBytes(op);

        switch (op){
            case 1:{RegisterMessage r=(RegisterMessage)message;
            byte [] user=r.getUsername().getBytes();
            byte[] pass=r.getPassword().getBytes();
            byte[] ret=new byte[4+user.length+pass.length];
            ret[0]=bytes[0];
            ret[1]=bytes[1];
            for(int i=0;i<user.length;i++){
                ret[i+2]=user[i];
            }
            ret[user.length+2]=0;
            for(int i=0;i<pass.length;i++){
                    ret[i+3+user.length]=user[i];
            }
            ret[ret.length-1]=0;
            return ret;
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
}
