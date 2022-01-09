package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.CommandMessage.NOTIFICATION;
import bgu.spl.net.api.bidi.CommandMessage.POST;
import bgu.spl.net.api.bidi.CommandMessage.REGISTER;;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.time.Year;

public class User {
    private String userName;
    private String password;
    private byte captcha;
    private Queue<String>  followList; //The users the user is following
    private Queue<String> followers; //The user’s followers
    private int age;
    private String birthday;
    private int counterPosted;
    private boolean isLogged;
    private List<NOTIFICATION>unSentNotification;
    private List<String> Blocked;//the users the current user is blocked
    private int connectionId; //need to initialize connectionId

    public User(REGISTER message, int HandlerConnectionId) {
        if (isValidDate(message.getBirthday()))
            throw new IllegalArgumentException();
        userName=message.getUsername();
        password=message.getPassword();
        birthday=message.getBirthday();
        isLogged=false;
        int birthYear=Integer.valueOf(birthday.substring(6));//need to check if the slicing is good->DD-MM-YYYY
        age= Year.now().getValue()-birthYear;
        captcha=1;
        followers=new ConcurrentLinkedQueue<String>();
        followList=new ConcurrentLinkedQueue<String>();
        unSentNotification=new LinkedList<>();
        Blocked=new LinkedList<>();
        connectionId=HandlerConnectionId;
    }
    private boolean isValidDate(String birthday){
        return birthday.matches("([0-3]\\d-[0-1]\\d-\\d{4})");
        //return birthday.matches("^(((0|[12]\\d|3[01])\\-(0[13578]|1[02])\\-((19|[2-9]\\d)\\d{2}))|((0[1-9]|[12]\\d|30)\\-(0[13456789]|1[012])\\-((19|[2-9]\\d)\\d{2}))|((0[1-9]|1\\d|2[0-8])\\-02\\-((19|[2-9]\\d)\\d{2}))|(29\\-02\\-((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|(([1][26]|[2468][048]|[3579][26])00))))$");
    }

    public boolean isLogged() { return isLogged; }

    public String getUserName() {return userName; }
    public int getConnectionId(){return connectionId;}

    public String getPassword() {return password; }
    public Queue<String> getFollowList() { return followList; }

    public List<NOTIFICATION> getUnSentNotification() { return unSentNotification; }

    public void setLogged(boolean b) {isLogged=b; }

    public void Follow(String userName) throws Exception {
        if (followList.contains(userName) || Blocked.contains(userName))
            throw new Exception();
        followList.add(userName); }

    public void unFollow(String userName) throws Exception {
        if (!followList.contains(userName))
            throw new Exception();
        followList.remove(userName); }

    public List<String> sendMessage(POST message) {
        counterPosted++;
        //return list of all the users that need to get the post
        List<String> sendTo= new ArrayList<String>(followers);
        sendTo.addAll(message.specificUsers());
        return sendTo;
    }
    public void addFollower(String UserName){followers.add(userName);}
    public void addUnSentNotification(NOTIFICATION notification){unSentNotification.add(notification);}

    public void removeFollower(String userName) {followers.remove(userName); }

    public boolean hasUnSentNotification() {return !unSentNotification.isEmpty();}
    public boolean isBlocking(User u){return Blocked.contains(u);}

    public boolean isFollowAfter(User user) {return followList.contains(user);}
    public String Status(){
        //<Age><NumPosts> <NumFollowers> <NumFollowing>
        return age+" "+counterPosted+" "+followers.size()+" "+followList.size();
    }

    public void Block(String username) {
        Blocked.add(username);
        if (followList.contains(username))
            followList.remove(username);
        if (followers.contains(username))
            followList.remove(username);
    }
}
