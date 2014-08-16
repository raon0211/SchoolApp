package in.suhj.banpo.PresentationModels;

import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;
import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.ItemPresentationModel;

import java.util.ArrayList;
import java.util.List;

import in.suhj.banpo.Activities.MealActivity;
import in.suhj.banpo.Activities.ScheduleActivity;
import in.suhj.banpo.App;
import in.suhj.banpo.Infrastructure.Modules.ScheduleModule;
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Infrastructure.Modules.MealModule;
import in.suhj.banpo.Models.Schedule;

/**
 * Created by SuhJin on 2014-06-02.
 */

public class HomePresentationModel extends AbstractPresentationModel
{
    private Context context;
    private MealModule mealModule;
    private ScheduleModule scheduleModule;

    private List<Meal> todayMeals;
    private List<Schedule> weekSchedules;

    public HomePresentationModel()
    {
        this.context = App.getContext();
        this.mealModule = new MealModule();
        this.scheduleModule = new ScheduleModule();

        this.todayMeals = new ArrayList<Meal>();
        this.weekSchedules = new ArrayList<Schedule>();

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

    public void NavigateMeal()
    {
        context.startActivity(new Intent(context, MealActivity.class));
    }

    public void NavigateSchedule()
    {
        context.startActivity(new Intent(context, ScheduleActivity.class));
    }
}
