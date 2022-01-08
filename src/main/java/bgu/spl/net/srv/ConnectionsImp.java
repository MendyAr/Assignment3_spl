package bgu.spl.net.srv;

public class ConnectionsImp implements bgu.spl.net.api.bidi.Connections {
    //need to add field of mad the id and the handlers
    public ConnectionsImp(int connectionId) {//need to add this id to the map of connection id
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        return false;
    }

    @Override
    public void broadcast(Object msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }
}
