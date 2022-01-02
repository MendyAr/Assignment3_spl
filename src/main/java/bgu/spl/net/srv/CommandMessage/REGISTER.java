package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

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

    public void setUsername(String username) { Username = username; }

    public void setPassword(String password) { Password = password; }

    public void setBirthday(String birthday) { Birthday = birthday; }

    @Override
    public String toString() {
        return "REGISTER " + Username + ' ' + Password + ' ' +Birthday;
    }
}
