package bgu.spl.net.impl.newsfeed;

import bgu.spl.net.api.Implamantation.BidiMessagingProtocolImpl;
import bgu.spl.net.api.Implamantation.ConnectionsImpl;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.Server;

public class NewsFeedServerMain {

    public static void main(String[] args) {
        NewsFeed feed = new NewsFeed(); //one shared object
        Connections connections = new ConnectionsImpl();
// you can use any server...
        Server.threadPerClient(
                7777, //port
                () -> new BidiMessagingProtocolImpl<>(), //protocol factory
                LineMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();

   //     Server.reactor(
     //           Runtime.getRuntime().availableProcessors(),
       //         7777, //port
         //       () ->  new RemoteCommandInvocationProtocol<>(feed), //protocol factory
           //     ObjectEncoderDecoder::new //message encoder decoder factory
        //).serve();

    }
}
