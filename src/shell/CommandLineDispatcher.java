package shell;

import server.WebSocketHandler;

import java.io.File;

/**
 * Created by Administrator on 1/18/2017.
 */
class CommandLineDispatcher
{
    private final WebSocketHandler _r;
    final ProgramStore store = new ProgramStore();
    BasicRunner basicRunner;

    private int speed = 900;

    public CommandLineDispatcher (WebSocketHandler r)
    {
        _r = r;
        new Thread(() ->
        {
            while(!Thread.interrupted())
            {
                try
                {
                    handleInput(_r.read());
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
                _r.write(formatted);
            }
        }
    }

    private void run (boolean sync)
    {
        basicRunner = new BasicRunner(store.toArray(), speed, _r);
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
            _r.write(ProgramStore.OK);
        }
        catch (Exception ex)
        {
            _r.write(ProgramStore.ERROR);
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
                    _r.write(store.list(i1,i1));
                }
                else // negative
                {
                    _r.write(store.list(0,-i1));
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
                _r.write(store.list(i1, i2));
            }
        }
        else  // no args
        {
            _r.write(store.toString());
        }
        _r.write(ProgramStore.OK);
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
        if (split[0].toLowerCase().equals("list"))
        {
            list (split);
        }
        else if (s.equals("stop"))
        {
            if (basicRunner != null)
                basicRunner.getOlsenBasic().runStop();
        }
        else if (s.equals("new"))
        {
            store.clear();
            _r.write(ProgramStore.OK);
        }
        else if (s.equals ("prettify"))
        {
            new Prettifier(store).doPrettify();
            _r.write(ProgramStore.OK);
        }
        else if (split[0].toLowerCase().equals("renumber"))
        {
            renumber (split);
        }
//        else if (s.equals("cls"))
//        {
//            //m_screen.matrix.clearScreen();
//        }
        else if (s.equals("run"))
        {
            run(true);
        }
        else if (s.equals("dir"))
        {
            dir();
            _r.write("\n"+ProgramStore.OK);
        }
        else if (split[0].toLowerCase().equals("speed"))
        {
            try
            {
                speed = Integer.parseInt(split[1]);
                _r.write("\n"+ProgramStore.OK);
            }
            catch (NumberFormatException ex)
            {
                _r.write("\n"+ProgramStore.ERROR);
            }
        }
        else if (split[0].toLowerCase().equals("save"))
        {
            String msg = store.save(split[1]);
            _r.write(msg);
        }
        else if (split[0].toLowerCase().equals("load"))
        {
            String msg = store.load(split[1]);
            _r.write(msg);
        }
        else
        {
            try
            {
                store.insert(s);
            }
            catch (Exception unused)
            {
                _r.write(BasicRunner.runSingleLine(s, _r));
            }
        }
    }
}
