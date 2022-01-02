package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class POST extends Message {
    private String Content;
    public POST(short opcode,String content){
        super(opcode);
        Content=content;
    }

    public void setContent(String content) {Content = content; }

    @Override
    public String toString() { return "POST " +Content; }
}
