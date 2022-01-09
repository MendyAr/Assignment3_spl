package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.CommandMessage.PM;
import bgu.spl.net.api.bidi.CommandMessage.POST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private ConcurrentHashMap<String, User> users;
    private LinkedList<Message>postedMessage;
    private ArrayList<String> FilterWord;

    private static class DatabaseSingletonHolder {
        private static Database instance = new Database();
    }
    //to prevent user from creating new Database
    private Database() {
        users = new ConcurrentHashMap<>();
        postedMessage=new LinkedList<>();
        FilterWord = new ArrayList<>(Arrays.asList(
                "spl", "goodvibes", "love"
        ));
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseSingletonHolder.instance;
    }
    public synchronized boolean isUserRegistered(String userName) {
        if (users.containsKey(userName)) {
            return true;
        }
        return false;
    }
    public User getUser(String userName) {
        return users.get(userName);
    }

    public synchronized void RegisterUser(User u) {
        users.putIfAbsent(u.getUserName(), u);
    }
    public void savePost(POST post){postedMessage.add(post);}
    public void savePM(PM pm) {
        for(String filterWord:FilterWord){
            if (pm.getContent().contains(filterWord))
                pm.getContent().replace(filterWord,"<filtered>");
        }
        postedMessage.add(pm);}
    public int UserConnectionId(String userName){return users.get(userName).getConnectionId();}
    public LinkedList<User>getLoggedInUsers(){
        LinkedList<User> LoggedInUsers=new LinkedList<>();
        for (Map.Entry<String,User> entry:users.entrySet()){
            if (entry.getValue().isLogged())
                LoggedInUsers.add(entry.getValue());
        }
        return LoggedInUsers;
    }

}
