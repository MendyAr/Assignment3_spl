package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class BLOCK extends Message {
    private String username;

    public BLOCK(short opcode,String UserName){
        super(opcode);
        username=UserName;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() { return "BLOCK " +username; }
    public String getMessage(){
        return super.getOpcode()+username+ "\0";
    }
}
