package in.suhj.banpo.Infrastructure.Modules;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static com.wagnerandade.coollection.Coollection.*;

import org.apache.http.Header;
import org.joda.time.DateTime;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.Models.Meal;

/**
 * Created by SuhJin on 2014-06-08.
 */
// TODO: 구조 수정. Date를 계속해서 넘기는 구조를 수정할 방안을 생각해 보자.
public class MealModule
{
    private ITaskCompleted<List<Meal>> listener;
    private static AsyncHttpClient client;

    public MealModule(ITaskCompleted<List<Meal>> listener)
    {
        this.listener = listener;
        this.client = new AsyncHttpClient();
    }

    // date의 중식, 저녁 정보를 반환
    public void GetMealOfDay(DateTime date)
    {
        date = date.withTime(0, 0, 0, 0);

        // TODO: 저장된 XML 파일이 있는지 확인. 없으면 다운로드
        downloadMeal(date);
    }

    // 실질적으로 급식 정보를 다운로드하는 메서드
    private void downloadMeal(final DateTime date)
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
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody, Charset.forName("UTF-8"));

                parseMeals(response, date);
            }
        });
    }

    private void parseMeals(String data, DateTime date)
    {
        ArrayList<Meal> meals = new ArrayList<>();

        final int year = date.getYear();
        final int month = date.getMonthOfYear();

        Pattern containerPattern = Pattern.compile("<td>((.|\n)*?)</td>");
        Matcher containerMatcher = containerPattern.matcher(data);

        Pattern datePattern = Pattern.compile("(\\d+)");
        Pattern lunchPattern = Pattern.compile("(?<=\\[중식\\])((.|\n)*?)(?=\\[)");
        Pattern dinnerPattern = Pattern.compile("(?<=\\[석식\\])((.|\n)+)");

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

        saveMeals(meals);
        returnMeals(meals, date);
    }

    private void saveMeals(ArrayList<Meal> meals)
    {
        // TODO: 받아온 급식 정보를 저장
    }

    private void returnMeals(ArrayList<Meal> meals, DateTime date)
    {
        List<Meal> returnMeals = from(meals).where("GetDate", eq(date)).all();

        listener.OnTaskCompleted(returnMeals);
    }
}
