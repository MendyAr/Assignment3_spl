package bgu.spl.net.srv;

public class Message {
    private short opcode;


    public Message(){}

    public Message(short opcode){
        this.opcode = opcode;
    }

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode){
        this.opcode = opcode;
    }

    @Override
    public String toString() {
        return "Message " + opcode;
    }
    public String getMessage(){
        return String.valueOf(opcode);
    }
}
