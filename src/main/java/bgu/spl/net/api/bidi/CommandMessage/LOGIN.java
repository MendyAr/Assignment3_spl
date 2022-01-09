package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

import java.util.List;

public class LOGIN extends Message {
    private String Username="";
    private String Password="";
    private byte Captcha;

    public LOGIN(short opcode,String username,String password,byte captcha){
        super(opcode);
        Username=username;
        Password=password;
        Captcha=captcha;
    }

    public LOGIN(short op,List<Byte> bytes) {
        super(op);
        int i=0;
        while (bytes.get(i)!=0){
            char c = (char) bytes.get(i).shortValue();
            Username = Username + c;
            i++;
        }
        i++;
        while (bytes.get(i)!=0){
            char c = (char) bytes.get(i).shortValue();
            Password=Password + c;
            i++;
        }
        i++;
        Captcha=bytes.get(i);
    }


    public void setUsername(String username) {Username = username; }

    public void setPassword(String password) {Password = password; }

    public void setCaptcha(byte captcha) {Captcha = captcha; }
    public String getUsername() { return Username; }

    public String getPassword() { return Password; }

    public byte getCaptcha() { return Captcha; }

    @Override
    public String toString() {
        return "LOGIN " +Username + ' ' +Password;
    }
}
