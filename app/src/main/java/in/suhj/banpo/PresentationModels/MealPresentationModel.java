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
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Infrastructure.Modules.MealModule;

/**
 * Created by SuhJin on 2014-08-02.
 */

public class MealPresentationModel extends AbstractPresentationModel
{
    private Context context;
    private MealModule mealModule;
    private DateTime today;
    private DateTime displayingDay;

    private List<Meal> meals;

    public MealPresentationModel()
    {
        this.context = App.getContext();
        this.mealModule = new MealModule();
        this.today = new DateTime();
        this.displayingDay = new DateTime();

        this.meals = new ArrayList<Meal>();

        // 일주일 급식 정보 다운로드
        meals = mealModule.GetMealOfWeek(today);
        presentationModelChangeSupport.firePropertyChange("meals");
    }

    @ItemPresentationModel(MealItemPresentationModel.class)
    public List<Meal> getMeals()
    {
        return meals;
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
        meals = mealModule.GetMealOfWeek(displayingDay);

        presentationModelChangeSupport.firePropertyChange("meals");
        presentationModelChangeSupport.firePropertyChange("timeString");
    }

    public void nextWeek()
    {
        displayingDay = displayingDay.plusWeeks(1);
        meals = mealModule.GetMealOfWeek(displayingDay);

        presentationModelChangeSupport.firePropertyChange("meals");
        presentationModelChangeSupport.firePropertyChange("timeString");
    }
}
