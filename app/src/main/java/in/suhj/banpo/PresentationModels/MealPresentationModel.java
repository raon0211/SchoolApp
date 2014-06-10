package in.suhj.banpo.PresentationModels;

import org.robobinding.itempresentationmodel.ItemPresentationModel;
import org.robobinding.presentationmodel.PresentationModel;

import in.suhj.banpo.Models.Meal;

/**
 * Created by SuhJin on 2014-06-08.
 */
@PresentationModel
public class MealPresentationModel implements ItemPresentationModel<Meal>
{
    private Meal meal;

    public int getIconId()
    {
        return meal.GetIconId();
    }

    public String getContent()
    {
        System.out.println(meal.GetContent());
        return meal.GetContent();
    }

    public void updateData(int index, Meal meal)
    {
        this.meal = meal;
    }
}
