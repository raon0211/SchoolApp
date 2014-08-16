package in.suhj.banpo.PresentationModels;

import android.graphics.Color;

import org.joda.time.DateTime;
import org.robobinding.itempresentationmodel.ItemPresentationModel;
import org.robobinding.presentationmodel.PresentationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Models.Schedule;

/**
 * Created by SuhJin on 2014-08-10.
 */
@PresentationModel
public class ScheduleItemPresentationModel implements ItemPresentationModel<Schedule>
{
    private Schedule schedule;
    private DateTime date;

    public String getDate()
    {
        return date.getMonthOfYear() + ". " + date.getDayOfMonth() + " (" + DateHelper.GetDayName(date.getDayOfWeek()) + ")";
    }

    public int getDateColor()
    {
        DateTime today = new DateTime().withTime(0, 0, 0, 0);

        if (date.equals(today))
        {
            return Color.parseColor("#0d64ad");
        }

        return Color.parseColor("#888888");
    }

    @org.robobinding.presentationmodel.ItemPresentationModel(ScheduleItemContentPresentationModel.class)
    public List<String> getSchedules()
    {
        return schedule.getSchedules();
    }

    public void updateData(int index, Schedule schedule)
    {
        this.schedule = schedule;
        this.date = schedule.getDate();
    }
}
