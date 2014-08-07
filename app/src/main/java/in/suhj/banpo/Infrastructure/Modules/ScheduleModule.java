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
        DateTime lastDayOfWeek = date.withDayOfWeek(DateTimeConstants.FRIDAY);

        List<Schedule> weekSchedule = from(GetScheduleOfYear(date))
                .where("getDateId", greaterThan(DateHelper.GetDateId(firstDayOfWeek) - 1))
                .and("getDateId", lessThan(DateHelper.GetDateId(lastDayOfWeek) + 1))
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

        Matcher tableMatcher = RegexManager.getScheduleTable().matcher(rawData);

        String tableData = "";
        if (tableMatcher.find())
        {
            tableData = tableMatcher.group(1);
        }

        // 반포고등학교 학사일정 페이지 구조가 매우 특수함
        // 날짜 tr - 일정 tr - 날짜 tr.. 이런 식으로 되어 있음
        // 날짜 tr와 일정 tr로 분류가 필요
        ArrayList<String> dateTrs = new ArrayList<>();
        ArrayList<String> scheduleTrs = new ArrayList<>();

        Matcher trMatcher = RegexManager.getScheduleWrapper().matcher(tableData);

        while (trMatcher.find())
        {
            String trData = trMatcher.group(1);

            Matcher dateMatcher = RegexManager.getScheduleDate().matcher(trData);
            Matcher contentMatcher = RegexManager.getScheduleContent().matcher(trData);

            if (dateMatcher.find())
            {
                dateTrs.add(trData);
            }
            else if (contentMatcher.find())
            {
                // Td가 5개인지 확인
                Matcher containerMatcher = RegexManager.getScheduleContainer().matcher(trData);

                int count = 0;
                while (containerMatcher.find())
                {
                    count++;
                }

                if (count != 5) continue;

                scheduleTrs.add(trData);
            }
        }

        int index = 0;
        int year = new DateTime().getYear();
        int month = 3;

        for (String dateData : dateTrs)
        {
            // TODO: 이렇게 해도 될지..
            String scheduleData = scheduleTrs.get(index++);

            Matcher monthMatcher = RegexManager.getScheduleMonth().matcher(dateData);
            Matcher dateMatcher = RegexManager.getScheduleDate().matcher(dateData);
            Matcher scheduleContainerMatcher = RegexManager.getScheduleContainer().matcher(scheduleData);

            if (monthMatcher.find())
            {
                month = Integer.parseInt(monthMatcher.group(1));
            }

            // 일단 tr로 묶여 있는 일정을 td별로 끊어 주자
            ArrayList<String> daySchedules = new ArrayList<>();

            while (scheduleContainerMatcher.find())
            {
                daySchedules.add(scheduleContainerMatcher.group(0));
            }

            int dayIndex = 0;

            while (dateMatcher.find())
            {
                // 일정 날짜
                int day = Integer.parseInt(dateMatcher.group(2));

                // 각 스케줄이 저장되는 곳
                ArrayList<String> parsedSchedules = new ArrayList<>();

                String dayScheduleData = daySchedules.get(dayIndex++);

                Matcher contentMatcher = RegexManager.getScheduleContent().matcher(dayScheduleData);

                while (contentMatcher.find())
                {
                    String rawSchedule = contentMatcher.group(0);
                    String trimmedSchedule = android.text.Html.fromHtml(rawSchedule).toString().replaceAll("[\r\n\\s]", "");

                    if (!StringUtils.isBlank(trimmedSchedule))
                    {
                        parsedSchedules.add(trimmedSchedule);
                    }
                }

                if (parsedSchedules.size() == 0) continue;

                DateTime date = new DateTime(year, month, day, 0, 0, 0);

                // 25~30일의 경우 학교 홈페이지 정보상 월이 +1이 되는 경우가 있다
                if (day > 25 && day > schedules.get(schedules.size() - 1).getDate().getDayOfMonth())
                {
                    date.minusMonths(1);
                }

                schedules.add(new Schedule(date, parsedSchedules));
            }
        }

        saveSchedules(schedules, time);

        return schedules;
    }

    private boolean scheduleDbExistsFor(DateTime date)
    {
        // json 파일 존재 여부 확인
        String scheduleJsonName = date.getYear() + ".json";

        File file = context.getFileStreamPath(scheduleJsonName);

        return file.exists();
    }

    private ArrayList<Schedule> parseScheduleFromDb(DateTime date)
    {
        ArrayList<Schedule> schedules = new ArrayList<>();

        if (scheduleDbExistsFor(date))
        {
            String scheduleJsonName = date.getYear() + "-" + date.getMonthOfYear() + ".json";

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

        String fileName = date.getYear() + ".json";

        try
        {
            FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            stream.write(serializedSchedules.getBytes());
            stream.close();
        }
        catch (Exception e) { }
    }
}
