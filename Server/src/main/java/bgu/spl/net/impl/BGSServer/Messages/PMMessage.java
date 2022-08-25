package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class PMMessage implements Messages {

    private short opcode;
    private String username;
    private String content;
    private String sendingDate_Time;

    public PMMessage(short opcode, String username, String content, String sendingDate_Time) {
        this.opcode = opcode;
        this.username = username;
        this.content = content;
        this.sendingDate_Time = sendingDate_Time;
    }

    public short getOpcode() {
        return opcode;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getSendingDate_Time() {
        return sendingDate_Time;
    }
}
