package in.suhj.banpo.Models;

import org.joda.time.DateTime;

import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.R;

/**
 * 급식 정보를 저장하는 클래스
 */
public class Meal
{
    private DateTime date;
    private String lunchContent; // 점심 내용
    private String dinnerContent; // 저녁 내용

    public Meal(DateTime date, String lunchContent, String dinnerContent)
    {
        this.date = date;
        this.lunchContent = lunchContent;
        this.dinnerContent = dinnerContent;
    }

    public DateTime GetDate()
    {
        return date;
    }

    public void SetDate(DateTime date)
    {
        this.date = date;
    }

    public String GetLunchContent()
    {
        return lunchContent;
    }

    public void SetLunchContent(String lunchContent)
    {
        this.lunchContent = lunchContent;
    }

    public String GetDinnerContent()
    {
        return dinnerContent;
    }

    public void SetDinnerContent(String dinnerContent)
    {
        this.dinnerContent = dinnerContent;
    }

    public int GetDateId() {
        return DateHelper.GetDateId(date);
    }
}
