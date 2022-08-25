package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.Messages;
import bgu.spl.net.impl.BGSServer.Messages.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Bidiprotocol implements BidiMessagingProtocol<Messages> {

    private boolean shouldterminate;
    private Connections connections;
    private int clientid;
    private Database database;

    @Override
    public void start(int connectionId, Connections<Messages> connections) {
        clientid = connectionId;
        this.connections = connections;
        database = Database.getInstance();
        shouldterminate = false;
    }


    @Override
    public void process(Messages message) {
        short opcode = message.getOpcode();
        switch (opcode) {
            case 1: {
                register((RegisterMessage) message);
                break;
            }
            case 2: {
                Login((LoginMessage) message, clientid);
                break;
            }
            case 3: {
                Logout((LogoutMessage) message, clientid);
                break;
            }
            case 4: {
                follow((FollowMessage) message, clientid);
                break;
            }
            case 5: {
                post((PostMessage) message, clientid);
                break;
            }
            case 6: {
                PM((PMMessage) message, clientid);
                break;
            }
            case 7: {
                logStat((LogStatMessage) message, clientid);
                break;
            }
            case 8: {
                stat((StatMessage) message, clientid);
                break;
            }
            case 12: {
                Block((BlockMessage) message, clientid);
                break;
            }

        }


    }

    @Override
    public boolean shouldTerminate() {
        return shouldterminate;
    }

    public void terminate() {
        shouldterminate = true;
    }


    public void register(RegisterMessage message) {
        if (database.isregister(clientid)) {
            ErrorMessage errorMessage = new ErrorMessage((short) 11, (short) 1);
            connections.send(clientid, errorMessage);
        } else {

            if (!database.isUsernameAvilable(message.getUsername(), clientid)) {
                database.getClientsIds().get(clientid).setUsername(message.getUsername());
                database.getClientsIds().get(clientid).setBirthday(message.getBirthday());
                database.getClientsIds().get(clientid).setPassword(message.getPassword());
                database.getClientsIds().get(clientid).setIsregistered(true);
                database.Addusername(message.getUsername(), database.getClientsIds().get(clientid));
                AckMessage ackMessage = new AckMessage((short) 10, (short) 1, null, (short) 0, (short) 0, (short) 0, (short) 0);
                connections.send(clientid, ackMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage((short) 11, (short) 1);
                connections.send(clientid, errorMessage);
            }
        }
    }

    public void Login(LoginMessage loginMessage, int clientid) {
        if (database.getUsernames().containsKey(loginMessage.getUsername()) && database.isregister(database.getUsernames().get(loginMessage.getUsername()).getClientId())) {
            if (!database.isLogedin(loginMessage.getUsername(), clientid)) {
                if (database.getUsernames().get(loginMessage.getUsername()).getPassword().equals(loginMessage.getPassword())) {
                    if (loginMessage.getCaptcha() == 1) {
                        ////////////////////////////////////////
                        ClientDetails oldclient = database.getUsernames().get(loginMessage.getUsername());
                        ClientDetails newclientDetails = database.getClientsIds().get(clientid);
                        newclientDetails.setUsername(loginMessage.getUsername());
                        newclientDetails.setBirthday(oldclient.getBirthday());
                        newclientDetails.setPassword(loginMessage.getPassword());
                        newclientDetails.setBlockedUsers(oldclient.getBlockedUsers());
                        newclientDetails.setFollowers(oldclient.getFollowers());
                        newclientDetails.setFollowing(oldclient.getFollowing());
                        newclientDetails.setMessages(oldclient.getMessages());
                        newclientDetails.setNumOfPosts(oldclient.getNumOfPosts());
                        database.getUsernames().remove(loginMessage.getUsername());
                        database.getUsernames().put(loginMessage.getUsername(), newclientDetails);
                        database.getClientsIds().get(clientid).setIslogedin(true);
                        database.getClientsIds().get(clientid).setIsregistered(true);
                        AckMessage ackMessage = new AckMessage((short) 10, (short) 2, null, (short) 0, (short) 0, (short) 0, (short) 0);
                        connections.send(clientid, ackMessage);
                        /////////////////////////////////////////
                        LinkedList<Messages> messagesToSend = database.getClientsIds().get(clientid).getMessages();
                        if (messagesToSend != null) {
                            while (!messagesToSend.isEmpty()) {
                                connections.send(clientid, messagesToSend.removeFirst());
                            }
                        }
                    } else {
                        connections.send(clientid, new ErrorMessage((short) 11, (short) 2));
                    }

                } else
                    connections.send(clientid, new ErrorMessage((short) 11, (short) 2));
            } else
                connections.send(clientid, new ErrorMessage((short) 11, (short) 2));

        } else
            connections.send(clientid, new ErrorMessage((short) 11, (short) 2));
    }

    public void Logout(LogoutMessage logoutMessage, int clientid) {
        if (database.isregister(clientid)) {
            if (database.isLogedin(null, clientid)) {
                database.getClientsIds().get(clientid).setIslogedin(false);
                AckMessage ackMessage = new AckMessage((short) 10, (short) 3, null, (short) 0, (short) 0, (short) 0, (short) 0);
                connections.send(clientid, ackMessage);
                terminate();
            } else
                connections.send(clientid, new ErrorMessage((short) 11, (short) 3));
        } else
            connections.send(clientid, new ErrorMessage((short) 11, (short) 3));

    }

    public void follow(FollowMessage followMessage, int clienid) {
        if (database.isregister(clienid)) {
            if (database.isLogedin(null, clientid)) {
                //follow case
                if (followMessage.getFollow() == 0) {
                    if (!database.getUsernames().containsKey(followMessage.getUsername()) || database.getClientsIds().get(clientid).getFollowing().contains(followMessage.getUsername()) |
                            database.getClientsIds().get(clientid).getBlockedUsers().contains(followMessage.getUsername()))
                        connections.send(clientid, new ErrorMessage((short) 11, (short) 4));
                    else {
                        database.getClientsIds().get(clientid).getFollowing().add(followMessage.getUsername());
                        database.getUsernames().get(followMessage.getUsername()).getFollowers().add(database.getClientsIds().get(clientid).getUsername());
                        AckMessage ackMessage = new AckMessage((short) 10, (short) 4, followMessage.getUsername(), (short) 0, (short) 0, (short) 0, (short) 0);
                        connections.send(clientid, ackMessage);
                    }


                } else {
                    if (database.getUsernames().containsKey(followMessage.getUsername()) || database.getClientsIds().get(clientid).getFollowing().contains(followMessage.getUsername())) {
                        database.getClientsIds().get(clientid).getFollowing().remove(followMessage.getUsername());
                        database.getUsernames().get(followMessage.getUsername()).getFollowers().remove(database.getClientsIds().get(clientid).getUsername());
                        AckMessage ackMessage = new AckMessage((short) 10, (short) 4, followMessage.getUsername(), (short) 1, (short) 0, (short) 0, (short) 0);
                        connections.send(clientid, ackMessage);
                    } else
                        connections.send(clientid, new ErrorMessage((short) 11, (short) 4));
                }
            } else
                connections.send(clientid, new ErrorMessage((short) 11, (short) 4));
        } else
            connections.send(clientid, new ErrorMessage((short) 11, (short) 4));
    }

    public void post(PostMessage message, int clientid) {
        if (database.isregister(clientid)) {
            if (database.isLogedin(null, clientid)) {
                LinkedList<String> followers;
                followers = (LinkedList<String>) database.getClientsIds().get(clientid).getFollowers().clone();
                database.getClientsIds().get(clientid).addPost();
                while (!followers.isEmpty()) {
                    String follower = followers.removeFirst();
                    NotificationMessage notificationMessage = new NotificationMessage((short) 9, (short) 1, database.getClientsIds().get(clientid).getUsername(), message.getContent());
                    ClientDetails clientDetails = database.getUsernames().get(follower);
                    if (database.isLogedin(follower, 0)) {
                        connections.send(clientDetails.getClientId(), notificationMessage);
                    } else {
                        clientDetails.getMessages().add(notificationMessage);
                    }
                }
                while (!message.getOtherusers().isEmpty()) {
                    String usertosend = message.getOtherusers().removeFirst();
                    if (database.getUsernames().containsKey(usertosend) && !database.getUsernames().get(usertosend).getBlockedUsers().contains(database.getClientsIds().get(clientid).getUsername())) {
                        NotificationMessage notificationMessage = new NotificationMessage((short) 9, (short) 1, database.getClientsIds().get(clientid).getUsername(), message.getContent());
                        ClientDetails clientDetails = database.getUsernames().get(usertosend);
                        if (database.isLogedin(usertosend, 0)) {
                            connections.send(clientDetails.getClientId(), notificationMessage);
                        } else {
                            clientDetails.getMessages().add(notificationMessage);
                        }
                    }
                }
                AckMessage ackMessage = new AckMessage((short) 10, (short) 5, null, (short) 0, (short) 0, (short) 0, (short) 0);
                connections.send(clientid, ackMessage);
            } else {
                connections.send(clientid, new ErrorMessage((short) 11, (short) 5));

            }
        } else
            connections.send(clientid, new ErrorMessage((short) 11, (short) 5));
    }

    public void PM(PMMessage pmMessage, int clientid) {
        if (!database.isregister(clientid) | !database.isLogedin(null, clientid) |
                !database.getUsernames().containsKey(pmMessage.getUsername()) ||
                !database.isregister(database.getUsernames().get(pmMessage.getUsername()).getClientId()) |
                        database.getClientsIds().get(clientid).getBlockedUsers().contains(pmMessage.getUsername())) {
            connections.send(clientid, new ErrorMessage((short) 11, (short) 6));
        } else {
            String content = pmMessage.getContent() + " " + pmMessage.getSendingDate_Time();
            Iterator<String> it = database.getFilterdWords().iterator();
            while (it.hasNext()) {
                content = content.replaceAll(it.next(), "<filtered>");
            }
            NotificationMessage notificationMessage = new NotificationMessage((short) 9, (short) 0, database.getClientsIds().get(clientid).getUsername(), content);
            if (database.getUsernames().get(pmMessage.getUsername()).islogedin) {
                connections.send(database.getUsernames().get(pmMessage.getUsername()).getClientId(), notificationMessage);
            } else {
                database.getUsernames().get(pmMessage.getUsername()).addMessageTobeSent(notificationMessage);
            }
            AckMessage ackMessage = new AckMessage((short) 10, (short) 6, null, (short) 0, (short) 0, (short) 0, (short) 0);
            connections.send(clientid, ackMessage);
        }
    }

    public void logStat(LogStatMessage logStatMessage, int clientid) {
        if (!database.isregister(clientid) | !database.isLogedin(null, clientid)) {
            connections.send(clientid, new ErrorMessage((short) 11, (short) 7));
        } else {
            LinkedList<ClientDetails> logedInClients = new LinkedList<>();
            for (Map.Entry<String, ClientDetails> entry : database.getUsernames().entrySet()) {
                ClientDetails clientDetails = entry.getValue();
                if (clientDetails.islogedin) {
                    logedInClients.add(clientDetails);
                }
            }
            while (!logedInClients.isEmpty()) {
                ClientDetails clientDetails = logedInClients.removeFirst();
                if (!database.getUsernames().get(clientDetails.getUsername()).getBlockedUsers().contains(database.getClientsIds().get(clientid).getUsername())) {
                    AckMessage ackMessage = new AckMessage((short) 10, (short) 7, null, (short) clientDetails.getAge(), (short) clientDetails.getNumOfPosts(), (short) clientDetails.getFollowers().size(), (short) clientDetails.getFollowing().size());
                    connections.send(clientid, ackMessage);
                }
            }
        }

    }

    public void stat(StatMessage statMessage, int clientid) {
        if (!database.isregister(clientid) | !database.isLogedin(null, clientid)) {
            connections.send(clientid, new ErrorMessage((short) 11, (short) 8));
        } else {
            for (int i = 0; i < statMessage.getListOfUsernames().length; i++) {
                if (database.getUsernames().containsKey(statMessage.getListOfUsernames()[i])) {
                    ClientDetails clientDetails = database.getUsernames().get(statMessage.getListOfUsernames()[i]);
                    if (!database.getUsernames().get(clientDetails.getUsername()).getBlockedUsers().contains(database.getClientsIds().get(clientid).getUsername())) {
                        AckMessage ackMessage = new AckMessage((short) 10, (short) 8, null, (short) clientDetails.getAge(), (short) clientDetails.getNumOfPosts(), (short) clientDetails.getFollowers().size(), (short) clientDetails.getFollowing().size());
                        connections.send(clientid, ackMessage);
                    }
                } else {
                    connections.send(clientid, new ErrorMessage((short) 11, (short) 8));
                }
            }
        }
    }

    public void Block(BlockMessage blockMessage, int clientid) {
        if (!database.isregister(clientid) | !database.isLogedin(null, clientid) |
                !database.getUsernames().containsKey(blockMessage.getUsername()) ||
                !database.isregister(database.getUsernames().get(blockMessage.getUsername()).getClientId()) |
                        database.getClientsIds().get(clientid).getBlockedUsers().contains(blockMessage.getUsername())) {
            connections.send(clientid, new ErrorMessage((short) 11, (short) 12));
        } else {
            if (database.getClientsIds().get(clientid).getFollowing().contains(blockMessage.getUsername())) {
                database.getClientsIds().get(clientid).getFollowing().remove(blockMessage.getUsername());
                database.getUsernames().get(blockMessage.getUsername()).getFollowers().remove(database.getClientsIds().get(clientid).getUsername());
            }
            if (database.getClientsIds().get(clientid).getFollowers().contains(blockMessage.getUsername())) {
                database.getUsernames().get(blockMessage.getUsername()).getFollowing().remove(database.getClientsIds().get(clientid).getUsername());
                database.getClientsIds().get(clientid).getFollowers().remove(blockMessage.getUsername());
            }
            database.getClientsIds().get(clientid).getBlockedUsers().add(blockMessage.getUsername());
            database.getUsernames().get(blockMessage.getUsername()).getBlockedUsers().add(database.getClientsIds().get(clientid).getUsername());
            AckMessage ackMessage = new AckMessage((short) 10, (short) 12, null, (short) 0, (short) 0, (short) 0, (short) 0);
            connections.send(clientid, ackMessage);
        }
    }

}


