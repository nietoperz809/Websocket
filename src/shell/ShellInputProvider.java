package shell;

import com.sixtyfour.plugins.InputProvider;
import server.WebSocketHandler;

/**
 * Created by Administrator on 1/4/2017.
 */
class ShellInputProvider implements InputProvider
{
    private WebSocketHandler _r;

    public ShellInputProvider (WebSocketHandler r)
    {
        _r = r;
    }

    @Override
    public Character readKey ()
    {
        return _r.read().charAt(0);
    }


    @Override
    public String readString ()
    {
        return _r.read();
    }
}
