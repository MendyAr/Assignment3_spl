package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

public class ACK extends Message {

    private short MessageOpcode;
    private short Follow;
    private String userName;
    private String Content;
    private short age;
    private short NumPosts;
    private short NumFollower;
    private short NumFollowing;

  public ACK(short opcode, short MessageOpcode){
      super(opcode);
      this.MessageOpcode=MessageOpcode;
      userName="";
      Content="";
  }
  //constructor for follow ack
    public ACK(short opcode, short messageOpcode,short FollowOpcode,String userName){
        super(opcode);
        MessageOpcode=messageOpcode;
        Follow=FollowOpcode;
        this.userName=userName;
        Content="";
    }
    //constructor for LogStat ack
    public ACK(short opcode, short messageOpcode,short age,short NumPosts,short NumFollower, short NumFollowing){
        super(opcode);
        MessageOpcode=messageOpcode;
        this.age=age;
        this.NumPosts=NumPosts;
        this.NumFollower=NumFollower;
        this.NumFollowing=NumFollowing;
        userName="";
    }
  public void setMessageOpcode(short MessageOpcode){
      this.MessageOpcode=MessageOpcode;
  }
    public short getMessageOpcode() { return MessageOpcode; }
    public short getFollow() {
        return Follow;
    }
    public short getNumFollower() { return NumFollower; }
    public short getAge() { return age; }
    public short getNumPosts() { return NumPosts; }
    public short getNumFollowing() { return NumFollowing; }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return Content;
    }

    public String toString() {
      if (MessageOpcode==4)
          //ACK 4 1 Rick
          return MessageOpcode+" "+Follow+" "+userName;
      if (MessageOpcode==7)
          //ACK-Opcode LOGSTAT-Opcode <Age><NumPosts> <NumFollowers> <NumFollowing>
          //ACK 8 47 1 2 0
          return "ACK "+MessageOpcode+Content;
      else
          return "ACK "+ MessageOpcode;


    }
    public String getMessage(){
      return String.valueOf(MessageOpcode);
    }
}
