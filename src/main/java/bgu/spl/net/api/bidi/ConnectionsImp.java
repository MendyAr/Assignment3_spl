package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImp<T> implements Connections<T>{

    private final ConcurrentHashMap<Integer, ConnectionHandler<T>> idConnHandlerMap;

    public ConnectionsImp() { idConnHandlerMap = new ConcurrentHashMap<>(); }

    public boolean register(int connectionId, ConnectionHandler<T> ch){
        ConnectionHandler<T> oldValue = idConnHandlerMap.putIfAbsent(connectionId, ch);
        return oldValue == null || oldValue == ch;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> ch = idConnHandlerMap.get(connectionId);
        if (ch != null) {
            ch.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> ch : idConnHandlerMap.values()){
            ch.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        idConnHandlerMap.remove(connectionId);
    }
}
