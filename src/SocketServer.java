import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.StaticFileHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

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

class WebSocketHandler extends BaseWebSocketHandler
{
    private final ConcurrentLinkedQueue<String> receiveQ = new ConcurrentLinkedQueue<>();
    private WebSocketConnection conn;

    public String read()
    {
        if (receiveQ.size() == 0)
            return null;
        return receiveQ.remove();
    }

    public boolean write (String s)
    {
        if (conn == null)
            return false;
        conn.send(s);
        return true;
    }

    @Override
    public void onOpen (WebSocketConnection connection)
    {
        conn = connection;
    }

    @Override
    public void onClose(WebSocketConnection connection)
    {
        conn = null;
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message)
    {
        receiveQ.offer(message);
    }
}