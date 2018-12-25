package bgu.spl.net.api.Messages;

public class RegisterMessage implements Message {
    private short opCode;
    private String username;
    private String password;

    @Override
    public short getOpCode() {
        return opCode;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
