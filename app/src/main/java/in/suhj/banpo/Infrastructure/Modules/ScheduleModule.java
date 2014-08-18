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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import in.suhj.banpo.Abstract.IHttpClient;
import in.suhj.banpo.App;
import in.suhj.banpo.Concrete.IonHttpClient;
import in.suhj.banpo.Infrastructure.Data.RegexManager;
import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.Models.Schedule;

import static com.wagnerandade.coollection.Coollection.from;
import static com.wagnerandade.coollection.Coollection.greaterThan;
import static com.wagnerandade.coollection.Coollection.lessThan;

/**
 * Created by SuhJin on 2014-08-07.
 */
public class ScheduleModule
{
    private IHttpClient client;
    private Context context;
    private Gson gson;

    public ScheduleModule()
    {
        this.client = new IonHttpClient();
        this.context = App.getContext();
        this.gson = Converters.registerDateTime(new GsonBuilder()).create();
    }

    public ArrayList<Schedule> GetScheduleOfWeek(DateTime date)
    {
        DateTime firstDayOfWeek = date.withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime lastDayOfWeek = date.withDayOfWeek(DateTimeConstants.SUNDAY);

        ArrayList<Schedule> yearSchedule = GetScheduleOfYear(date);

        List<Schedule> weekSchedule = from(yearSchedule)
                .where("getDateId", greaterThan(DateHelper.GetDateId(firstDayOfWeek) - 1))
                .and("getDateId", lessThan(DateHelper.GetDateId(lastDayOfWeek) + 1))
                .all();

        return new ArrayList<Schedule>(weekSchedule);
    }

    public ArrayList<Schedule> GetScheduleOfFollowingWeek(DateTime date)
    {
        DateTime nextWeekDate = date.plusWeeks(1);

        ArrayList<Schedule> yearSchedule = GetScheduleOfYear(date);

        List<Schedule> weekSchedule = from(yearSchedule)
                .where("getDateId", greaterThan(DateHelper.GetDateId(date) - 1))
                .and("getDateId", lessThan(DateHelper.GetDateId(nextWeekDate)))
                .all();

        return new ArrayList<Schedule>(weekSchedule);
    }

    public ArrayList<Schedule> GetScheduleOfYear(DateTime date)
    {
        if (scheduleDbExistsFor(date))
        {
            return parseScheduleFromDb(date);
        }
        else
        {
            return downloadScheduleAll(date);
        }
    }

    private ArrayList<Schedule> downloadScheduleAll(DateTime time)
    {
        // 반환할 스케줄 리스트
        ArrayList<Schedule> schedules = new ArrayList<>();

        String url = RegexManager.getScheduleUrl();
        String rawData = "";

        try
        {
            rawData = client.get(url);
        // TODO: 예외 처리
        } catch (Exception e) { }

        schedules = gson.fromJson(rawData, new TypeToken<ArrayList<Schedule>>(){}.getType());

        saveSchedules(schedules, time);

        return schedules;
    }

    private boolean scheduleDbExistsFor(DateTime date)
    {
        // json 파일 존재 여부 확인
        String scheduleJsonName = getScheduleDbFilename(date);

        File file = context.getFileStreamPath(scheduleJsonName);

        return file.exists();
    }

    private ArrayList<Schedule> parseScheduleFromDb(DateTime date)
    {
        ArrayList<Schedule> schedules = new ArrayList<>();

        if (scheduleDbExistsFor(date))
        {
            String scheduleJsonName = getScheduleDbFilename(date);

            try
            {
                FileInputStream stream = context.openFileInput(scheduleJsonName);

                String scheduleJson = IOUtils.toString(stream);

                schedules = gson.fromJson(scheduleJson, new TypeToken<ArrayList<Schedule>>(){}.getType());
            } catch (Exception e) { }
        }

        return schedules;
    }

    private void saveSchedules(ArrayList<Schedule> schedules, DateTime date)
    {
        String serializedSchedules = gson.toJson(schedules);

        String fileName = getScheduleDbFilename(date);

        try
        {
            FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            stream.write(serializedSchedules.getBytes());
            stream.close();
        }
        catch (Exception e) { }
    }

    private String getScheduleDbFilename(DateTime date)
    {
        return "schedule-" + date.getYear() + ".json";
    }

    public void removeScheduleData()
    {
        List<String> fileList = Arrays.asList(context.fileList());

        for (String file : fileList)
        {
            if (file.startsWith("schedule"))
            {
                context.deleteFile(file);
            }
        }
    }
}
