package bgu.spl.net.api.Implamantation;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {


             Server.reactor(
                   Runtime.getRuntime().availableProcessors(),
                 7777, //port
               BidiMessagingProtocolImpl::new, //protocol factory
             EncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }
}
