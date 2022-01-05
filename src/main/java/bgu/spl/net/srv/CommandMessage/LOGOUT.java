package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class LOGOUT extends Message {
    public LOGOUT(short opcode){
        super(opcode);
    }
}
