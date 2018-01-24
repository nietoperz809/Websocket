package shell;

import server.WebSocketHandler;

import java.io.File;

/**
 * Created by Administrator on 1/18/2017.
 */
class CommandLineDispatcher
{
    private final WebSocketHandler socketHandler;
    final ProgramStore store = new ProgramStore();
    BasicRunner basicRunner;

    private int speed = 900;

    public CommandLineDispatcher (WebSocketHandler r)
    {
        socketHandler = r;
        new Thread(() ->
        {
            while(!Thread.interrupted())
            {
                try
                {
                    handleInput(socketHandler.read());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void dir ()
    {
        File[] filesInFolder = new File(".").listFiles();
        for (final File fileEntry : filesInFolder)
        {
            if (fileEntry.isFile())
            {
                String formatted = String.format("\n%-15s = %d",
                        fileEntry.getName(), fileEntry.length() );
                socketHandler.write(formatted);
            }
        }
    }

    private void run (boolean sync)
    {
        basicRunner = new BasicRunner(store.toArray(), speed, socketHandler);
        basicRunner.start(sync);
    }

    private void renumber (String[] split)
    {
        try
        {
            Prettifier pf = new Prettifier(store);
            switch (split.length)
            {
                case 1:
                    pf.doRenumber();
                    break;
                case 2:
                    int v1 = Integer.parseInt(split[1]);
                    pf.doRenumber(v1, v1);
                    break;
                case 3:
                    int va = Integer.parseInt(split[1]);
                    int vb = Integer.parseInt(split[2]);
                    pf.doRenumber(va, vb);
                    break;
            }
            socketHandler.write(ProgramStore.OK);
        }
        catch (Exception ex)
        {
            socketHandler.write(ProgramStore.ERROR);
        }
    }

    private void sendStringArray (String[] arr)
    {
        for (String s : arr)
        {
            String formatted = String.format("\n%s",s);
            socketHandler.write(formatted);
        }
    }

    private void list (String[] split)
    {
        if (split.length == 2)
        {
            try
            {
                int i1 = Integer.parseInt(split[1]);  // single number
                if (i1>=0) // positive
                {
                    sendStringArray(store.list(i1,i1));
                }
                else // negative
                {
                    sendStringArray(store.list(0,-i1));
                }
            }
            catch (NumberFormatException ex)
            {
                String[] args = split[1].split("-");
                int i1 = Integer.parseInt(args[0]);
                int i2;
                try
                {
                    i2 = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ex2)
                {
                    i2 = Integer.MAX_VALUE;
                }
                sendStringArray(store.list(i1, i2));
            }
        }
        else  // no args
        {
            socketHandler.write(store.toString());
        }
        socketHandler.write(ProgramStore.OK);
    }

    private void say (String in)
    {
        in = in.substring(4);  // skip "say "
        socketHandler.write ("++SAY!!"+in);
    }

    private void edit (String[] in)
    {
        int num = Integer.parseInt(in[1]);
        String[] line = store.list(num, num);
        socketHandler.write("++EDIT!!"+line[0]);
    }

    /**
     * Main function. Runs in a separate thread
     * @param in
     */
    private void handleInput (String in) throws Exception
    {
        String s = in.trim();
        String[] split = s.split(" ");
        s = s.toLowerCase();
        split[0] = split[0].toLowerCase();
        if (split[0].equals("list"))
        {
            list (split);
        }
        else if (split[0].equals("edit"))
        {
            edit (split);
        }
        else if (split[0].equals("say"))
        {
            say (s);
        }
        else if (s.equals("stop"))
        {
            if (basicRunner != null)
                basicRunner.getOlsenBasic().runStop();
        }
        else if (s.equals("new"))
        {
            store.clear();
            socketHandler.write(ProgramStore.OK);
        }
        else if (s.equals ("prettify"))
        {
            new Prettifier(store).doPrettify();
            socketHandler.write(ProgramStore.OK);
        }
        else if (split[0].equals("renumber"))
        {
            renumber (split);
        }
        else if (s.equals("run"))
        {
            run(true);
        }
        else if (s.equals("dir"))
        {
            dir();
            socketHandler.write("\n"+ProgramStore.OK);
        }
        else if (split[0].equals("speed"))
        {
            try
            {
                speed = Integer.parseInt(split[1]);
                socketHandler.write("\n"+ProgramStore.OK);
            }
            catch (NumberFormatException ex)
            {
                socketHandler.write("\n"+ProgramStore.ERROR);
            }
        }
        else if (split[0].equals("save"))
        {
            String msg = store.save(split[1]);
            socketHandler.write(msg);
        }
        else if (split[0].equals("load"))
        {
            String msg = store.load(split[1]);
            socketHandler.write(msg);
        }
        else
        {
            try
            {
                store.insert(s);
            }
            catch (Exception unused)
            {
                socketHandler.write(BasicRunner.runSingleLine(s, socketHandler));
            }
        }
    }
}
