package in.suhj.banpo.Infrastructure.Modules;

import android.content.Context;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.suhj.banpo.Abstract.IHttpClient;
import in.suhj.banpo.App;
import in.suhj.banpo.Concrete.IonHttpClient;
import in.suhj.banpo.Infrastructure.Data.RegexManager;
import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.Models.Notice;
import in.suhj.banpo.Models.Schedule;

import static com.wagnerandade.coollection.Coollection.from;
import static com.wagnerandade.coollection.Coollection.greaterThan;
import static com.wagnerandade.coollection.Coollection.lessThan;

/**
 * Created by SuhJin on 2014-08-17.
 */
public class NoticeModule
{
    private IHttpClient client;
    private Context context;
    private Gson gson;

    public NoticeModule()
    {
        this.client = new IonHttpClient();
        this.context = App.getContext();
        this.gson = Converters.registerDateTime(new GsonBuilder()).create();
    }

    public Notice GetNotice()
    {
        String url = RegexManager.getNoticeUrl();
        String rawData = "";

        try
        {
            rawData = client.get(url);
        }
        catch (Exception e)
        {
            return new Notice("", "", "", false);
        }

        Notice notice = gson.fromJson(rawData, Notice.class);
        return notice;
    }
}
