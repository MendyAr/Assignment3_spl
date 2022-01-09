package bgu.spl.net.api.bidi.CommandMessage;

import bgu.spl.net.api.bidi.Message;

public class NOTIFICATION extends Message {
    private byte NotificationType;
    private String PostingUser="";
    private String Content="";

    public NOTIFICATION(short Opcode,byte NotificationType, String PostingUser, String Content){
        super(Opcode);
        this.NotificationType=NotificationType;
        this.PostingUser=PostingUser;
        this.Content=Content;
    }
    public byte getNotificationType() { return NotificationType; }

    public String getPostingUser() { return PostingUser; }

    public String getContent() { return Content; }

    public void setNotificationType(byte notificationType) { NotificationType = notificationType; }

    public void setPostingUser(String postingUser) { PostingUser = postingUser; }

    public void setContent(String content) { Content = content; }

    @Override
    public String toString() {
        String notiType;
        if (NotificationType==1)
            notiType= "Public ";
        else
            notiType="PM ";
        return "NOTIFICATION " +
                notiType + PostingUser + " " + Content;
    }
    public String getMessage(){
        return NotificationType+PostingUser+ "\0"+
                Content;
    }
}
