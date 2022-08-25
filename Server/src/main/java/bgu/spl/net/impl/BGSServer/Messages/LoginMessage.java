package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class LoginMessage implements Messages {

    private short opcode;
    private String username;
    private String Password;
    private short Captcha;

    public LoginMessage(short opcode, String username, String password, short captcha) {
        this.opcode = opcode;
        this.username = username;
        Password = password;
        Captcha = captcha;
    }

    public short getOpcode() {
        return opcode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Password;
    }

    public short getCaptcha() {
        return Captcha;
    }
}
