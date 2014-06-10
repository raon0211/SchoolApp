package in.suhj.banpo.Models;

import org.joda.time.DateTime;

import in.suhj.banpo.R;

/**
 * 급식 정보를 저장하는 클래스
 */
public class Meal
{
    public enum MealType { Lunch, Dinner }

    private DateTime date;
    private MealType type; // 점심, 저녁
    private String content; // 급식 내용

    public Meal(DateTime date, MealType type, String content)
    {
        this.date = date;
        this.type = type;
        this.content = content;
    }

    public DateTime GetDate()
    {
        return date;
    }

    public void SetDate(DateTime date)
    {
        this.date = date;
    }

    public MealType GetType()
    {
        return type;
    }

    public void SetType(MealType type)
    {
        this.type = type;
    }

    public String GetContent()
    {
        return content;
    }

    public void SetContent(String content)
    {
        this.content = content;
    }

    public int GetIconId()
    {
        int id = 0;

        switch (type)
        {
            case Lunch:
                id = R.drawable.ic_sun;
                break;
            case Dinner:
                id = R.drawable.ic_moon;
                break;
        }

        return id;
    }
}
