package in.suhj.banpo.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.robobinding.binder.Binders;

import in.suhj.banpo.PresentationModels.MealPresentationModel;
import in.suhj.banpo.PresentationModels.SettingsPresentationModel;
import in.suhj.banpo.R;

/**
 * Created by SuhJin on 2014-08-02.
 */
public class MealActivity extends ActionBarActivity
{
    private MealPresentationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new MealPresentationModel();
        Binders.bind(this, R.layout.activity_meal, model);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
