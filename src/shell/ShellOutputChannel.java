package shell;

import com.sixtyfour.plugins.impl.ConsoleOutputChannel;
import server.WebSocketHandler;


/**
 * Created by Administrator on 1/4/2017.
 */
class ShellOutputChannel extends ConsoleOutputChannel
{
    private WebSocketHandler _r;

    public ShellOutputChannel (WebSocketHandler r)
    {
        _r = r;
    }

    @Override
    public void print (int id, String txt)
    {
        _r.write(txt);
    }

    @Override
    public void println (int id, String txt)
    {
        print (id, txt+'\n');
    }

    @Override
    public int getCursor ()
    {
        return 0;
    }

}
