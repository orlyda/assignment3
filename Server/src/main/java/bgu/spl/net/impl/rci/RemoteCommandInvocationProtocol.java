package bgu.spl.net.impl.rci;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements BidiMessagingProtocol<Serializable> {

    private T arg;
    private int connectionId;
    private Connections connections;
    public RemoteCommandInvocationProtocol(T arg) {
        this.arg = arg;
    }


    @Override
    public void start(int connectionId, Connections<Serializable> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
    }

    @Override
    public void  process(Serializable msg) {
        connections.send(connectionId,((Command) msg).execute(arg));
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

}
