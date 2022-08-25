package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;


public class ReactorMain {

    public static void main(String[] args) {
        Server.reactor(5,7777,() -> new Bidiprotocol(), () -> new EncDec()).serve();
//        Server.threadPerClient(7777,() -> new Bidiprotocol(),() -> new EncDec()).serve();
    }
}
