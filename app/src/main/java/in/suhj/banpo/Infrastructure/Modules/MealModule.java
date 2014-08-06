package in.suhj.banpo.Infrastructure.Modules;

import android.content.Context;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import static com.wagnerandade.coollection.Coollection.*;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.suhj.banpo.Abstract.IHttpClient;
import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.App;
import in.suhj.banpo.Concrete.IonHttpClient;
import in.suhj.banpo.Infrastructure.Data.RegexManager;
import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.Models.Meal;

/**
 * Created by SuhJin on 2014-06-08.
 */
// TODO: 급식 List를 반환 후에도 계속해서 유지하도록 하기 (?)
public class MealModule
{
    private IHttpClient client;
    private Context context;
    private Gson gson;

    public MealModule()
    {
        this.client = new IonHttpClient();
        this.context = App.getContext();
        this.gson = Converters.registerDateTime(new GsonBuilder()).create();
    }

    // date의 중식, 저녁 정보를 반환
    public ArrayList<Meal> GetMealOfDay(DateTime date)
    {
        date = date.withTime(0, 0, 0, 0); // 비교를 위해 시간 삭제

        ArrayList<Meal> monthMeals = GetMealOfMonth(date);
        ArrayList<Meal> todayMeal = new ArrayList<>(from(monthMeals).where("GetDate", eq(date)).all());

        return todayMeal;
    }

    public ArrayList<Meal> GetMealOfWeek(DateTime date)
    {
        date = date.withTime(0, 0, 0, 0); // 비교를 위해 시간 삭제
        
        ArrayList<Meal> allMeals = GetMealOfMonth(date); // 요청한 1주를 포함하는 달의 급식 정보 (최대 2달까지)

        // 한 주가 두 달에 걸쳐 있으면 조금 골치가 아파진다
        DateTime firstDayOfWeek = date.withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime lastDayOfWeek = date.withDayOfWeek(DateTimeConstants.FRIDAY);

        // 월요일이 이전 달에 위치할 경우
        if (firstDayOfWeek.getMonthOfYear() < date.getMonthOfYear())
        {
            allMeals.addAll(GetMealOfMonth(firstDayOfWeek));
        }

        // 금요일이 다음 달에 위치할 경우
        if (lastDayOfWeek.getMonthOfYear() > date.getMonthOfYear())
        {
            allMeals.addAll(GetMealOfMonth(lastDayOfWeek));
        }

        // Coollection이 DateTime간의 연산은 제공하지 않는 것 같다
        List<Meal> weekMeals = from(allMeals)
                .where("GetDateId", greaterThan(DateHelper.GetDateId(firstDayOfWeek) - 1))
                .and("GetDateId", lessThan(DateHelper.GetDateId(lastDayOfWeek) + 1))
                .all();

        return new ArrayList<Meal>(weekMeals);
    }

    public ArrayList<Meal> GetMealOfMonth(DateTime date)
    {
        if (mealDbExistsFor(date))
        {
            return parseMealOfMonthFromDb(date);
        }
        else
        {
            return downloadMealOfMonth(date);
        }
    }

    // 하루의 급식 정보를 다운로드
    private ArrayList<Meal> downloadMealOfMonth(DateTime date)
    {
        // 반환할 ArrayList
        ArrayList<Meal> meals = new ArrayList<>();

        String url = "http://hes.sen.go.kr/sts_sci_md00_001.do"; // 급식 정보가 있는 URL

        int year = date.getYear();
        int month = date.getMonthOfYear();

        // month 처리
        String strMonth = String.valueOf(month);
        if (month < 10)
        {
            strMonth = "0" + strMonth;
        }

        HashMap<String, String> postData = new HashMap<>();

        postData.put("schulCode", "B100000440");
        postData.put("schulCrseScCode", "4");
        postData.put("schulKndScCode", "04");
        postData.put("schYm", year + "." + strMonth);

        String rawData = "";

        try
        {
            rawData = client.post(url, postData);
        }
        // TODO: 예외 처리
        catch (Exception e) { }

        // 하루의 급식 정보를 담고 있는 컨테이너
        Matcher containerMatcher = RegexManager.getMealContainerPattern().matcher(rawData);

        // 날짜, 점심, 저녁 정보
        Pattern datePattern = RegexManager.getMealDatePattern();
        Pattern lunchPattern = RegexManager.getMealLunchPattern();
        Pattern dinnerPattern = RegexManager.getMealDinnerPattern();

        String brRegex = "<br />";
        String trashInformationRegex = "(^, |, $|[①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬.]|\\(석\\)|\\(완\\))";

        while (containerMatcher.find())
        {
            String mealData = containerMatcher.group(1);

            if (!mealData.matches(".*\\d.*")) continue;

            // 날짜 가지고 오기
            Matcher dateMatcher = datePattern.matcher(mealData);
            dateMatcher.find();
            int day = Integer.parseInt(dateMatcher.group(1));

            // 점심 파싱 및 추가
            Matcher lunchMatcher = lunchPattern.matcher(mealData);

            String lunchData = "점심이 없습니다 :'(";
            if (lunchMatcher.find())
            {
                lunchData = lunchMatcher.group(1);
            }
            lunchData = lunchData
                    .replaceAll(brRegex, ", ")
                    .replaceAll(trashInformationRegex, "");

            // 저녁 파싱 및 추가
            Matcher dinnerMatcher = dinnerPattern.matcher(mealData);

            String dinnerData = "저녁이 없습니다 :'(";
            if (dinnerMatcher.find())
            {
                dinnerData = dinnerMatcher.group(1);
            }
            dinnerData = dinnerData
                    .replaceAll(brRegex, ", ")
                    .replaceAll(trashInformationRegex, "");

            meals.add(new Meal(new DateTime(year, month, day, 0, 0, 0), lunchData, dinnerData));
        }

        // 다운로드한 급식 정보를 저장
        saveMeals(meals, date);

        return meals;
    }

    private boolean mealDbExistsFor(DateTime date)
    {
        // json 파일 존재 여부 확인
        String mealJsonName = date.getYear() + "-" + date.getMonthOfYear() + ".json";

        File file = context.getFileStreamPath(mealJsonName);

        return file.exists();
    }

    private ArrayList<Meal> parseMealOfMonthFromDb(DateTime date)
    {
        ArrayList<Meal> meals = new ArrayList<>();

        if (mealDbExistsFor(date))
        {
            String mealJsonName = date.getYear() + "-" + date.getMonthOfYear() + ".json";

            try
            {
                FileInputStream stream = context.openFileInput(mealJsonName);

                String mealJson = IOUtils.toString(stream);

                meals = gson.fromJson(mealJson, new TypeToken<ArrayList<Meal>>(){}.getType());
            } catch (Exception e) { }
        }

        return meals;
    }

    private void saveMeals(ArrayList<Meal> meals, DateTime date)
    {
        String serializedMeals = gson.toJson(meals);

        String fileName = date.getYear() + "-" + date.getMonthOfYear() + ".json";

        try
        {
            FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            stream.write(serializedMeals.getBytes());
            stream.close();
        }
        catch (Exception e) { }
    }
}
