package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class BlockMessage implements Messages {

    private short opcode;
    private String username;

    public BlockMessage(short opcode, String username) {
        this.opcode = opcode;
        this.username = username;
    }

    public short getOpcode() {
        return opcode;
    }

    public String getUsername() {
        return username;
    }
}
