package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class REGISTER extends Message {
    private String Username;
    private String Password;
    private String Birthday;


    public REGISTER(short opcode,String username,String password,String birthday){
        super(opcode);
        Username=username;
        Password=password;
        Birthday=birthday;

    }

    public REGISTER(short op,List<Byte> bytes) {
       super(op);
        int i=0;
        while (bytes.get(i)!=0){
            Username=Username+bytes.get(i);
        }
        i++;
        while (bytes.get(i)!=0){
            Password=Password+bytes.get(i);
        }
        i++;
        while (bytes.get(i)!=0){
            Birthday=Birthday+bytes.get(i);
        }
    }


    public void setUsername(String username) { Username = username; }

    public void setPassword(String password) { Password = password; }

    public void setBirthday(String birthday) { Birthday = birthday; }

    @Override
    public String toString() {
        return "REGISTER " + Username + ' ' + Password + ' ' +Birthday;
    }
}
