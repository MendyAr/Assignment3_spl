package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class PM extends Message {
    private String UserName;
    private String Content;
    private String DateAndTime;

    public PM(short opcode,String userName,String content,String dateAndTime){
        super(opcode);
        UserName=userName;
        Content=content;
        DateAndTime=dateAndTime;
    }

    public void setUserName(String userName) {UserName = userName; }

    public void setContent(String content) {Content = content;}

    public void setDateAndTime(String dateAndTime) {DateAndTime = dateAndTime; }

    @Override
    public String toString() {
        return "PM " +UserName + ' ' +Content + ' ';
    }
}
