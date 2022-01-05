package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

import java.util.List;

public class FOLLOW extends Message {
    private byte Follow;//0 when a user wants to follow, otherwise it has a value of 1(Unfollow).
    private String UserName;
    public FOLLOW(short opcode,byte follow,String userName){
        super(opcode);
        UserName=userName;
        Follow=follow;
    }

    public FOLLOW(short op,List<Byte> bytes) {
        super(op);
        Follow=bytes.get(0);
        for (int i=1;i<bytes.size();i++)
            UserName=UserName+bytes.get(i);
    }

    public void setUserName(String userName) { UserName = userName; }

    public void setFollow(byte follow) {Follow = follow; }

    @Override
    public String toString() {
        return "FOLLOW " +Follow +" "+UserName;
    }
}
