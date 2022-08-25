package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Messages;

public class AckMessage implements Messages {

    private short opcode;
    private short messageOpcode;
    private String username; //for follow ack
    private short age; // for stat or logstat message
    private short numOfPosts; // for stat or logstat message
    private short numOfFollowers; // for stat or logstat message
    private short numOfFollowing; // for stat or logstat message

    public AckMessage(short opcode, short messageOpcode, String username, short age, short numOfPosts, short numOfFollowers, short numOfFollowing) {
        this.opcode = opcode;
        this.messageOpcode = messageOpcode;
        this.username = username;
        this.age = age;
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    @Override
    public short getOpcode() {
        return opcode;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }

    public String getUsername() {
        return username;
    }

    public short getAge() {
        return age;
    }

    public short getNumOfPosts() {
        return numOfPosts;
    }

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }
}
