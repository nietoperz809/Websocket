package server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SimpleWebserver
{
    public static void start (int port)
    {
        try
        {
            new SimpleWebserver().startServer(port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private HttpHandler httpHandler = e ->
    {
        String txt = e.getRequestURI().toString();
        System.out.println("request "+txt);
        String[] split = txt.split("\\?");
        OutputStream os = e.getResponseBody();
        e.sendResponseHeaders(200, 0);
        if (txt.equals("/"))
        {
            SimpleWebserver.this.sendResource("ClientPage.html", os);
        }
        else if (txt.equals("/jquery.js")) //send jquery
        {
            SimpleWebserver.this.sendResource("jquery-3.2.1.min.js", os);
        }
        else if (txt.equals("/favicon.ico")) //send icon
        {
            SimpleWebserver.this.sendResource("logo.png", os);
        }
        os.flush();
        os.close();
    };

    private void startServer (int port) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
        server.setExecutor(Executors.newCachedThreadPool()); // multiple Threads
        server.createContext("/", httpHandler);
        server.start();
    }

    private void sendResource (String name, OutputStream os) throws IOException
    {
        InputStream jqStream = ClassLoader.getSystemResourceAsStream(name);
        byte[] buff = new byte[1024];
        for (; ; )
        {
            int r = jqStream.read(buff);
            if (r == -1)
            {
                break;
            }
            os.write (buff, 0, r);
        }
    }
}
