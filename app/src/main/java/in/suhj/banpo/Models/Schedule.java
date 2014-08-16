package in.suhj.banpo.Models;

import org.joda.time.DateTime;

import java.util.ArrayList;

import in.suhj.banpo.Infrastructure.Helpers.DateHelper;

/**
 * Created by SuhJin on 2014-08-07.
 */
public class Schedule
{
    private DateTime date;
    private ArrayList<String> schedules;

    public Schedule(DateTime date, ArrayList<String> schedules)
    {
        this.date = date;
        this.schedules = schedules;
    }

    public DateTime getDate()
    {
        return date;
    }

    public void setDate(DateTime date)
    {
        this.date = date;
    }

    public ArrayList<String> getSchedules()
    {
        return schedules;
    }

    public void setSchedules(ArrayList<String> schedules)
    {
        this.schedules = schedules;
    }

    public int getDateId()
    {
        return DateHelper.GetDateId(date);
    }


    @Override
    public String toString() {
        String schedule = "스케줄 없음";

        if (schedules.size() > 0)
        {
            schedule = schedules.get(0);
        }

        return getDateId() + ": " + schedule;
    }
}
