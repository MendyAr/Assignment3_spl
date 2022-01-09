package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

public class ERROR extends Message {
    private short MessageOpcode;
    private String Content;
    public ERROR(short opcode,short messageOpcode){
        super(opcode);
        MessageOpcode=messageOpcode;
    }
    public ERROR(short opcode,short messageOpcode,String content){
        super(opcode);
        MessageOpcode=messageOpcode;
        Content=content;
    }
    public short getMessageOpcode() { return MessageOpcode; }

    public void setMessageOpcode(short messageOpcode) { MessageOpcode = messageOpcode; }

    @Override
    public String toString() {
        if (getOpcode()==6){
            return "ERROR " +Content;
        }
        return "ERROR " +
                MessageOpcode;
    }
    public String getMessage(){
        return super.getOpcode()+String.valueOf(MessageOpcode);
    }
}
