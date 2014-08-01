package in.suhj.banpo.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.robobinding.binder.Binders;

import in.suhj.banpo.PresentationModels.HomePresentationModel;
import in.suhj.banpo.PresentationModels.SettingsPresentationModel;
import in.suhj.banpo.R;


public class SettingsActivity extends ActionBarActivity
{
    private SettingsPresentationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new SettingsPresentationModel();
        Binders.bind(this, R.layout.activity_settings, model);
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
