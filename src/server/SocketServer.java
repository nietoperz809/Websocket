package server;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

public class SocketServer
{
    public static WebSocketHandler start (int port)
    {
        WebServer webServer = WebServers.createWebServer(port);
        webServer.add(new StaticFileHandler("/static-files"));
        WebSocketHandler wsh = new WebSocketHandler();
        webServer.add("/websocket-echo", wsh);
        webServer.start();
        return wsh;
    }
}

