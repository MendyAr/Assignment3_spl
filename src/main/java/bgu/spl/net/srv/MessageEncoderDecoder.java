package bgu.spl.net.srv;

import bgu.spl.net.srv.CommandMessage.NOTIFICATION;

import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoder implements bgu.spl.net.api.MessageEncoderDecoder<Message> {
    byte[] opcode;
    List<Byte> bytes=new LinkedList<>();
    int len=0;
    Message msg;
    int lenOpcode;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';'){
            short op=bytesToShort(opcode);
            switch (op){
                case 1://REGISTER Messages


            }
            return msg;
        }
        else {
            if (lenOpcode<2){
                opcode[lenOpcode++]=nextByte;
            }
            else
                bytes.add(nextByte);
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        byte[] opcode = shortToBytes(message.getOpcode());
        byte[] msg ;
        msg=(message.getMessage()+ "\0").getBytes();
        return msg;
    }

    public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
