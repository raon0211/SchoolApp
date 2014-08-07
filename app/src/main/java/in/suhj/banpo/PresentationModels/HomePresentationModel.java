package in.suhj.banpo.PresentationModels;

import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;
import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.ItemPresentationModel;

import java.util.ArrayList;
import java.util.List;

import in.suhj.banpo.Activities.MealActivity;
import in.suhj.banpo.App;
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Infrastructure.Modules.MealModule;

/**
 * Created by SuhJin on 2014-06-02.
 */

public class HomePresentationModel extends AbstractPresentationModel
{
    private Context context;
    private MealModule mealModule;

    private List<Meal> todayMeals;

    public HomePresentationModel()
    {
        this.context = App.getContext();
        this.mealModule = new MealModule();

        this.todayMeals = new ArrayList<Meal>();

        // 오늘 급식 정보 다운로드
        todayMeals = mealModule.GetMealOfDay(new DateTime());
        presentationModelChangeSupport.firePropertyChange("todayMeals");
    }

    @ItemPresentationModel(MealItemPresentationModel.class)
    public List<Meal> getTodayMeals()
    {
        return todayMeals;
    }

    public void NavigateMeal()
    {
        context.startActivity(new Intent(context, MealActivity.class));
    }
}
