package bgu.spl.net.api.Implamantation;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        if(args.length<2)
            System.out.println("Not enough arguments");
        else {
            int port = Integer.parseInt(args[0]);
            int numThreads = Integer.parseInt(args[1]);
            Server.reactor(
                   numThreads,
                    port,
                    BidiMessagingProtocolImpl::new, //protocol factory
                    EncoderDecoderImpl::new //message encoder decoder factory
            ).serve();
        }
    }
}
