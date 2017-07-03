/**
 * Created by Administrator on 7/3/2017.
 */
public class Runner
{
    public static void main (String[] args) throws Exception
    {
        SimpleWebserver.start(80);
        WebSocketHandler wsh = SocketServer.start(8080);
        System.out.println("both servers running");
        for (;;)
        {
            Thread.sleep(100);
            String s = wsh.read();
            if (s != null)
            {
                System.out.println(s);
                wsh.write(s);
            }
        }
    }
}
