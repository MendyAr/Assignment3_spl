package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class STAT extends Message {
    private String ListOfUserName;
    public STAT(short opcode,String listOfUserName){
        super(opcode);
        ListOfUserName=listOfUserName;
    }

    public void setListOfUserName(String listOfUserName) {ListOfUserName = listOfUserName; }

    @Override
    public String toString() {
        return "STAT " +ListOfUserName;
    }
}
