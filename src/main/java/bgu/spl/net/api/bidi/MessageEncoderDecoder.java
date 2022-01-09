package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.CommandMessage.*;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoder implements bgu.spl.net.api.MessageEncoderDecoder<Message> {
    byte[] opcode = new byte[2];
    List<Byte> bytes = new LinkedList<>();
    int lenOpcode = 0;


    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            short op = bytesToShort(opcode);
            List<Byte> tmp = bytes;
            bytes = new LinkedList<>();
            lenOpcode = 0;
            switch (op) {
                case 1:
                    return new REGISTER(op, tmp);
                case 2:
                    return new LOGIN(op, tmp);
                case 3:
                    return new LOGOUT(op);
                case 4:
                    return new FOLLOW(op, tmp);
                case 5:
                    return new POST(op, tmp);
                case 6:
                    return new PM(op, tmp);
                case 7:
                    return new LOGSTAT(op);
                case 8:
                    return new STAT(op, tmp);
            }
        } else {
            if (lenOpcode < 2) {
                opcode[lenOpcode] = nextByte;
                lenOpcode++;
            } else
                bytes.add(nextByte);
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        short opcode = message.getOpcode();
        byte[] msg = new byte[0];

        switch (opcode) {
            case 9:   //NOTIFICATION
                NOTIFICATION notification = (NOTIFICATION) message;
                byte[] type = new byte[1];
                type[0] = notification.getNotificationType();
                byte[] pUser = (notification.getPostingUser()).getBytes();
                byte[] zero = new byte[1];
                zero[0] = 0;
                byte[] content = (notification.getContent()).getBytes();
                msg = concatBitsArrays(shortToBytes(opcode), type, pUser, zero, content);
                break;

            case 10:
                ACK ack = (ACK) message;
                short opcodeMsg = ack.getMessageOpcode();
                switch (opcodeMsg) {
                    case 4:
                        byte[] follow = shortToBytes(ack.getFollow());
                        byte[] uname = (ack.getUserName()).getBytes();
                        msg = concatBitsArrays(shortToBytes(opcode), shortToBytes(opcodeMsg), follow, uname);
                        break;

                    case 7:
                        byte[] content1 = (ack.getContent()).getBytes();
                        msg = concatBitsArrays(shortToBytes(opcode), shortToBytes(opcodeMsg), shortToBytes(ack.getAge()),
                                shortToBytes(ack.getNumPosts()),shortToBytes(ack.getNumFollower()),shortToBytes(ack.getNumFollowing()));
                        break;

                    default:
                        msg = concatBitsArrays(shortToBytes(opcode), shortToBytes(opcodeMsg));
                        break;

                }
                break;

            case 11:
                ERROR error = (ERROR) message;
                short opcodeMsg1 = error.getMessageOpcode(); //(getOpcode()==6){
                // return "ERROR " +Content;
                switch (opcodeMsg1) {
                    case 6:
                        byte[] content2 = (error.getContent()).getBytes();
                        msg = concatBitsArrays(shortToBytes(opcode), shortToBytes(opcodeMsg1), content2);
                        break;

                    default:
                        msg = concatBitsArrays(shortToBytes(opcode), shortToBytes(opcodeMsg1));
                        break;
                }
                break;
        }

        byte[] delimiter = new byte[1];
        delimiter[0] = (byte) ';';
        return mergeArr(msg, delimiter);
    }


    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    static byte[] concatBitsArrays(byte[]... bytes) {
        if (bytes.length < 2)
            throw new IllegalArgumentException();

        byte[] output = bytes[0];
        for (int i = 1; i < bytes.length; i++)
            output = mergeArr(output, bytes[i]);

        return output;
    }

    static byte[] mergeArr(byte[] arr1, byte[] arr2) {
        byte[] output = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, output, 0, arr1.length);
        System.arraycopy(arr2, 0, output, arr1.length, arr2.length);
        return output;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
