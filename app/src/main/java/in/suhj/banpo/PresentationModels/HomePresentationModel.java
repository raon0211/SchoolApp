package in.suhj.banpo.PresentationModels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.joda.time.DateTime;
import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.ItemPresentationModel;

import java.util.ArrayList;
import java.util.List;

import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.Activities.MealActivity;
import in.suhj.banpo.Activities.ScheduleActivity;
import in.suhj.banpo.App;
import in.suhj.banpo.Infrastructure.Data.RegexManager;
import in.suhj.banpo.Infrastructure.Modules.NoticeModule;
import in.suhj.banpo.Infrastructure.Modules.ScheduleModule;
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Infrastructure.Modules.MealModule;
import in.suhj.banpo.Models.Notice;
import in.suhj.banpo.Models.Schedule;

/**
 * Created by SuhJin on 2014-06-02.
 */

public class HomePresentationModel extends AbstractPresentationModel
{
    private ITaskCompleted<Boolean> listener;

    private Context context;
    private MealModule mealModule;
    private ScheduleModule scheduleModule;
    private NoticeModule noticeModule;

    private List<Meal> todayMeals;
    private List<Schedule> weekSchedules;
    private Notice notice;

    public HomePresentationModel(ITaskCompleted<Boolean> listener)
    {
        this.listener = listener;

        this.context = App.getContext();
        this.mealModule = new MealModule();
        this.scheduleModule = new ScheduleModule();
        this.noticeModule = new NoticeModule();

        this.todayMeals = new ArrayList<Meal>();
        this.weekSchedules = new ArrayList<Schedule>();

        notice = noticeModule.GetNotice();

        // 오늘 급식 정보 다운로드
        todayMeals = mealModule.GetMealOfDay(new DateTime());
        presentationModelChangeSupport.firePropertyChange("todayMeals");

        weekSchedules = scheduleModule.GetScheduleOfFollowingWeek(new DateTime());
        presentationModelChangeSupport.firePropertyChange("weekSchedules");
        presentationModelChangeSupport.firePropertyChange("isWeekScheduleEmpty");

    }

    @ItemPresentationModel(MealItemPresentationModel.class)
    public List<Meal> getTodayMeals()
    {
        return todayMeals;
    }

    @ItemPresentationModel(ScheduleItemPresentationModel.class)
    public List<Schedule> getWeekSchedules()
    {
        return weekSchedules;
    }

    public boolean getIsWeekScheduleEmpty()
    {
        return weekSchedules.size() == 0;
    }

    public boolean getNoticeShow()
    {
        return notice.getShow();
    }

    public String getNoticeTitle()
    {
        return notice.getTitle();
    }

    public String getNoticeButtonContent()
    {
        return notice.getButtonContent();
    }

    public void NavigateNotice()
    {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(notice.getUrl())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void NavigateMeal()
    {
        context.startActivity(new Intent(context, MealActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void NavigateSchoolInfo()
    {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RegexManager.getInformationUrl())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void NavigateSchedule()
    {
        context.startActivity(new Intent(context, ScheduleActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
