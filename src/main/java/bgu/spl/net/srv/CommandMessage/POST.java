package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

import java.util.LinkedList;
import java.util.List;

public class POST extends Message {
    private String Content;
    public POST(short opcode,String content){
        super(opcode);
        Content=content;
    }

    public POST(short op,List<Byte> bytes) {
        super(op);
        for (int i=0;i<bytes.size()-1;i++)
            Content=Content+bytes.get(i);
    }

    public void setContent(String content) {Content = content; }
    public List<String> specificUsers(){
        List<String> specificUsers=new LinkedList<>();
        for (int i=0;i<Content.length();i++){
            if (Content.charAt(i)=='@'){
                String userName="";
                while (i<Content.length() && Content.charAt(i)!=' ') {
                    userName = userName + Content.charAt(i);
                    i++;
                }
                specificUsers.add(userName);
            }
        }
        return specificUsers;
    }

    public String getContent() { return Content; }

    @Override
    public String toString() { return "POST " +Content; }
}
