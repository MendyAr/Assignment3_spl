package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

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
        Username = "";
        Password = "";
        Birthday = "";
        int i=0;
        while (bytes.get(i)!=0) {
            char c = (char) bytes.get(i).shortValue();
            Username = Username + c;
            i++;
        }
        i++;
        while (bytes.get(i)!=0) {
            char c = (char) bytes.get(i).shortValue();
            Password = Password + c;
            i++;
        }
        i++;
        while (bytes.get(i)!=0){
            char c = (char) bytes.get(i).shortValue();
            Birthday=Birthday + c;
            i++;
        }
    }


    public void setUsername(String username) { Username = username; }

    public void setPassword(String password) { Password = password; }

    public void setBirthday(String birthday) { Birthday = birthday; }

    public String getBirthday() { return Birthday; }

    public String getPassword() { return Password; }

    public String getUsername() { return Username; }


    @Override
    public String toString() {
        return "REGISTER " + Username + ' ' + Password + ' ' +Birthday;
    }
}
