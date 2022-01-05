package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class ERROR extends Message {
    private short MessageOpcode;
    public ERROR(short opcode,short messageOpcode){
        super(opcode);
        MessageOpcode=messageOpcode;
    }

    public short getMessageOpcode() { return MessageOpcode; }

    public void setMessageOpcode(short messageOpcode) { MessageOpcode = messageOpcode; }

    @Override
    public String toString() {
        return "ERROR " +
                MessageOpcode;
    }
    public String getMessage(){
        return super.getOpcode()+String.valueOf(MessageOpcode);
    }
}
