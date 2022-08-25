package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.ConnectionHandler;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private Map<Integer, ClientDetails> ClientsIds;
    private Map <ClientDetails, ConnectionHandler> ClientsHandlers;
    private Map <String,ClientDetails> usernames;
    private LinkedList<String> FilterdWords;

    private static Database instance = null;

    private Database() {
        ClientsIds = new ConcurrentHashMap<>();
        ClientsHandlers = new ConcurrentHashMap<>();
        usernames = new ConcurrentHashMap<>();
        FilterdWords = new LinkedList<>();

    }

    public static synchronized Database getInstance() {
        if(instance == null)
            instance = new Database();
        return instance;
    }

    public Map<Integer, ClientDetails> getClientsIds() {
        return ClientsIds;
    }

    public Map<ClientDetails, ConnectionHandler> getClientsHandlers() {
        return ClientsHandlers;
    }

    public boolean isregister( int clientId) {
        return ClientsIds.get(clientId).isregistered;
    }

    public boolean isUsernameAvilable(String username,int clientid){
        return usernames.containsKey(username);
    }

    public boolean isLogedin( String username,int clientid) {
        if(username != null) {
            return usernames.get(username).islogedin;
        }
        else
            return ClientsIds.get(clientid).islogedin;
    }

    public void Addusername(String username, ClientDetails clientDetails) {
        usernames.put(username,clientDetails);
    }

    public Map<String, ClientDetails> getUsernames() {
        return usernames;
    }

    public LinkedList<String> getFilterdWords() {
        return FilterdWords;
    }
}
