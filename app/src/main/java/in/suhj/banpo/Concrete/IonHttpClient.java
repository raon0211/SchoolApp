package in.suhj.banpo.Concrete;

import android.content.Context;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import in.suhj.banpo.Abstract.IHttpClient;
import in.suhj.banpo.App;

/**
 * Created by SuhJin on 2014-08-05.
 */
public class IonHttpClient implements IHttpClient
{
    private Context context;

    public IonHttpClient()
    {
        this.context = App.getContext();
    }

    public String get(String url) throws Exception
    {
        Future<String> downloadTask = Ion.with(context).load(url).setHeader("Accept-Encoding", "identity").asString();

        String result = "";

        try
        {
            result = downloadTask.get();
        }
        catch (Exception e) { throw new Exception(e.getMessage()); }

        return result;
    }

    public String post(String url, HashMap<String, String> postData) throws Exception
    {
        Builders.Any.B ion = Ion.with(context).load(url);

        for (Map.Entry<String, String> entry : postData.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();

            ion.setBodyParameter(key, value);
        }

        Future<String> downloadTask = ion.asString();

        String result = "";

        try
        {
            result = downloadTask.get();
        }
        catch (Exception e) { throw new Exception(); }

        return result;
    }
}
