package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

import java.util.LinkedList;

public class PostMessage implements Messages {

    private short opcode;
    private String content;
    private LinkedList<String> otherusers;

    public PostMessage(short opcode, String content, LinkedList<String> otherusers) {
        this.opcode = opcode;
        this.content = content;
        this.otherusers = otherusers;
    }

    public short getOpcode() {
        return opcode;
    }

    public String getContent() {
        return content;
    }

    public LinkedList<String> getOtherusers() {
        return otherusers;
    }
}
