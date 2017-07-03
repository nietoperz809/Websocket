package server;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Administrator on 7/3/2017.
 */
public class WebSocketHandler extends BaseWebSocketHandler
{
    private final ArrayBlockingQueue<String> receiveQ = new ArrayBlockingQueue<>(100);
    private WebSocketConnection conn;

    public String read()
    {
        try
        {
            return receiveQ.take();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
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
        try
        {
            receiveQ.put(message);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}