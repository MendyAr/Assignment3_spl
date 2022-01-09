package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.api.bidi.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BGSProtocol;
import bgu.spl.net.api.bidi.Message;

public class ServerMain {

    public static void main(String[] args) {
        if (args.length < 2){
            throw new IllegalArgumentException("Usage: <port> <Reactor/TPC>");
        }
        int port = Integer.decode(args[0]);
        if (args[1].equals("TPC")) {
            Server.threadPerClient(
                    port,
                    () -> new BGSProtocol(){},
                    () -> new MessageEncoderDecoder(){},
                    new ConnectionsImp<Message>()
            ).serve();
        }
        else if (args[1].equals("Reactor")) {
            int numOfThreads=Integer.decode(args[2]);
            Server.reactor(
                    numOfThreads,
                    port,
                    () -> new BGSProtocol(){},
                    () -> new MessageEncoderDecoder(){},
                    new ConnectionsImp<Message>()
            ).serve();
        }
        else{
            throw new IllegalArgumentException("Incorrect Server mode!");
        }

    }
}

