package in.suhj.banpo;

import android.app.Application;
import android.content.Context;

/**
 * Created by SuhJin on 2014-08-01.
 */
public class App extends Application
{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}
