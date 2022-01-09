package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

public class LOGOUT extends Message {
    public LOGOUT(short opcode){
        super(opcode);
    }
}
