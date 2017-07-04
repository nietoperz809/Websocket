package shell;

import com.sixtyfour.Basic;
import com.sixtyfour.DelayTracer;
import server.WebSocketHandler;

/**
 * Proxy class to instantiate an run the BASIC system
 */
public class BasicRunner implements Runnable
{
    private static volatile boolean running = false;
    private Basic olsenBasic;
    private Thread basicThread;

    public BasicRunner (String[] program, int speed, WebSocketHandler r)
    {
        if (running)
        {
            return;
        }
        olsenBasic = new Basic(program);
        r.setBasicRunner(this);
        if (speed > 0)
        {
            DelayTracer t = new DelayTracer(speed);
            olsenBasic.setTracer(t);
        }
        //olsenBasic.getMachine().setMemoryListener(new PeekPokeHandler(shellFrame));
        olsenBasic.setOutputChannel(new ShellOutputChannel(r));
        olsenBasic.setInputProvider(new ShellInputProvider(r));
    }

    /**
     * Compile an run a single line
     *
     * */
    public static String runSingleLine (String in, WebSocketHandler r)
    {
        try
        {
            Basic b = new Basic("0 " + in.toUpperCase());
            //b.getMachine().setMemoryListener(new PeekPokeHandler(sf));
            b.compile();
            b.setOutputChannel(new ShellOutputChannel(r));
            b.setInputProvider(new ShellInputProvider(r));
            b.start();
            return "";
        }
        catch (Exception ex)
        {
            return ex.getMessage().toUpperCase()+"\n";
        }
    }

    public void stop()
    {
        olsenBasic.runStop();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        basicThread.interrupt();
    }

    /**
     * Start BASIC task
     *
     * @param synchronous if true the caller is blocked
     */
    public void start (boolean synchronous)
    {
        if (running)
        {
            System.out.println("already running ...");
            return;
        }
        basicThread = new Thread (this);
        basicThread.start();
        if (!synchronous)
        {
            return;
        }
        try
        {
            basicThread.join();
        }
        catch (Exception ignored)
        {
            //e.printStackTrace();
        }
    }

    public Basic getOlsenBasic ()
    {
        return olsenBasic;
    }

    @Override
    public void run ()
    {
        running = true;
        try
        {
            olsenBasic.run();
        }
        catch (Exception ignored)
        {
            //ex.printStackTrace();
        }
        finally
        {
            running = false;
        }
    }
}
