package in.suhj.banpo.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import org.robobinding.binder.Binders;

import in.suhj.banpo.Abstract.IRunnable;
import in.suhj.banpo.Infrastructure.Data.RegexManager;
import in.suhj.banpo.Infrastructure.Modules.MealModule;
import in.suhj.banpo.Infrastructure.Modules.ScheduleModule;
import in.suhj.banpo.PresentationModels.HomePresentationModel;
import in.suhj.banpo.PresentationModels.SettingsPresentationModel;
import in.suhj.banpo.R;


public class SettingsActivity extends ActionBarActivity
{
    private boolean busy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.busy = false;

        getSupportActionBar().setIcon(R.drawable.ic_action_settings);
        setContentView(R.layout.activity_settings);
    }

    public void updateRegex(View view)
    {
        if (busy) return;
        busy = true;

        // 로드 애니메이션
        final ImageView refreshBtn = (ImageView)findViewById(R.id.settings_refresh_button);

        Animation animation = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());

        refreshBtn.startAnimation(animation);

        // Regex Update 요청
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                RegexManager.Update(new IRunnable<String>()
                {
                    @Override
                    public void run(String param)
                    {
                        final String response = param;

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ImageView refreshBtn = (ImageView) findViewById(R.id.settings_refresh_button);
                                refreshBtn.clearAnimation();

                                Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                                toast.show();

                                busy = false;
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void deleteCache(View view)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // 급식 DB 삭제
                new MealModule().removeMealData();
                // 학사일정 DB 삭제
                new ScheduleModule().removeScheduleData();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "모든 데이터가 삭제되었습니다. 앱을 다시 시작해주세요.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        }).start();
    }
}
