package in.suhj.banpo.PresentationModels;

import android.content.Context;

import org.joda.time.DateTime;
import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.ItemPresentationModel;
import org.robobinding.presentationmodel.PresentationModel;

import java.util.ArrayList;
import java.util.List;

import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.App;
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Infrastructure.Modules.MealModule;

/**
 * Created by SuhJin on 2014-06-02.
 */

public class HomePresentationModel extends AbstractPresentationModel implements ITaskCompleted<List<Meal>>
{
    private Context context;
    private MealModule mealModule;

    private List<Meal> todayMeals;

    public HomePresentationModel()
    {
        this.context = App.getContext();
        this.mealModule = new MealModule(this);

        this.todayMeals = new ArrayList<Meal>();

        // 오늘 급식 정보 다운로드
        mealModule.GetMealOfDay(new DateTime());
    }

    @ItemPresentationModel(MealPresentationModel.class)
    public List<Meal> getTodayMeals()
    {
        return todayMeals;
    }

    public void OnTaskCompleted(List<Meal> result)
    {
        todayMeals.clear();

        for (Meal meal : result)
        {
            todayMeals.add(meal);
        }

        presentationModelChangeSupport.firePropertyChange("todayMeals");
    }
}
