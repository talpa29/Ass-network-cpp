package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;

import java.io.IOException;


public class connections<T> implements Connections<T> {

    private Database database;

    private static connections instance = null;

    private connections(Database database) {

        this.database = database;
    }

    public static synchronized connections getInstance(Database database) {
        if(instance == null)
            instance = new connections(database);
        return instance;
    }


    @Override
    public boolean send(int connectionId, T msg) {
        ClientDetails clientDetails = database.getClientsIds().get(connectionId);
        database.getClientsHandlers().get(clientDetails).send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {
        database.getClientsIds().get(connectionId).setIslogedin(false);
        try {
            database.getClientsHandlers().get(database.getClientsIds().get(connectionId)).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
