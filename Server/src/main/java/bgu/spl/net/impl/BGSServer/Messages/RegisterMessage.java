package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class RegisterMessage implements Messages {

    private short opcode;
    private String username;
    private String Password;
    private String birthday;

    public RegisterMessage(short opcode, String username, String password, String birthday) {
        this.opcode = opcode;
        this.username = username;
        Password = password;
        this.birthday = birthday;
    }

    @Override
    public short getOpcode() {
        return opcode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Password;
    }

    public String getBirthday() {
        return birthday;
    }
}
