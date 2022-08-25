package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class StatMessage implements Messages {

    private short opcode;
    private String[] ListOfUsernames;

    public StatMessage(short opcode, String[] listOfUsernames) {
        this.opcode = opcode;
        ListOfUsernames = listOfUsernames;
    }

    public short getOpcode() {
        return opcode;
    }

    public String[] getListOfUsernames() {
        return ListOfUsernames;
    }
}
