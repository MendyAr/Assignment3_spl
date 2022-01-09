package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

import java.util.List;

public class PM extends Message {
    private String UserName="";
    private String Content="";
    private String DateAndTime="";//need to add the Date to the content in the end of the string


    public PM(short opcode,String userName,String content,String dateAndTime){
        super(opcode);
        UserName=userName;
        Content=content;
        DateAndTime=dateAndTime;
    }

    public PM(short op,List<Byte> bytes) {
        super(op);
        int i=0;
        while (bytes.get(i)!=0){
            UserName=UserName+bytes.get(i);
            i++;
        }
        i++;
        while (bytes.get(i)!=0){
            Content=Content+bytes.get(i);
            i++;
        }
        i++;
        while (bytes.get(i)!=0){
            DateAndTime=DateAndTime+bytes.get(i);
            i++;
        }
    }

    public void setUserName(String userName) {UserName = userName; }

    public void setContent(String content) {Content = content;}

    public void setDateAndTime(String dateAndTime) {DateAndTime = dateAndTime; }

    public String getUserName() { return UserName; }

    public String getContent() { return Content; }

    @Override
    public String toString() {
        return "PM " +UserName + ' ' +Content + ' ';
    }
}
