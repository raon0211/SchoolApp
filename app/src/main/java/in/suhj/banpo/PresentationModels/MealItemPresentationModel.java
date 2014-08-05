package in.suhj.banpo.PresentationModels;

import org.robobinding.itempresentationmodel.ItemPresentationModel;
import org.robobinding.presentationmodel.PresentationModel;

import in.suhj.banpo.Models.Meal;

/**
 * Created by SuhJin on 2014-06-08.
 */
@PresentationModel
public class MealItemPresentationModel implements ItemPresentationModel<Meal>
{
    private Meal meal;

    public String getLunchContent()
    {
        return meal.GetLunchContent();
    }

    public String getDinnerContent()
    {
        return meal.GetDinnerContent();
    }

    public String getDateString()
    {
        return meal.GetDateString();
    }

    public void updateData(int index, Meal meal)
    {
        this.meal = meal;
    }
}
