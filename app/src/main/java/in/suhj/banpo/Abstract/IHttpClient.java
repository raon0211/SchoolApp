package in.suhj.banpo.Abstract;

import java.util.HashMap;

/**
 * Created by SuhJin on 2014-08-05.
 */
public interface IHttpClient
{
    public String get(String url) throws Exception;
    public String post(String url, HashMap<String, String> postData) throws Exception;
}
