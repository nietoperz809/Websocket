package shell;

import server.SimpleWebserver;
import server.SocketServer;
import server.WebSocketHandler;

/**
 * Created by Administrator on 7/3/2017.
 */
public class Runner
{
    public static WebSocketHandler wsh;

    public static void main (String[] args) throws Exception
    {
        CommandLineDispatcher cld;
        SimpleWebserver.start(80);
        wsh = SocketServer.start(8080);
        System.out.println("both servers running");
        cld = new CommandLineDispatcher(wsh);
    }
}
