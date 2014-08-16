package in.suhj.banpo.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.suhj.banpo.Abstract.ITaskCompleted;
import in.suhj.banpo.Infrastructure.Controls.ScheduleCardItem;
import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.Infrastructure.Modules.ScheduleModule;
import in.suhj.banpo.Models.Schedule;
import in.suhj.banpo.R;

/**
 * Created by SuhJin on 2014-08-10.
 */
// RoboBinding이 nested List를 잘 지원하지 않기 때문에 어쩔 수 없다
public class ScheduleActivity extends ActionBarActivity implements ITaskCompleted<Boolean>
{
    private ExecutorService executorService;
    private ScheduleModule scheduleModule;

    private ArrayList<Schedule> schedules;
    private DateTime today;
    private DateTime displayingDay;

    // 핸들러 관련
    private final int SCHEDULE_UPDATE = 0;
    private Handler handler = new ScheduleHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.executorService = Executors.newFixedThreadPool(10);
        this.scheduleModule = new ScheduleModule();

        this.schedules = new ArrayList<>();
        this.today = new DateTime();
        this.displayingDay = new DateTime();

        setContentView(R.layout.activity_schedule);
        bindEvents();

        loadSchedules();
    }

    private void loadSchedules()
    {
        setTimeString();

        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                schedules = scheduleModule.GetScheduleOfWeek(displayingDay);
                handler.sendEmptyMessage(SCHEDULE_UPDATE);
            }
        });
    }

    private void bindEvents()
    {
        final TextView previousBtn = (TextView)findViewById(R.id.schedule_previous_btn);
        previousBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                previousWeek();
            }
        });

        final TextView nextBtn = (TextView)findViewById(R.id.schedule_next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                nextWeek();
            }
        });
    }

    private void previousWeek()
    {
        displayingDay = displayingDay.minusWeeks(1);
        loadSchedules();
    }

    private void nextWeek()
    {
        displayingDay = displayingDay.plusWeeks(1);
        loadSchedules();
    }

    private void setSchedules(ArrayList<Schedule> schedules)
    {
        LinearLayout layout = (LinearLayout)findViewById(R.id.schedule_list);
        layout.removeAllViews();

        for (Schedule schedule : schedules)
        {
            ScheduleCardItem item = new ScheduleCardItem(getApplicationContext());

            DateTime date = schedule.getDate();
            String dateString = date.getMonthOfYear() + ". " + date.getDayOfMonth() + " (" + DateHelper.GetDayName(date.getDayOfWeek()) + ")";
            item.setDateString(dateString);

            item.setSchedules(schedule.getSchedules());

            layout.addView(item);
        }
    }

    private void setTimeString()
    {
        String timeString = "";

        int weeksBetween = Weeks.weeksBetween(today.toLocalDate(), displayingDay.toLocalDate()).getWeeks();

        if (weeksBetween > 0)
        {
            timeString = weeksBetween + "주 후";
        }
        else if (weeksBetween == 0)
        {
            timeString = "이번 주";
        }
        else
        {
            timeString = -weeksBetween + "주 전";
        }

        TextView dateStringTB = (TextView)findViewById(R.id.schedule_date_string);
        dateStringTB.setText(timeString);
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

    public void OnTaskCompleted(Boolean result)
    {
        ListView view = (ListView)findViewById(R.id.scheduleListView);
        view.setSelectionAfterHeaderView();
    }

    class ScheduleHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SCHEDULE_UPDATE:
                    setSchedules(schedules);
                    break;
            }
        }
    }
}
