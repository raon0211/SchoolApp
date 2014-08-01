package in.suhj.banpo.Infrastructure.Modules;

import android.content.Context;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static com.wagnerandade.coollection.Coollection.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.joda.time.DateTime;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.suhj.banpo.Abstract.IRunnable;
import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.App;
import in.suhj.banpo.Infrastructure.Data.RegexManager;
import in.suhj.banpo.Models.Meal;

/**
 * Created by SuhJin on 2014-06-08.
 */
public class MealModule
{
    private ITaskCompleted<List<Meal>> listener;
    private Context context;
    private AsyncHttpClient client;
    private Gson gson;

    public MealModule(ITaskCompleted<List<Meal>> listener)
    {
        this.context = App.getContext();
        this.listener = listener;
        this.client = new AsyncHttpClient();
        this.gson = Converters.registerDateTime(new GsonBuilder()).create();
    }

    // date의 중식, 저녁 정보를 반환
    public void GetMealOfDay(DateTime mealDate)
    {
        // date의 시간 정보 삭제: 추후 비교에 사용하기 위함 (returnMeal에서 date 비교)
        final DateTime date = mealDate.withTime(0, 0, 0, 0);

        // json 파일 존재 여부 확인
        String mealJsonName = date.getYear() + "-" + date.getMonthOfYear() + ".json";

        File file = context.getFileStreamPath(mealJsonName);
        if (file.exists())
        {
            try
            {
                FileInputStream stream = context.openFileInput(mealJsonName);

                String mealJson = IOUtils.toString(stream);

                ArrayList<Meal> meals = gson.fromJson(mealJson, new TypeToken<ArrayList<Meal>>(){}.getType());
                returnMeals(meals, date);
            } catch (Exception e) { }
        }
        else
        {
            downloadMeal(mealDate, new IRunnable<String>()
            {
                public void run(String param)
                {
                    String response = param;

                    ArrayList<Meal> meals = parseMeals(response);
                    saveMeals(meals, date);
                    returnMeals(meals, date);
                }
            });
        }
    }

    // 실질적으로 급식 정보를 다운로드하는 메서드
    // 재사용을 위해 IRunnable<String>을 받음 (callback)
    private void downloadMeal(DateTime date, final IRunnable<String> callback)
    {
        String url = "http://hes.sen.go.kr/sts_sci_md00_001.do"; // 급식 정보가 있는 URL

        int year = date.getYear();
        int month = date.getMonthOfYear();

        // month 처리
        String strMonth = String.valueOf(month);
        if (month < 10)
        {
            strMonth = "0" + strMonth;
        }

        RequestParams params = new RequestParams(); // POST 요청 데이터
        params.put("schulCode", "B100000440");
        params.put("schulCrseScCode", "4");
        params.put("schulKndScCode", "04");
        params.put("schYm", year + "." + strMonth);

        client.post(url, params, new AsyncHttpResponseHandler()
        {
            // TODO: OnFailure 시에 오류 출력
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody, Charset.forName("UTF-8"));

                callback.run(response);
            }
        });
    }

    private ArrayList<Meal> parseMeals(String data)
    {
        ArrayList<Meal> meals = new ArrayList<>();

        // 연도 및 월 파싱
        Matcher yearMonthMatcher = RegexManager.getMealYearMonthPattern().matcher(data);
        yearMonthMatcher.find();

        String rawYearMonth = yearMonthMatcher.group(1);

        final int year = Integer.parseInt(rawYearMonth.split("\\.")[0]);
        final int month = Integer.parseInt(rawYearMonth.split("\\.")[1]);

        // 하루의 급식 정보를 담고 있는 컨테이너
        Matcher containerMatcher = RegexManager.getMealContainerPattern().matcher(data);

        // 날짜, 점심, 저녁 정보
        Pattern datePattern = RegexManager.getMealDatePattern();
        Pattern lunchPattern = RegexManager.getMealLunchPattern();
        Pattern dinnerPattern = RegexManager.getMealDinnerPattern();

        String brRegex = "<br />";
        String trashInformationRegex = "(^, |, $|[①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬.]|\\(석\\)|\\(완\\))";

        while (containerMatcher.find())
        {
            String mealData = containerMatcher.group(1);

            if (!mealData.matches("[0-9](.*)")) continue;

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

            meals.add(new Meal(new DateTime(year, month, day, 0, 0, 0), Meal.MealType.Lunch, lunchData));

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

            meals.add(new Meal(new DateTime(year, month, day, 0, 0, 0), Meal.MealType.Dinner, dinnerData));
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

    private void returnMeals(ArrayList<Meal> meals, DateTime date)
    {
        List<Meal> returnMeals = from(meals).where("GetDate", eq(date)).all();

        listener.OnTaskCompleted(returnMeals);
    }
}
