package in.suhj.banpo.PresentationModels;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.ItemPresentationModel;

import java.util.ArrayList;
import java.util.List;

import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.App;
import in.suhj.banpo.Infrastructure.Modules.ScheduleModule;
import in.suhj.banpo.Models.Schedule;

/**
 * Created by SuhJin on 2014-08-10.
 */

public class SchedulePresentationModel extends AbstractPresentationModel
{
    private ITaskCompleted<Boolean> listener;
    private Context context;
    private ScheduleModule scheduleModule;
    private DateTime today;
    private DateTime displayingDay;

    private ArrayList<Schedule> schedules;

    public SchedulePresentationModel(ITaskCompleted<Boolean> listener)
    {
        this.listener = listener;
        this.context = App.getContext();
        this.scheduleModule = new ScheduleModule();
        this.today = new DateTime();
        this.displayingDay = new DateTime();

        this.schedules = new ArrayList<Schedule>();

        // 일주일 학사일정 정보 다운로드
        schedules = scheduleModule.GetScheduleOfWeek(today);
        presentationModelChangeSupport.firePropertyChange("schedules");
    }

    @ItemPresentationModel(ScheduleItemPresentationModel.class)
    public List<Schedule> getSchedules()
    {
        return schedules;
    }

    public String getTimeString()
    {
        int weeksBetween = Weeks.weeksBetween(today.toLocalDate(), displayingDay.toLocalDate()).getWeeks();

        if (weeksBetween > 0)
        {
            return weeksBetween + "주 후";
        }
        else if (weeksBetween == 0)
        {
            return "이번 주";
        }
        else
        {
            return -weeksBetween + "주 전";
        }
    }

    public void previousWeek()
    {
        displayingDay = displayingDay.minusWeeks(1);
        schedules = scheduleModule.GetScheduleOfWeek(displayingDay);

        presentationModelChangeSupport.firePropertyChange("schedules");
        presentationModelChangeSupport.firePropertyChange("timeString");

        notifyListener(true);
    }

    public void nextWeek()
    {
        displayingDay = displayingDay.plusWeeks(1);
        schedules = scheduleModule.GetScheduleOfWeek(displayingDay);

        presentationModelChangeSupport.firePropertyChange("schedules");
        presentationModelChangeSupport.firePropertyChange("timeString");

        notifyListener(true);
    }

    private void notifyListener(boolean success)
    {
        listener.OnTaskCompleted(success);
    }
}
