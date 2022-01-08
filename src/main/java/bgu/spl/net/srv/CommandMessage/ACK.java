package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class ACK extends Message {
    private short MessageOpcode;

  public ACK(short opcode, short MessageOpcode){
      super(opcode);
      this.MessageOpcode=MessageOpcode;
  }
  public void setMessageOpcode(short MessageOpcode){
      this.MessageOpcode=MessageOpcode;
  }
    public short getMessageOpcode() { return MessageOpcode; }
    public String toString() {
        return "ACK "  + MessageOpcode;
    }
    public String getMessage(){
      return super.getOpcode()+String.valueOf(MessageOpcode);
    }
}
