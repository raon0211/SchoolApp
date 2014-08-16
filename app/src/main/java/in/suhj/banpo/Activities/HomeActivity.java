package in.suhj.banpo.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.robobinding.binder.Binders;

import in.suhj.banpo.PresentationModels.HomePresentationModel;
import in.suhj.banpo.R;


public class HomeActivity extends ActionBarActivity
{
    private HomePresentationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle(getResources().getString(R.string.home));

        model = new HomePresentationModel();
        Binders.bind(this, R.layout.activity_main, model);

        bindEvents();
    }

    private void bindEvents()
    {
        View mealMoreBtn = (View)findViewById(R.id.btn_main_navigate_meal);
        mealMoreBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), MealActivity.class));
            }
        });

        View scheduleMoreBtn = (View)findViewById(R.id.btn_main_navigate_schedule);
        scheduleMoreBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
