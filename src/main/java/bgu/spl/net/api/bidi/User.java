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
    private short age;
    private String birthday;
    private short counterPosted;
    private boolean isLogged;
    private List<NOTIFICATION>unSentNotification;
    private List<String> Blocked;//the users the current user is blocked
    private int connectionId; //need to initialize connectionId
    public User(REGISTER message, int HandlerConnectionId) {
        userName=message.getUsername();
        password=message.getPassword();
        birthday=message.getBirthday();
        isLogged=false;
        int birthYear=Integer.valueOf(birthday.substring(6));//need to check if the slicing is good->DD-MM-YYYY
        age= (short)(Year.now().getValue()-birthYear);
        captcha=1;
        followers=new ConcurrentLinkedQueue<String>();
        followList=new ConcurrentLinkedQueue<String>();
        unSentNotification=new LinkedList<>();
        Blocked=new LinkedList<>();
        connectionId=HandlerConnectionId;
    }

    public boolean isLogged() { return isLogged; }

    public String getUserName() {return userName; }
    public int getConnectionId(){return connectionId;}
    public Queue<String> getFollowers() { return followers; }

    public short getAge() { return age; }

    public short getCounterPosted() { return counterPosted; }


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
