package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class ErrorMessage implements Messages {

    private short opcode;
    private short messageOpcode;

    public ErrorMessage(short opcode, short messageOpcode) {
        this.opcode = opcode;
        this.messageOpcode = messageOpcode;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }
}
