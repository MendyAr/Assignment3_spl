package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionsImp;

public class ServerMain {

    public static void main(String[] args) {

        Server.threadPerClient(
                7777, //port
                () -> new BidiMessagingProtocol<String>(new ConnectionsImp<String>()) {}, //protocol factory
                MessageEncoderDecoder<String>::new,           //message encoder decoder factory
        ).serve();

//        Server.reactor(
//                Runtime.getRuntime().availableProcessors(),
//                7777, //port
//                () ->  new RemoteCommandInvocationProtocol<>(feed), //protocol factory
//                ObjectEncoderDecoder::new //message encoder decoder factory
//        ).serve();

    }
}
