package bgu.spl.net.srv.CommandMessage;

import bgu.spl.net.srv.Message;

public class LOGIN extends Message {
    private String Username;
    private String Password;
    private byte Captcha;

    public LOGIN(short opcode,String username,String password,byte captcha){
        super(opcode);
        Username=username;
        Password=password;
        Captcha=captcha;
    }

    public void setUsername(String username) {Username = username; }

    public void setPassword(String password) {Password = password; }

    public void setCaptcha(byte captcha) {Captcha = captcha; }

    @Override
    public String toString() {
        return "LOGIN " +Username + ' ' +Password;
    }
}
