package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

import java.util.LinkedList;
import java.util.List;

public class STAT extends Message {
    private String ListOfUserName;
    public STAT(short opcode,String listOfUserName){
        super(opcode);
        ListOfUserName=listOfUserName;
    }

    public STAT(short op,List<Byte> bytes) {
        super(op);
        for (int i=0;i< bytes.size()-1;i++)
            ListOfUserName=ListOfUserName+bytes.get(i);
    }

    public void setListOfUserName(String listOfUserName) {ListOfUserName = listOfUserName; }

    public List<String> getListOfUserName() {
        List<String> listOfUserName=new LinkedList<>();
        String User="";
        for (int i=0;i<ListOfUserName.length();i++) {
            if (ListOfUserName.charAt(i)!='|')
                User=User+ListOfUserName.charAt(i);
            else {
                String newUser=User;
                listOfUserName.add(newUser);
                User="";
            }
        }
        return listOfUserName;
    }

    @Override
    public String toString() {
        return "STAT " +ListOfUserName;
    }
}
