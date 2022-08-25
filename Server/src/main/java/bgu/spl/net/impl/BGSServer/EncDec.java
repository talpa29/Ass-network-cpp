package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.Messages;
import bgu.spl.net.impl.BGSServer.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class EncDec implements MessageEncoderDecoder<Messages> {

    //    @Override
//    public Messages decodeNextByte(byte nextByte) {
//        return null;
//    }
//
//    @Override
//    public byte[] encode(Messages message) {
//        return new byte[0];
//    }
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private byte[] opbyte = new byte[1 << 10];
    private int len = 0;
    LinkedList<String> msg = new LinkedList<>();
    private int count = 0;

    @Override
    public Messages decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            short opcode = pooOpcode();
            switch (opcode) {
                case 1: {
                    String username = msg.removeFirst();
                    String password = msg.removeFirst();
                    String birthday = msg.removeFirst();
                    RegisterMessage registerMessage = new RegisterMessage(opcode, username, password, birthday);
                    msg.clear();
                    return registerMessage;

                }
                case 2: {
                    String username = msg.removeFirst();
                    String password = msg.removeFirst();
                    String captcha;
                    if (msg.isEmpty()) {
                        captcha = "0";
                    }
                    else
                    {
                        captcha = msg.removeFirst();
                    }
                    byte[] test = captcha.getBytes();
                    short captchas;
                    if (test[0] == 49) {
                        captchas = 1;
                    } else
                        captchas = 0;
                    msg.clear();
                    return new LoginMessage(opcode, username, password, captchas);

                }
                case 3: {
                    msg.clear();
                    return new LogoutMessage(opcode);
                }
                case 4: {
                    String followUnfollow = msg.removeFirst();
                    String username = msg.removeFirst();
                    byte[] test = followUnfollow.getBytes();
                    short followUnfollows;
                    if (test[0] == 49) {
                        followUnfollows = 1;
                    } else
                        followUnfollows = 0;
                    msg.clear();
                    return new FollowMessage(opcode, followUnfollows, username);
                }
                case 5: {
                    String content = "";
                    LinkedList<String> users = new LinkedList<>();

                    while (!msg.isEmpty() && !msg.getFirst().equals("0")) {
                        String str = msg.removeFirst();

                        if (str.charAt(0) == '@') {
                            users.add(str.substring(1));
                        }
                        content = content + " " + str;
                    }
                    content = content.substring(1);
                    msg.clear();
                    return new PostMessage(opcode, content, users);
                }
                case 6: {
                    String username = msg.removeFirst();
                    String content = msg.removeFirst();
                    String Sending_Date_And_Time = msg.removeFirst();
                    msg.clear();
                    return new PMMessage(opcode, username, content, Sending_Date_And_Time);
                }
                case 7: {
                    msg.clear();
                    return new LogStatMessage(opcode);
                }
                case 8: {
                    String usernamelist = msg.removeFirst();
                    msg.clear();
                    return new StatMessage(opcode, usernamelist.split("\\|"));
                }
                case 12: {
                    String username = msg.removeFirst();
                    BlockMessage blockMessage = new BlockMessage(opcode, username);
                    msg.clear();
                    return blockMessage;
                }
            }
        }
        if (nextByte == 0) {
            if (count > 1) {
                String word = popString();
                msg.add(word);
            }

        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Messages message) {
        short opcode = message.getOpcode();
        switch (opcode) {
            case 9: {
                byte[] opbyte = new byte[2];
                opbyte[0] = (byte)((opcode >> 8) & 0xFF);
                opbyte[1] = (byte)(opcode & 0xFF);
                byte[] content = NotificationMsg((NotificationMessage) message); //uses utf8 by default
                byte[] result = new byte[opbyte.length + content.length];
                result[0] = opbyte[0];
                result[1] = opbyte[1];
                for (int i = 2; i < content.length + 2; i++ ) {
                    result[i] = content[i - 2];
                }
                return result;

            }
            case 10: {
                return (ACKMsg((AckMessage) message) ); //uses utf8 by default
            }
            case 11:
                return (ErrorMsg((ErrorMessage) message)); //uses utf8 by default
        }
        return null;
    }

    private void pushByte(byte nextByte) {
        if (count < 2) {
            opbyte[count++] = nextByte;
        } else {
            if (nextByte != 0) {
                if (len >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, len * 2);
                }

                bytes[len++] = nextByte;
            }
        }

    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private short pooOpcode() {
        short opcode = (short) ((opbyte[0] & 0xff) << 8);
        opcode += (short) (opbyte[1] & 0xff);
        count = 0;
        return opcode;

    }

    private byte[] NotificationMsg(NotificationMessage notificationMessage) {
        byte[] bytesArr = new byte[1];
        bytesArr[0] = (byte)(notificationMessage.getType() & 0xFF);
        String content = notificationMessage.getPostingUser() + "\0";
        content = content + notificationMessage.getContent() + "\0" + ";";
        byte[] result = new byte[bytesArr.length + content.getBytes().length];
        System.arraycopy(bytesArr,0,result,0,bytesArr.length);
        System.arraycopy(content.getBytes(),0,result,bytesArr.length,content.getBytes().length);
        return result;
    }

    private byte[] ACKMsg(AckMessage ackMessage) {
        short opcpde = 10;
        short msgOpcode = ackMessage.getMessageOpcode();

        if (msgOpcode == 4) {
            byte[] opcodeAndMsgOpcode = new byte[4];
            opcodeAndMsgOpcode[0] = (byte)((opcpde >> 8) & 0xFF);
            opcodeAndMsgOpcode[1] = (byte)(opcpde & 0xFF);
            opcodeAndMsgOpcode[2] = (byte)((msgOpcode >> 8) & 0xFF);
            opcodeAndMsgOpcode[3] = (byte)(msgOpcode & 0xFF);
            String content;
            content = ackMessage.getUsername() + "\0" + ";";
            byte[] contentbyte = content.getBytes();
            byte[] result = new byte[4 + contentbyte.length];
            System.arraycopy(opcodeAndMsgOpcode,0,result,0,4);
            System.arraycopy(contentbyte,0,result,4,contentbyte.length);
            return result;
        }
        else if (msgOpcode == 7 | msgOpcode == 8) {
            byte[] ackmsgbyte = new byte[13];
            ackmsgbyte[0] = (byte)((opcpde >> 8) & 0xFF);
            ackmsgbyte[1] = (byte)(opcpde & 0xFF);
            ackmsgbyte[2] = (byte)((msgOpcode >> 8) & 0xFF);
            ackmsgbyte[3] = (byte)(msgOpcode & 0xFF);
            ackmsgbyte[4] = (byte)((ackMessage.getAge() >> 8) & 0xFF);
            ackmsgbyte[5] = (byte)(ackMessage.getAge() & 0xFF);
            ackmsgbyte[6] = (byte)((ackMessage.getNumOfPosts() >> 8) & 0xFF);
            ackmsgbyte[7] = (byte)(ackMessage.getNumOfPosts() & 0xFF);
            ackmsgbyte[8] = (byte)((ackMessage.getNumOfFollowers() >> 8) & 0xFF);
            ackmsgbyte[9] = (byte)(ackMessage.getNumOfFollowers() & 0xFF);
            ackmsgbyte[10] = (byte)((ackMessage.getNumOfFollowing() >> 8) & 0xFF);
            ackmsgbyte[11] = (byte)(ackMessage.getNumOfFollowing() & 0xFF);
            ackmsgbyte[12] = ';';
            return ackmsgbyte;
        }
        else {
            byte[] ackmsgbyte = new byte[5];
            ackmsgbyte[0] = (byte)((opcpde >> 8) & 0xFF);
            ackmsgbyte[1] = (byte)(opcpde & 0xFF);
            ackmsgbyte[2] = (byte)((msgOpcode >> 8) & 0xFF);
            ackmsgbyte[3] = (byte)(msgOpcode & 0xFF);
            ackmsgbyte[4] = ';';
            return ackmsgbyte;

        }
    }

    private byte[] ErrorMsg(ErrorMessage errorMessage) {
        short opcpde = 11;
        short msgOpcode = errorMessage.getMessageOpcode();
        byte[] error = new byte[5];
        error[0] = (byte)((opcpde >> 8) & 0xFF);
        error[1] = (byte)(opcpde & 0xFF);
        error[2] = (byte)((msgOpcode >> 8) & 0xFF);
        error[3] = (byte)(msgOpcode & 0xFF);
        error[4] = ';';
        return error;
    }
}
