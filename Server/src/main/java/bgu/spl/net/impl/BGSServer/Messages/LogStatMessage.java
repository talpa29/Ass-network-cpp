package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class LogStatMessage implements Messages {

    private short opcode;

    public LogStatMessage(short opcode) {
        this.opcode = opcode;
    }

    public short getOpcode() {
        return opcode;
    }
}
