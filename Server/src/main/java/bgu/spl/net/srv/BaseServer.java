package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.ClientDetails;
import bgu.spl.net.impl.BGSServer.Database;
import bgu.spl.net.impl.BGSServer.connections;
import bgu.spl.net.impl.BGSServer.connectionHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    //TODO- we add here the connections
    private ServerSocket sock;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
            System.out.println("Server started");

            this.sock = serverSock; //just to be able to close
            int clientid = 0;
            while (!Thread.currentThread().isInterrupted()) {
                Database database= Database.getInstance();
                Socket clientSock = serverSock.accept();
                Connections<T> connection = connections.getInstance(database);

                connectionHandler<T> handler = new connectionHandler<T>(protocolFactory.get(),encdecFactory.get(),clientSock,connection,clientid);
                //create new <clientid,clientdetails with the new id> to db
                //create new <clientdetails,Handler> to db.
                //create new handler with connections and id, then in handler we start the bidiprotocol
                ClientDetails clientDetails = new ClientDetails(clientid);
                database.getClientsIds().put(clientid,clientDetails);
                database.getClientsHandlers().put(clientDetails,handler);
                execute(handler);
                clientid++;
            }
        } catch (IOException ex) {
        }

//        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(connectionHandler<T>  handler);

}
