package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

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

    @Override
    public String toString() {
        return "STAT " +ListOfUserName;
    }
}
