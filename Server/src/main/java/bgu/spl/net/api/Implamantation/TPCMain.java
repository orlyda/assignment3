package bgu.spl.net.api.Implamantation;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.srv.Server;



public class TPCMain {



        public static void main(String[] args) {
            if(args.length<1) {
                System.out.println("Not enough arguments");
            }
            else {
                int port = Integer.parseInt(args[0]);
                Server.threadPerClient(
                        port, //port
                        BidiMessagingProtocolImpl::new, //protocol factory
                        EncoderDecoderImpl::new //message encoder decoder factory
                ).serve();
            }
    }

}
