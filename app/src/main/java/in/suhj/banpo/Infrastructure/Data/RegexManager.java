package in.suhj.banpo.Infrastructure.Data;

import android.content.Context;

import com.koushikdutta.ion.Ion;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

import in.suhj.banpo.Abstract.IHttpClient;
import in.suhj.banpo.Abstract.IRunnable;
import in.suhj.banpo.App;
import in.suhj.banpo.Concrete.IonHttpClient;
import in.suhj.banpo.Infrastructure.Helpers.StorageManager;
import in.suhj.banpo.R;

/**
 * Created by SuhJin on 2014-08-01.
 */
public class RegexManager
{
    private static Context context;
    private static IHttpClient client;
    private static String fileName = "parse-regex.json";

    private static int version;

    private static String mealUrl;
    private static Pattern mealYearMonthPattern;
    private static Pattern mealContainerPattern;
    private static Pattern mealDatePattern;
    private static Pattern mealLunchPattern;
    private static Pattern mealDinnerPattern;
    private static Pattern mealTrashInfoPattern;

    private static String scheduleUrl;
    private static Pattern scheduleTable;
    private static Pattern scheduleWrapper;
    private static Pattern scheduleContainer;
    private static Pattern scheduleDate;
    private static Pattern scheduleContent;
    private static Pattern scheduleMonth;


    private static Pattern toolsBrPattern;

    static
    {
        context = App.getContext();
        client = new IonHttpClient();
        Load();
    }

    private static void Load()
    {
        if (StorageManager.CopyResourceToStorage(R.raw.json_regex, fileName, false))
        {
            try
            {
                FileInputStream stream = context.openFileInput(fileName);
                String regexJsonString = IOUtils.toString(stream);

                JSONObject regexJson = new JSONObject(regexJsonString);

                version = regexJson.getInt("version");

                JSONObject mealJson = regexJson.getJSONObject("meal");
                mealUrl = mealJson.getString("url");
                mealYearMonthPattern = Pattern.compile(mealJson.getString("yearMonth"));
                mealContainerPattern = Pattern.compile(mealJson.getString("container"));
                mealDatePattern = Pattern.compile(mealJson.getString("date"));
                mealLunchPattern = Pattern.compile(mealJson.getString("lunch"));
                mealDinnerPattern = Pattern.compile(mealJson.getString("dinner"));
                mealTrashInfoPattern = Pattern.compile(mealJson.getString("trashInfo"));

                JSONObject scheduleJson = regexJson.getJSONObject("schedule");
                scheduleUrl = scheduleJson.getString("url");
                scheduleTable = Pattern.compile(scheduleJson.getString("table"), Pattern.DOTALL);
                scheduleWrapper = Pattern.compile(scheduleJson.getString("wrapper"), Pattern.DOTALL);
                scheduleContainer = Pattern.compile(scheduleJson.getString("container"), Pattern.DOTALL);
                scheduleDate = Pattern.compile(scheduleJson.getString("date"), Pattern.DOTALL);
                scheduleContent = Pattern.compile(scheduleJson.getString("content"), Pattern.DOTALL);
                scheduleMonth = Pattern.compile(scheduleJson.getString("month"));

                JSONObject toolsJson = regexJson.getJSONObject("tools");
                toolsBrPattern = Pattern.compile(toolsJson.getString("br"));
            } catch (Exception e) { }
        }
    }

    private static void Update(final IRunnable<String> callback)
    {
        String response = "";

        try
        {
             response = client.get("http://raon0211.github.io/Banpo/data/regex.json");
        }
        // TODO: 예외 처리
        catch (Exception e) { }

        String message = "패턴 업데이트에 실패했습니다. 잠시 후 다시 시도해 주세요. 오류 코드: 없음";

        try
        {
            JSONObject regexJson = new JSONObject(response);
            int newVersion = regexJson.getInt("version");

            if (version >= newVersion)
            {
                message = "현재 최신 패턴을 사용하고 있습니다.";
            }
            else
            {
                FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                stream.write(response.getBytes());
                stream.close();

                Load();

                message = "패턴 업데이트에 성공했습니다.";
            }
        }
        catch (Exception e)
        {
            message = "패턴 업데이트에 실패했습니다. 잠시 후 다시 시도해 주세요. 오류 메시지: " + e.getMessage();
        }
        finally
        {
            callback.run(message);
        }
    }

    // 기본 버전으로 회귀
    public static void Reset()
    {
        StorageManager.CopyResourceToStorage(R.raw.json_regex, fileName, true);
        Load();
    }

    public static String getMealUrl()
    {
        return mealUrl;
    }

    public static Pattern getMealYearMonthPattern()
    {
        return mealYearMonthPattern;
    }

    public static Pattern getMealContainerPattern()
    {
        return mealContainerPattern;
    }

    public static Pattern getMealDatePattern()
    {
        return mealDatePattern;
    }

    public static Pattern getMealLunchPattern()
    {
        return mealLunchPattern;
    }

    public static Pattern getMealDinnerPattern()
    {
        return mealDinnerPattern;
    }

    public static Pattern getMealTrashInfoPattern()
    {
        return mealTrashInfoPattern;
    }

    public static String getScheduleUrl()
    {
        return scheduleUrl;
    }

    public static Pattern getScheduleTable()
    {
        return scheduleTable;
    }

    public static Pattern getScheduleWrapper()
    {
        return scheduleWrapper;
    }

    public static Pattern getScheduleContainer()
    {
        return scheduleContainer;
    }

    public static Pattern getScheduleDate()
    {
        return scheduleDate;
    }

    public static Pattern getScheduleContent()
    {
        return scheduleContent;
    }

    public static Pattern getScheduleMonth()
    {
        return scheduleMonth;
    }

    public static Pattern getToolsBrPattern()
    {
        return toolsBrPattern;
    }
}
