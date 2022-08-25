package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class NotificationMessage implements Messages {

    private short opcode;
    private short type; // PM or POST message
    private String postingUser;
    private String content;

    public NotificationMessage(short opcode, short type, String postingUser, String content) {
        this.opcode = opcode;
        this.type = type;
        this.postingUser = postingUser;
        this.content = content;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getType() {
        return type;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}
