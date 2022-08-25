package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class LogoutMessage implements Messages {

    private short opcode;

    public LogoutMessage(short opcode) {
        this.opcode = opcode;
    }

    public short getOpcode() {
        return opcode;
    }
}
