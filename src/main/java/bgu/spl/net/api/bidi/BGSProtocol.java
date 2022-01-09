package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.CommandMessage.*;
import java.util.List;

public class BGSProtocol implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate = false;
    private User u;
    private final Database database=Database.getInstance();
    private Connections<Message> connections;
    private int HandlerConnectionId;


    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connections=connections;
        HandlerConnectionId=connectionId;
    }

    @Override
    public void process(Message message) {
        short op = message.getOpcode();
        switch (op) {
            //1) REGISTER Messages
            case (1):
                if (u != null || database.isUserRegistered(((REGISTER) message).getUsername())) {
                    connections.send(u.getConnectionId(), new ERROR((short) 11, (short) 1));
                }
                else {
                    u = new User((REGISTER) message, HandlerConnectionId);
                    database.RegisterUser(u);
                    connections.send(HandlerConnectionId, new ACK((short) 10, (short) 1));
                }
                break;
            //2) LOGIN Messages
            case (2):
                u= database.getUser(((LOGIN) message).getUsername());
                if (u == null || !database.isUserRegistered(u.getUserName()) || u.isLogged()
                        || !((LOGIN) message).getPassword().equals(u.getPassword()) || ((LOGIN) message).getCaptcha() == 0)
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 2));
                else {
                    u.setLogged(true);
                    if (u.hasUnSentNotification()) {
                        List<NOTIFICATION> UnSentNotification = u.getUnSentNotification();
                        for (NOTIFICATION notification : UnSentNotification)
                            connections.send(u.getConnectionId(), notification);

                    }
                    connections.send(HandlerConnectionId, new ACK((short) 10, (short) 2));
                }
                break;
            //3) LOGOUT Messages
            case (3):
                if (u == null || !u.isLogged())
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 3));
                else {
                    u.setLogged(false);
                    connections.send(HandlerConnectionId, new ACK((short) 10, (short) 3));
                }
                break;
            //4) FOLLOW Messages
            case (4):
                if (u == null || !u.isLogged())
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 4));
                // follow-a user on the list must not already be on the
                //following list of the logged in user
                else if (((FOLLOW) message).getFollow() == 0) {
                    try {
                        u.Follow(((FOLLOW) message).getUserName());
                        User userToFollowAfter = database.getUser(((FOLLOW) message).getUserName());
                        userToFollowAfter.addFollower(u.getUserName());
                    } catch (Exception e) {
                        connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 4));
                        break;
                    }
                }
                else {//unfollow
                    try {
                        u.unFollow(((FOLLOW) message).getUserName());
                        User userToUnFollow = database.getUser(((FOLLOW) message).getUserName());
                        userToUnFollow.removeFollower(u.getUserName());
                    } catch (Exception e) {
                        connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 4));
                        break;
                    }
                }
                connections.send(HandlerConnectionId, new ACK((short) 10, (short) 4, ((FOLLOW) message).getFollow(), ((FOLLOW) message).getUserName()));
            break;
            //5) POST Messages
            case (5):
                if (u == null || !u.isLogged())
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 5));
                else {
                    database.savePost((POST) message);
                    List<String> sendTo = u.sendMessage((POST) message);
                    for (String userName : sendTo) {
                        if (database.isUserRegistered(userName))
                            connections.send(HandlerConnectionId, new NOTIFICATION((short) 9, (byte) 1, u.getUserName(), ((POST) message).getContent()));
                        else {
                            database.getUser(userName).addUnSentNotification(new NOTIFICATION((short) 9, (byte) 1, u.getUserName(), ((POST) message).getContent()));
                        }
                    }
                    connections.send(HandlerConnectionId, new ACK((short) 10, (short) 5));
                }
                break;
            //6) PM Messages
            case (6):
                if (u == null || !u.isLogged() || u.isBlocking(database.getUser(((PM) message).getUserName())))
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 6));
                else if (!database.isUserRegistered(((PM) message).getUserName()))
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 6, "@" + ((PM) message).getUserName() + " isn’t applicable for private messages"));
                else if (!u.isFollowAfter(database.getUser(((PM) message).getUserName())))
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 6));
                else {
                    database.savePM((PM) message);
                    connections.send(HandlerConnectionId, new NOTIFICATION((short) 9, (byte) 0, u.getUserName(), ((PM) message).getContent()));
                    connections.send(HandlerConnectionId, new ACK((short) 10, (short) 6));
                }
                break;
            //7) LOGSTAT Messages
            case (7):
                if (u == null || !u.isLogged())
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 7));
                else {
                    List<User> LoggedUsers = database.getLoggedInUsers();
                    for (User user : LoggedUsers) {
                        if (!database.isUserRegistered(user.getUserName()))
                            connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 7));
                        if (!u.isBlocking(user) && !user.isBlocking(u))
                            connections.send(HandlerConnectionId, new ACK((short) 10, (short) 7, user.getAge(),user.getCounterPosted(),(short) user.getFollowers().size(),(short) user.getFollowList().size()));
                    }
                }
                break;
            //8) STAT Messages
            case (8):
                if (u == null || !u.isLogged())
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 8));
                else {
                    List<String> StatOfUsers = ((STAT) message).getListOfUserName();
                    for (String username : StatOfUsers) {
                        User user = database.getUser(username);
                        if (u.isBlocking(user) || user.isBlocking(u) || !database.isUserRegistered(username))
                            connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 8));
                        else
                            connections.send(HandlerConnectionId, new ACK((short) 10, (short) 8, user.getAge(),user.getCounterPosted(),(short) user.getFollowers().size(),(short) user.getFollowList().size()));
                    }
                }
                break;
            //12) BLOCK Messages
            case (12):
                if (u == null || !u.isLogged() || !database.isUserRegistered(((BLOCK) message).getUsername()))
                    connections.send(HandlerConnectionId, new ERROR((short) 11, (short) 12));
                else {
                    u.Block(((BLOCK) message).getUsername());
                    connections.send(HandlerConnectionId, new ACK((short) 10, (short) 12));
                }

        }
    }


    @Override
    public boolean shouldTerminate() {
        return false;
    }

}
