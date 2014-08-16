package in.suhj.banpo.PresentationModels;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.ItemPresentationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.App;
import in.suhj.banpo.Models.Meal;
import in.suhj.banpo.Infrastructure.Modules.MealModule;

/**
 * Created by SuhJin on 2014-08-02.
 */

public class MealPresentationModel extends AbstractPresentationModel
{
    private ExecutorService executorService;
    private ITaskCompleted<Boolean> listener;

    private Context context;
    private MealModule mealModule;
    private DateTime today;
    private DateTime displayingDay;

    private List<Meal> meals;

    // 핸들러 관련
    private final int MEAL_UPDATE = 0;
    private Handler handler = new MealHandler();

    public MealPresentationModel(ITaskCompleted<Boolean> listener)
    {
        this.executorService = Executors.newFixedThreadPool(10);
        this.listener = listener;
        this.context = App.getContext();
        this.mealModule = new MealModule();
        this.today = new DateTime();
        this.displayingDay = new DateTime();

        this.meals = new ArrayList<Meal>();

        // 일주일 급식 정보 다운로드
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                meals = mealModule.GetMealOfWeek(today);
                handler.sendEmptyMessage(MEAL_UPDATE);
            }
        });
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

        notifyListener(true);
    }

    public void nextWeek()
    {
        displayingDay = displayingDay.plusWeeks(1);
        meals = mealModule.GetMealOfWeek(displayingDay);

        presentationModelChangeSupport.firePropertyChange("meals");
        presentationModelChangeSupport.firePropertyChange("timeString");

        notifyListener(true);
    }

    private void notifyListener(boolean success)
    {
        listener.OnTaskCompleted(success);
    }

    // 급식 핸들러
    class MealHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MEAL_UPDATE:
                    presentationModelChangeSupport.firePropertyChange("meals");
                    break;
            }
        }
    }
}
