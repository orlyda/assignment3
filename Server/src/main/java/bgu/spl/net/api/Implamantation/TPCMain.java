package bgu.spl.net.api.Implamantation;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.srv.Server;



public class TPCMain {



        public static void main(String[] args) {
            Server.threadPerClient(
                    7777, //port
                    BidiMessagingProtocolImpl::new, //protocol factory
                    LineMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();


    }

}
